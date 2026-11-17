package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    if (isWideScreen) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                PhotoBlendOutputSection(
                    uiState = uiState,
                    onBlendPhotos = onBlendPhotos,
                    onSaveToDevice = onSaveToDevice,
                    onShareBitmap = onShareBitmap
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PhotoBlendHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
    PhotoSlotCard(
        title = stringResource(R.string.label_scene_background),
        badge = "01 SCENE",
        source = uiState.blendBackground,
        emptyHint = "Take or choose your destination background photo",
        contentDescription = stringResource(R.string.cd_background_preview),
        onTakePhoto = onTakeBackgroundPhoto,
        onPickPhoto = onPickBackgroundPhoto,
        onTrySample = onUseSampleBackground,
        onClear = onClearBackground,
        tagPrefix = "blend_bg"
    )

    PhotoSlotCard(
        title = stringResource(R.string.label_subjects_photo),
        badge = "02 SUBJECTS",
        source = uiState.blendSubject,
        emptyHint = "Take or choose a photo of 1 person or a group of friends",
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.label_lighting_style),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LightingPreset.entries.forEach { preset ->
                    FilterChip(
                        selected = uiState.selectedLighting == preset,
                        onClick = { onSelectLighting(preset) },
                        label = { Text(preset.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("lighting_${preset.name.lowercase()}")
                    )
                }
            }

            Text(
                text = stringResource(R.string.label_framing_style),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FramingPreference.entries.forEach { framing ->
                    FilterChip(
                        selected = uiState.selectedFraming == framing,
                        onClick = { onSelectFraming(framing) },
                        label = { Text(framing.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.testTag("framing_${framing.name.lowercase()}")
                    )
                }
            }

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

    Button(
        onClick = onBlendPhotos,
        enabled = uiState.photoBlendState !is PhotoBlendUiState.Loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
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
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Lighting-matched compositor",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pick a Scene + Subject photo (or tap Sample on both!) then tap Composite Photo to fuse them with realistic shadows and color grading.",
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
                subtext = "Gemini Nano Banana is matching perspective, ambient color, and contact shadows"
            )
        }

        is PhotoBlendUiState.Error -> {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
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
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Archived in Studio Vault",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
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
