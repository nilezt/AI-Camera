package com.example.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.model.PhotoSource
import com.example.model.SavedCreation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "ImageStorageHelper"
private const val CREATIONS_DIR = "studio_creations"
private const val CAMERA_CACHE_DIR = "camera_captures"
private const val INDEX_FILE = "creations_index.json"
private const val MAX_AI_IMAGE_DIM = 1280

class ImageStorageHelper(private val context: Context) {

    fun createCameraCaptureUri(): Uri {
        val cacheFolder = File(context.cacheDir, CAMERA_CACHE_DIR).apply { mkdirs() }
        val file = File(cacheFolder, "capture_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    suspend fun loadBitmapForAi(source: PhotoSource): Bitmap? = withContext(Dispatchers.IO) {
        when (source) {
            is PhotoSource.SampleDrawable -> {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeResource(context.resources, source.resId, options)
                val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight, MAX_AI_IMAGE_DIM)
                val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                val raw = BitmapFactory.decodeResource(context.resources, source.resId, decodeOpts)
                    ?: return@withContext null
                scaleDownIfNeeded(raw, MAX_AI_IMAGE_DIM)
            }
            is PhotoSource.UriSource -> {
                try {
                    val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(source.uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, boundsOpts)
                    }
                    val sampleSize = calculateInSampleSize(boundsOpts.outWidth, boundsOpts.outHeight, MAX_AI_IMAGE_DIM)
                    val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                    val raw = context.contentResolver.openInputStream(source.uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, decodeOpts)
                    } ?: return@withContext null

                    val oriented = applyExifOrientation(source.uri, raw)
                    scaleDownIfNeeded(oriented, MAX_AI_IMAGE_DIM)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load bitmap from URI: ${source.uri}", e)
                    null
                }
            }
        }
    }

    suspend fun saveCreationToVault(
        bitmap: Bitmap,
        title: String,
        subtitle: String
    ): SavedCreation = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, CREATIONS_DIR).apply { mkdirs() }
        val id = UUID.randomUUID().toString()
        val file = File(dir, "posecraft_$id.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        val item = SavedCreation(
            id = id,
            title = title,
            subtitle = subtitle,
            filePath = file.absolutePath,
            createdAtMillis = System.currentTimeMillis()
        )
        val current = loadVaultIndexInternal().toMutableList()
        current.add(0, item)
        saveVaultIndexInternal(current)
        item
    }

    suspend fun loadVaultCreations(): List<SavedCreation> = withContext(Dispatchers.IO) {
        loadVaultIndexInternal().filter { File(it.filePath).exists() }
    }

    suspend fun deleteCreation(id: String): List<SavedCreation> = withContext(Dispatchers.IO) {
        val current = loadVaultIndexInternal().toMutableList()
        val target = current.find { it.id == id }
        if (target != null) {
            File(target.filePath).delete()
            current.removeAll { it.id == id }
            saveVaultIndexInternal(current)
        }
        current.filter { File(it.filePath).exists() }
    }

    suspend fun exportToDeviceGallery(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val filename = "PoseCraft_$timestamp.jpg"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/PoseCraft"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return@withContext Result.failure(IllegalStateException("Could not create MediaStore entry."))

                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                Result.success("Saved to Pictures/PoseCraft")
            } else {
                // Pre-Android 10: save in app-specific external pictures or prompt share without requesting legacy storage permissions
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir,
                    filename
                )
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                Result.success("Saved to app Pictures folder")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export image", e)
            Result.failure(e)
        }
    }

    fun buildShareIntent(bitmap: Bitmap): Intent {
        val dir = File(context.filesDir, CREATIONS_DIR).apply { mkdirs() }
        val shareFile = File(dir, "share_preview.jpg")
        FileOutputStream(shareFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun buildShareIntentForFile(filePath: String): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(filePath)
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun loadVaultIndexInternal(): List<SavedCreation> {
        val indexFile = File(context.filesDir, INDEX_FILE)
        if (!indexFile.exists()) return emptyList()
        return try {
            val array = JSONArray(indexFile.readText())
            (0 until array.length()).mapNotNull { idx ->
                val obj = array.optJSONObject(idx) ?: return@mapNotNull null
                SavedCreation(
                    id = obj.optString("id"),
                    title = obj.optString("title"),
                    subtitle = obj.optString("subtitle"),
                    filePath = obj.optString("filePath"),
                    createdAtMillis = obj.optLong("createdAtMillis", 0L)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed reading vault index", e)
            emptyList()
        }
    }

    private fun saveVaultIndexInternal(items: List<SavedCreation>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("subtitle", item.subtitle)
                    put("filePath", item.filePath)
                    put("createdAtMillis", item.createdAtMillis)
                }
            )
        }
        File(context.filesDir, INDEX_FILE).writeText(array.toString())
    }

    private fun applyExifOrientation(uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val orientation = context.contentResolver.openInputStream(uri)?.use { stream ->
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) bitmap.recycle()
            rotated
        } catch (e: Exception) {
            Log.w(TAG, "Could not read EXIF orientation", e)
            bitmap
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxDim: Int): Int {
        var inSampleSize = 1
        if (width > maxDim || height > maxDim) {
            var halfW = width / 2
            var halfH = height / 2
            while ((halfW / inSampleSize) >= maxDim && (halfH / inSampleSize) >= maxDim) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }

    private fun scaleDownIfNeeded(bitmap: Bitmap, maxDim: Int): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= maxDim) return bitmap
        val ratio = maxDim.toFloat() / longest.toFloat()
        val newW = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val newH = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        if (scaled != bitmap) bitmap.recycle()
        return scaled
    }
}
