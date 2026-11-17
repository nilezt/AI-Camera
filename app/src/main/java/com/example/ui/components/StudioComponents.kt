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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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
    val reticleColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.65f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
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

                if (source !is PhotoSource.SampleDrawable) {
                    TextButton(
                        onClick = onTrySample,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("${tagPrefix}_sample_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Load Sample",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .border(
                        width = 1.dp,
                        color = if (source != null) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        } else {
                            MaterialTheme.colorScheme.outline
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

                    // Floating clear button inside preview
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("${tagPrefix}_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.action_clear_photo),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Bottom-left status badge
                    Surface(
                        color = Color(0xFF14171F).copy(alpha = 0.78f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (source is PhotoSource.SampleDrawable) {
                                "SAMPLE • ${source.label.uppercase()}"
                            } else {
                                "FRAME LOADED"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    // Precision viewfinder corner reticles in empty state
                    Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        val len = 14.dp.toPx()
                        val stroke = 1.5.dp.toPx()
                        drawLine(reticleColor, Offset(0f, 0f), Offset(len, 0f), stroke)
                        drawLine(reticleColor, Offset(0f, 0f), Offset(0f, len), stroke)
                        drawLine(reticleColor, Offset(size.width, 0f), Offset(size.width - len, 0f), stroke)
                        drawLine(reticleColor, Offset(size.width, 0f), Offset(size.width, len), stroke)
                        drawLine(reticleColor, Offset(0f, size.height), Offset(len, size.height), stroke)
                        drawLine(reticleColor, Offset(0f, size.height), Offset(0f, size.height - len), stroke)
                        drawLine(reticleColor, Offset(size.width, size.height), Offset(size.width - len, size.height), stroke)
                        drawLine(reticleColor, Offset(size.width, size.height), Offset(size.width, size.height - len), stroke)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = emptyHint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = onTakePhoto,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Take Photo",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }

                FilledTonalButton(
                    onClick = onPickPhoto,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "From Gallery",
                        style = MaterialTheme.typography.labelLarge,
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
    val activeAccent = MaterialTheme.colorScheme.secondary
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
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
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStroke = 1.dp.toPx()
                val gridColor = Color.White.copy(alpha = 0.35f)
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                drawLine(gridColor, Offset(size.width / 3f, 0f), Offset(size.width / 3f, size.height), gridStroke, pathEffect = dashEffect)
                drawLine(gridColor, Offset(size.width * 2f / 3f, 0f), Offset(size.width * 2f / 3f, size.height), gridStroke, pathEffect = dashEffect)
                drawLine(gridColor, Offset(0f, size.height / 3f), Offset(size.width, size.height / 3f), gridStroke, pathEffect = dashEffect)
                drawLine(gridColor, Offset(0f, size.height * 2f / 3f), Offset(size.width, size.height * 2f / 3f), gridStroke, pathEffect = dashEffect)

                if (subjects.size > 1) {
                    val points = subjects.map {
                        Offset(it.xNorm * size.width, it.yNorm * size.height)
                    }
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = activeAccent.copy(alpha = 0.9f),
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    }
                }

                subjects.forEachIndexed { idx, subject ->
                    val center = Offset(subject.xNorm * size.width, subject.yNorm * size.height)
                    val isSelected = idx == selectedPersonIndex
                    drawCircle(
                        color = (if (isSelected) activeAccent else primaryColor).copy(alpha = 0.24f),
                        radius = if (isSelected) 34.dp.toPx() else 26.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = if (isSelected) activeAccent else Color.White.copy(alpha = 0.9f),
                        radius = if (isSelected) 34.dp.toPx() else 26.dp.toPx(),
                        center = center,
                        style = Stroke(width = if (isSelected) 2.5.dp.toPx() else 1.5.dp.toPx())
                    )
                }
            }

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
                            if (isSelected) activeAccent else surfaceColor.copy(alpha = 0.95f)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color.White else activeAccent,
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
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
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
    var splitFraction by remember { mutableFloatStateOf(0.50f) }
    val accent = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
                            splitFraction = newFraction.coerceIn(0.02f, 0.98f)
                        }
                    }
                }
                .testTag("before_after_viewer")
        ) {
            val totalWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
            val dividerXPx = totalWidthPx * splitFraction

            Image(
                bitmap = afterBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.cd_blended_result),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (beforeBitmap != null) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) {
                    clipRect(left = 0f, top = 0f, right = dividerXPx, bottom = size.height) {
                        drawImage(
                            image = beforeBitmap.asImageBitmap(),
                            dstSize = IntSize(
                                size.width.roundToInt(),
                                size.height.roundToInt()
                            )
                        )
                    }
                    drawLine(
                        color = Color.White,
                        start = Offset(dividerXPx, 0f),
                        end = Offset(dividerXPx, size.height),
                        strokeWidth = 2.5.dp.toPx()
                    )
                }

                Surface(
                    color = Color(0xFF14171F).copy(alpha = 0.78f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "ORIGINAL SCENE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = accent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "AI COMPOSITE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                val handleOffset = (dividerXPx - with(LocalDensity.current) { 19.dp.toPx() }).roundToInt()
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(2.dp, accent),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset { IntOffset(handleOffset, 0) }
                        .size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (beforeBitmap != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0.96f to "Original",
                    0.50f to "50 / 50 Split",
                    0.04f to "Composite"
                ).forEach { (target, label) ->
                    val isNear = kotlin.math.abs(splitFraction - target) < 0.08f
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clickable { splitFraction = target },
                        shape = MaterialTheme.shapes.small,
                        color = if (isNear) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = if (!isNear) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isNear) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
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

    val accent = MaterialTheme.colorScheme.secondary
    val surface = MaterialTheme.colorScheme.surface
    val surfaceHigh = MaterialTheme.colorScheme.surfaceContainerHigh

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(surface, surfaceHigh, accent.copy(alpha = 0.12f), surfaceHigh, surface),
                        start = Offset(progress * 900f - 300f, 0f),
                        end = Offset(progress * 900f + 300f, 500f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                val len = 20.dp.toPx()
                val stroke = 2.dp.toPx()
                val bracketColor = accent.copy(alpha = 0.75f)
                drawLine(bracketColor, Offset(0f, 0f), Offset(len, 0f), stroke)
                drawLine(bracketColor, Offset(0f, 0f), Offset(0f, len), stroke)
                drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width - len, 0f), stroke)
                drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, len), stroke)
                drawLine(bracketColor, Offset(0f, size.height), Offset(len, size.height), stroke)
                drawLine(bracketColor, Offset(0f, size.height), Offset(0f, size.height - len), stroke)
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
                    tint = accent,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
