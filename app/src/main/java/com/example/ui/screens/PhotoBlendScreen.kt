package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FramingPreference
import com.example.model.LightingPreset
import com.example.model.PhotoBlendUiState
import com.example.ui.StudioUiState
import com.example.ui.components.BeforeAfterSplitViewer
import com.example.ui.components.PhotoSlotCard
import com.example.ui.components.StudioShimmerLoader
import kotlin.math.roundToInt

@Composable
fun PhotoBlendScreen(
    uiState: StudioUiState,
    isWideScreen: Boolean,
    onTakeBackgroundPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onUseSampleBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onTakeSubjectPhoto: () -> Unit,
    onPickSubjectPhoto: () -> Unit,
    onUseSampleSubjects: () -> Unit,
    onClearSubject: () -> Unit,
    onSelectLighting: (LightingPreset) -> Unit,
    onSelectFraming: (FramingPreference) -> Unit,
    onNotesChange: (String) -> Unit,
    onBlendPhotos: () -> Unit,
    onSaveToDevice: (android.graphics.Bitmap) -> Unit,
    onShareBitmap: (android.graphics.Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var resultSectionY by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.photoBlendState) {
        if (!isWideScreen && (uiState.photoBlendState is PhotoBlendUiState.Loading || uiState.photoBlendState is PhotoBlendUiState.Success)) {
            if (resultSectionY > 0) {
                scrollState.animateScrollTo(resultSectionY)
            }
        }
    }

    if (isWideScreen) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PhotoBlendHeader()
                PhotoBlendInputs(
                    uiState = uiState,
                    onTakeBackgroundPhoto = onTakeBackgroundPhoto,
                    onPickBackgroundPhoto = onPickBackgroundPhoto,
                    onUseSampleBackground = onUseSampleBackground,
                    onClearBackground = onClearBackground,
                    onTakeSubjectPhoto = onTakeSubjectPhoto,
                    onPickSubjectPhoto = onPickSubjectPhoto,
                    onUseSampleSubjects = onUseSampleSubjects,
                    onClearSubject = onClearSubject,
                    onSelectLighting = onSelectLighting,
                    onSelectFraming = onSelectFraming,
                    onNotesChange = onNotesChange,
                    onBlendPhotos = onBlendPhotos
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PhotoBlendOutputSection(
                    uiState = uiState,
                    onBlendPhotos = onBlendPhotos,
                    onSaveToDevice = onSaveToDevice,
                    onShareBitmap = onShareBitmap
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PhotoBlendHeader()
                PhotoBlendInputs(
                    uiState = uiState,
                    onTakeBackgroundPhoto = onTakeBackgroundPhoto,
                    onPickBackgroundPhoto = onPickBackgroundPhoto,
                    onUseSampleBackground = onUseSampleBackground,
                    onClearBackground = onClearBackground,
                    onTakeSubjectPhoto = onTakeSubjectPhoto,
                    onPickSubjectPhoto = onPickSubjectPhoto,
                    onUseSampleSubjects = onUseSampleSubjects,
                    onClearSubject = onClearSubject,
                    onSelectLighting = onSelectLighting,
                    onSelectFraming = onSelectFraming,
                    onNotesChange = onNotesChange,
                    onBlendPhotos = onBlendPhotos
                )
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        resultSectionY = coords.positionInParent().y.roundToInt()
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        PhotoBlendOutputSection(
                            uiState = uiState,
                            onBlendPhotos = onBlendPhotos,
                            onSaveToDevice = onSaveToDevice,
                            onShareBitmap = onShareBitmap
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PhotoBlendHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "LIGHTING & PERSPECTIVE COMPOSITOR",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(R.string.photo_blend_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.photo_blend_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhotoBlendInputs(
    uiState: StudioUiState,
    onTakeBackgroundPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onUseSampleBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onTakeSubjectPhoto: () -> Unit,
    onPickSubjectPhoto: () -> Unit,
    onUseSampleSubjects: () -> Unit,
    onClearSubject: () -> Unit,
    onSelectLighting: (LightingPreset) -> Unit,
    onSelectFraming: (FramingPreference) -> Unit,
    onNotesChange: (String) -> Unit,
    onBlendPhotos: () -> Unit
) {
    var showNotes by remember { mutableStateOf(uiState.blendCustomNotes.isNotBlank()) }

    PhotoSlotCard(
        title = stringResource(R.string.label_scene_background),
        badge = "01 SCENE",
        source = uiState.blendBackground,
        emptyHint = "Capture or select the target background",
        contentDescription = stringResource(R.string.cd_background_preview),
        onTakePhoto = onTakeBackgroundPhoto,
        onPickPhoto = onPickBackgroundPhoto,
        onTrySample = onUseSampleBackground,
        onClear = onClearBackground,
        tagPrefix = "blend_bg"
    )

    // Clean visual merge connector between Scene and Subject
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline
        )
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "COMBINE WITH",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline
        )
    }

    PhotoSlotCard(
        title = stringResource(R.string.label_subjects_photo),
        badge = "02 SUBJECTS",
        source = uiState.blendSubject,
        emptyHint = "Capture or select 1 person or a group",
        contentDescription = stringResource(R.string.cd_subject_preview),
        onTakePhoto = onTakeSubjectPhoto,
        onPickPhoto = onPickSubjectPhoto,
        onTrySample = onUseSampleSubjects,
        onClear = onClearSubject,
        tagPrefix = "blend_subj"
    )

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "03 FINISH",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.label_lighting_style),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LightingPreset.entries.forEach { preset ->
                    val selected = uiState.selectedLighting == preset
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectLighting(preset) },
                        label = { Text(preset.label) },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("lighting_${preset.name.lowercase()}")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Text(
                text = stringResource(R.string.label_framing_style),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FramingPreference.entries.forEach { framing ->
                    val selected = uiState.selectedFraming == framing
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectFraming(framing) },
                        label = { Text(framing.label) },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("framing_${framing.name.lowercase()}")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNotes = !showNotes }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.blendCustomNotes.isNotBlank()) {
                        "Placement note: ${uiState.blendCustomNotes}"
                    } else {
                        "Add optional placement note"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (showNotes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            AnimatedVisibility(visible = showNotes) {
                OutlinedTextField(
                    value = uiState.blendCustomNotes,
                    onValueChange = onNotesChange,
                    label = { Text(stringResource(R.string.label_blend_direction)) },
                    placeholder = { Text(stringResource(R.string.hint_blend_direction)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("blend_notes_input")
                )
            }
        }
    }

    Button(
        onClick = onBlendPhotos,
        enabled = uiState.photoBlendState !is PhotoBlendUiState.Loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("composite_photo_button"),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            imageVector = Icons.Default.AutoFixHigh,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.btn_blend_photos),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PhotoBlendOutputSection(
    uiState: StudioUiState,
    onBlendPhotos: () -> Unit,
    onSaveToDevice: (android.graphics.Bitmap) -> Unit,
    onShareBitmap: (android.graphics.Bitmap) -> Unit
) {
    when (val state = uiState.photoBlendState) {
        is PhotoBlendUiState.Idle -> {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Lighting-matched compositor",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Load a Scene + Subject photo (or tap Load Sample on both), then tap Composite Photo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        is PhotoBlendUiState.Loading -> {
            StudioShimmerLoader(
                headline = stringResource(R.string.loading_blend),
                subtext = "Matching perspective, ambient light, and grounded shadows"
            )
        }

        is PhotoBlendUiState.Error -> {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Compositing failed",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Button(
                        onClick = onBlendPhotos,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier.testTag("blend_retry_button")
                    ) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }
        }

        is PhotoBlendUiState.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDIO COMPOSITE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "ARCHIVED IN VAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                BeforeAfterSplitViewer(
                    beforeBitmap = uiState.blendBackgroundPreviewBitmap,
                    afterBitmap = state.blendedBitmap
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onSaveToDevice(state.blendedBitmap) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_blend_button"),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.action_save_device))
                    }

                    OutlinedButton(
                        onClick = { onShareBitmap(state.blendedBitmap) },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("share_blend_button"),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.action_share))
                    }

                    OutlinedButton(
                        onClick = onBlendPhotos,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("regenerate_blend_button"),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate composite"
                        )
                    }
                }
            }
        }
    }
}
