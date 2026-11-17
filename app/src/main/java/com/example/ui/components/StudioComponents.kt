package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.PersonPlacement
import com.example.model.PhotoSource
import kotlin.math.roundToInt

@Composable
fun PhotoSourceRenderer(
    source: PhotoSource,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    when (source) {
        is PhotoSource.UriSource -> {
            AsyncImage(
                model = source.uri,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        is PhotoSource.SampleDrawable -> {
            Image(
                painter = painterResource(id = source.resId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}

@Composable
fun PhotoSlotCard(
    title: String,
    badge: String,
    source: PhotoSource?,
    emptyHint: String,
    contentDescription: String,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit,
    onTrySample: () -> Unit,
    onClear: () -> Unit,
    tagPrefix: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (source != null) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("${tagPrefix}_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.action_clear_photo),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .border(
                        width = 1.dp,
                        color = if (source != null) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onPickPhoto() }
                    .testTag("${tagPrefix}_preview_box"),
                contentAlignment = Alignment.Center
            ) {
                if (source != null) {
                    PhotoSourceRenderer(
                        source = source,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (source is PhotoSource.SampleDrawable) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "SAMPLE • ${source.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_studio_hero),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.25f
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = emptyHint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onTakePhoto,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("${tagPrefix}_camera_button"),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_take_photo),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                FilledTonalButton(
                    onClick = onPickPhoto,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("${tagPrefix}_gallery_button"),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_pick_photo),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onTrySample,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("${tagPrefix}_sample_button"),
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sample",
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun PoseOverlayCanvas(
    source: PhotoSource,
    subjects: List<PersonPlacement>,
    selectedPersonIndex: Int,
    showOverlay: Boolean,
    onSelectPerson: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceColor = MaterialTheme.colorScheme.surface

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
    ) {
        val boxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val boxHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

        PhotoSourceRenderer(
            source = source,
            contentDescription = stringResource(R.string.cd_background_preview),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (showOverlay) {
            // Viewfinder Rule-of-Thirds Grid & Staggered Formation Connector
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStroke = 1.dp.toPx()
                val gridColor = Color.White.copy(alpha = 0.25f)
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                drawLine(
                    color = gridColor,
                    start = Offset(size.width / 3f, 0f),
                    end = Offset(size.width / 3f, size.height),
                    strokeWidth = gridStroke,
                    pathEffect = dashEffect
                )
                drawLine(
                    color = gridColor,
                    start = Offset(size.width * 2f / 3f, 0f),
                    end = Offset(size.width * 2f / 3f, size.height),
                    strokeWidth = gridStroke,
                    pathEffect = dashEffect
                )
                drawLine(
                    color = gridColor,
                    start = Offset(0f, size.height / 3f),
                    end = Offset(size.width, size.height / 3f),
                    strokeWidth = gridStroke,
                    pathEffect = dashEffect
                )
                drawLine(
                    color = gridColor,
                    start = Offset(0f, size.height * 2f / 3f),
                    end = Offset(size.width, size.height * 2f / 3f),
                    strokeWidth = gridStroke,
                    pathEffect = dashEffect
                )

                // Draw formation connection lines for groups
                if (subjects.size > 1) {
                    val points = subjects.map {
                        Offset(it.xNorm * size.width, it.yNorm * size.height)
                    }
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = primaryColor.copy(alpha = 0.7f),
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    }
                }

                // Draw subtle stylized pose silhouette ring at each placement
                subjects.forEachIndexed { idx, subject ->
                    val center = Offset(subject.xNorm * size.width, subject.yNorm * size.height)
                    val isSelected = idx == selectedPersonIndex
                    drawCircle(
                        color = (if (isSelected) primaryColor else secondaryColor).copy(alpha = 0.22f),
                        radius = if (isSelected) 34.dp.toPx() else 26.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = if (isSelected) primaryColor else Color.White.copy(alpha = 0.85f),
                        radius = if (isSelected) 34.dp.toPx() else 26.dp.toPx(),
                        center = center,
                        style = Stroke(width = if (isSelected) 2.5.dp.toPx() else 1.5.dp.toPx())
                    )
                }
            }

            // Interactive numbered person chips positioned at normalized coordinates
            val chipHalfPx = with(LocalDensity.current) { 24.dp.toPx() }
            subjects.forEachIndexed { idx, subject ->
                val isSelected = idx == selectedPersonIndex
                val xPx = (subject.xNorm * boxWidthPx - chipHalfPx).roundToInt()
                val yPx = (subject.yNorm * boxHeightPx - chipHalfPx).roundToInt()

                Box(
                    modifier = Modifier
                        .offset { IntOffset(xPx, yPx) }
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) primaryColor else surfaceColor.copy(alpha = 0.92f)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color.White else primaryColor,
                            shape = CircleShape
                        )
                        .clickable { onSelectPerson(idx) }
                        .testTag("pose_marker_${idx + 1}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${subject.personNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) onPrimaryColor else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BeforeAfterSplitViewer(
    beforeBitmap: Bitmap?,
    afterBitmap: Bitmap,
    modifier: Modifier = Modifier
) {
    var splitFraction by remember { mutableFloatStateOf(0.55f) }
    val accent = MaterialTheme.colorScheme.primary

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    (afterBitmap.width.toFloat() / afterBitmap.height.toFloat()).coerceIn(0.65f, 1.5f)
                )
                .clip(MaterialTheme.shapes.large)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
                .pointerInput(beforeBitmap) {
                    if (beforeBitmap != null) {
                        detectHorizontalDragGestures { change, _ ->
                            change.consume()
                            val newFraction = (change.position.x / size.width.toFloat())
                            splitFraction = newFraction.coerceIn(0.05f, 0.95f)
                        }
                    }
                }
                .testTag("before_after_viewer")
        ) {
            val totalWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
            val dividerXPx = totalWidthPx * splitFraction

            // Base layer: Final AI Blended photo
            Image(
                bitmap = afterBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.cd_blended_result),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (beforeBitmap != null) {
                // Clipped left layer: Original Background scene
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) {
                    clipRect(left = 0f, top = 0f, right = dividerXPx, bottom = size.height) {
                        drawImage(
                            image = beforeBitmap.asImageBitmap(),
                            dstSize = androidx.compose.ui.unit.IntSize(
                                size.width.roundToInt(),
                                size.height.roundToInt()
                            )
                        )
                    }
                    // Vertical split line
                    drawLine(
                        color = accent,
                        start = Offset(dividerXPx, 0f),
                        end = Offset(dividerXPx, size.height),
                        strokeWidth = 2.5.dp.toPx()
                    )
                }

                // Labels
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "SCENE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = accent.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "AI BLEND",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Draggable handle pill
                val handleOffset = (dividerXPx - with(LocalDensity.current) { 20.dp.toPx() }).roundToInt()
                Surface(
                    shape = CircleShape,
                    color = accent,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset { IntOffset(handleOffset, 0) }
                        .size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (beforeBitmap != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Drag across image to compare original background vs AI composite",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun StudioShimmerLoader(
    headline: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "studio_shimmer")
    val progress by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_sweep"
    )

    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val surfaceHigh = MaterialTheme.colorScheme.surfaceContainerHigh

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = surface),
        border = BorderStroke(1.dp, primary.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(surface, surfaceHigh, primary.copy(alpha = 0.16f), surfaceHigh, surface),
                        start = Offset(progress * 900f - 300f, 0f),
                        end = Offset(progress * 900f + 300f, 500f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Viewfinder corner brackets
            Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                val len = 22.dp.toPx()
                val stroke = 2.dp.toPx()
                val bracketColor = primary.copy(alpha = 0.7f)
                // Top-left
                drawLine(bracketColor, Offset(0f, 0f), Offset(len, 0f), stroke)
                drawLine(bracketColor, Offset(0f, 0f), Offset(0f, len), stroke)
                // Top-right
                drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width - len, 0f), stroke)
                drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, len), stroke)
                // Bottom-left
                drawLine(bracketColor, Offset(0f, size.height), Offset(len, size.height), stroke)
                drawLine(bracketColor, Offset(0f, size.height), Offset(0f, size.height - len), stroke)
                // Bottom-right
                drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width - len, size.height), stroke)
                drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width, size.height - len), stroke)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
