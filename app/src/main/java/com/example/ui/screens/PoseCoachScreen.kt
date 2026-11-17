package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PersonPlacement
import com.example.model.PoseCoachResult
import com.example.model.PoseCoachUiState
import com.example.model.PoseStyle
import com.example.ui.StudioUiState
import com.example.ui.components.PhotoSlotCard
import com.example.ui.components.PoseOverlayCanvas
import com.example.ui.components.StudioShimmerLoader

@Composable
fun PoseCoachScreen(
    uiState: StudioUiState,
    isWideScreen: Boolean,
    onTakeBackgroundPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onUseSampleBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onPeopleCountChange: (Int) -> Unit,
    onSelectStyle: (PoseStyle) -> Unit,
    onNotesChange: (String) -> Unit,
    onGeneratePoses: () -> Unit,
    onSelectConcept: (Int) -> Unit,
    onSelectPersonMarker: (Int) -> Unit,
    onToggleOverlay: () -> Unit,
    onSendToBlend: () -> Unit,
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
                PoseCoachHeader()
                PoseCoachInputs(
                    uiState = uiState,
                    onTakeBackgroundPhoto = onTakeBackgroundPhoto,
                    onPickBackgroundPhoto = onPickBackgroundPhoto,
                    onUseSampleBackground = onUseSampleBackground,
                    onClearBackground = onClearBackground,
                    onPeopleCountChange = onPeopleCountChange,
                    onSelectStyle = onSelectStyle,
                    onNotesChange = onNotesChange,
                    onGeneratePoses = onGeneratePoses
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PoseCoachResultSection(
                    uiState = uiState,
                    onGeneratePoses = onGeneratePoses,
                    onSelectConcept = onSelectConcept,
                    onSelectPersonMarker = onSelectPersonMarker,
                    onToggleOverlay = onToggleOverlay,
                    onSendToBlend = onSendToBlend
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
                PoseCoachHeader()
                PoseCoachInputs(
                    uiState = uiState,
                    onTakeBackgroundPhoto = onTakeBackgroundPhoto,
                    onPickBackgroundPhoto = onPickBackgroundPhoto,
                    onUseSampleBackground = onUseSampleBackground,
                    onClearBackground = onClearBackground,
                    onPeopleCountChange = onPeopleCountChange,
                    onSelectStyle = onSelectStyle,
                    onNotesChange = onNotesChange,
                    onGeneratePoses = onGeneratePoses
                )
                PoseCoachResultSection(
                    uiState = uiState,
                    onGeneratePoses = onGeneratePoses,
                    onSelectConcept = onSelectConcept,
                    onSelectPersonMarker = onSelectPersonMarker,
                    onToggleOverlay = onToggleOverlay,
                    onSendToBlend = onSendToBlend
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PoseCoachHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.pose_coach_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.pose_coach_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PoseCoachInputs(
    uiState: StudioUiState,
    onTakeBackgroundPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onUseSampleBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onPeopleCountChange: (Int) -> Unit,
    onSelectStyle: (PoseStyle) -> Unit,
    onNotesChange: (String) -> Unit,
    onGeneratePoses: () -> Unit
) {
    PhotoSlotCard(
        title = stringResource(R.string.label_scene_background),
        badge = "01 SCENE",
        source = uiState.coachBackground,
        emptyHint = "Snap or choose the background where your friend(s) will stand",
        contentDescription = stringResource(R.string.cd_background_preview),
        onTakePhoto = onTakeBackgroundPhoto,
        onPickPhoto = onPickBackgroundPhoto,
        onTrySample = onUseSampleBackground,
        onClear = onClearBackground,
        tagPrefix = "coach_bg"
    )

    // People Count Selector Card
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.label_people_count),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (uiState.peopleCount) {
                            1 -> "Solo portrait focus"
                            2 -> "Duo / pair chemistry"
                            in 3..4 -> "Small group triangle spacing"
                            else -> "Multi-tier group choreography"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { onPeopleCountChange(uiState.peopleCount - 1) },
                        enabled = uiState.peopleCount > 1,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("decrease_people_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.cd_decrease_people)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${uiState.peopleCount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { onPeopleCountChange(uiState.peopleCount + 1) },
                        enabled = uiState.peopleCount < 10,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("increase_people_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.cd_increase_people)
                        )
                    }
                }
            }

            // Quick headcount chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1 to "1 Solo", 2 to "2 Duo", 3 to "3 Trio", 4 to "4 Quartet", 6 to "6 Party").forEach { (num, label) ->
                    FilterChip(
                        selected = uiState.peopleCount == num,
                        onClick = { onPeopleCountChange(num) },
                        label = { Text(label) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("quick_count_$num")
                    )
                }
            }

            Text(
                text = stringResource(R.string.label_pose_vibe),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PoseStyle.entries.forEach { style ->
                    FilterChip(
                        selected = uiState.selectedPoseStyle == style,
                        onClick = { onSelectStyle(style) },
                        label = { Text(style.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("pose_style_${style.name.lowercase()}")
                    )
                }
            }

            OutlinedTextField(
                value = uiState.poseExtraNotes,
                onValueChange = onNotesChange,
                label = { Text(stringResource(R.string.label_extra_notes)) },
                placeholder = { Text(stringResource(R.string.hint_pose_notes)) },
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pose_notes_input")
            )
        }
    }

    Button(
        onClick = onGeneratePoses,
        enabled = uiState.poseCoachState !is PoseCoachUiState.Loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("direct_poses_button"),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.btn_generate_poses),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PoseCoachResultSection(
    uiState: StudioUiState,
    onGeneratePoses: () -> Unit,
    onSelectConcept: (Int) -> Unit,
    onSelectPersonMarker: (Int) -> Unit,
    onToggleOverlay: () -> Unit,
    onSendToBlend: () -> Unit
) {
    when (val state = uiState.poseCoachState) {
        is PoseCoachUiState.Idle -> {
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
                        imageVector = Icons.Rounded.AccessibilityNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Ready to scout your scene",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap Direct Poses to get 3 tailored body-figure setups, exact hand & stance cues, and interactive placement pins.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        is PoseCoachUiState.Loading -> {
            StudioShimmerLoader(
                headline = stringResource(R.string.loading_poses),
                subtext = "Building ${uiState.peopleCount}-person body-posing blueprints for your scene"
            )
        }

        is PoseCoachUiState.Error -> {
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
                            text = "Could not analyze poses",
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
                        onClick = onGeneratePoses,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier.testTag("pose_retry_button")
                    ) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }
        }

        is PoseCoachUiState.Success -> {
            PoseCoachSuccessView(
                result = state.result,
                uiState = uiState,
                onSelectConcept = onSelectConcept,
                onSelectPersonMarker = onSelectPersonMarker,
                onToggleOverlay = onToggleOverlay,
                onSendToBlend = onSendToBlend
            )
        }
    }
}

@Composable
private fun PoseCoachSuccessView(
    result: PoseCoachResult,
    uiState: StudioUiState,
    onSelectConcept: (Int) -> Unit,
    onSelectPersonMarker: (Int) -> Unit,
    onToggleOverlay: () -> Unit,
    onSendToBlend: () -> Unit
) {
    val activeConceptIndex = uiState.selectedConceptIndex.coerceIn(0, (result.concepts.size - 1).coerceAtLeast(0))
    val concept = result.concepts.getOrNull(activeConceptIndex) ?: return
    val activePersonIndex = uiState.selectedPersonIndex.coerceIn(0, (concept.subjects.size - 1).coerceAtLeast(0))

    // Scene Telemetry Readout Card
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SCENE SCOUT REPORT",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = result.sceneRead,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TelemetryPill(
                    icon = Icons.Default.LightMode,
                    label = "LIGHT",
                    value = result.lightingDirection,
                    modifier = Modifier.weight(1f)
                )
                TelemetryPill(
                    icon = Icons.Default.CenterFocusStrong,
                    label = "BEST SPOT",
                    value = result.bestSpot,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Concept Selector Strip
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        result.concepts.forEachIndexed { idx, item ->
            val selected = idx == activeConceptIndex
            FilterChip(
                selected = selected,
                onClick = { onSelectConcept(idx) },
                label = {
                    Text(
                        text = "${idx + 1}. ${item.title}",
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("pose_concept_tab_${idx + 1}")
            )
        }
    }

    // Interactive Placement Overlay on Scene Photo
    val bgSource = uiState.coachBackground
    if (bgSource != null) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Interactive Placement Blueprint",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tap any numbered marker to inspect their body pose",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleOverlay,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("toggle_pose_overlay_button")
                ) {
                    Icon(
                        imageVector = if (uiState.showSceneOverlay) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = stringResource(R.string.cd_toggle_overlay),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            PoseOverlayCanvas(
                source = bgSource,
                subjects = concept.subjects,
                selectedPersonIndex = activePersonIndex,
                showOverlay = uiState.showSceneOverlay,
                onSelectPerson = onSelectPersonMarker
            )
        }
    }

    // Concept Overview + Camera Setup
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = concept.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = concept.vibeTag.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = concept.whyItWorks,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Camera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Camera: ${concept.cameraAngle} • ${concept.framingTip}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Placement: ${concept.placementSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Per-Person Body Pose Breakdown
    if (concept.subjects.isNotEmpty()) {
        val selectedSubject = concept.subjects[activePersonIndex]
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BODY FIGURE BREAKDOWN",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = selectedSubject.postureTag.replace("_", " ").uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (concept.subjects.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        concept.subjects.forEachIndexed { idx, subj ->
                            FilterChip(
                                selected = idx == activePersonIndex,
                                onClick = { onSelectPersonMarker(idx) },
                                label = { Text("#${subj.personNumber} ${subj.roleLabel}") },
                                modifier = Modifier.testTag("subject_chip_${idx + 1}")
                            )
                        }
                    }
                } else {
                    Text(
                        text = "#${selectedSubject.personNumber} • ${selectedSubject.roleLabel}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                BodyCueRow(
                    icon = Icons.Default.Face,
                    part = "Head & Gaze",
                    cue = selectedSubject.headAndGaze
                )
                BodyCueRow(
                    icon = Icons.Rounded.AccessibilityNew,
                    part = "Shoulders & Torso",
                    cue = selectedSubject.shouldersAndTorso
                )
                BodyCueRow(
                    icon = Icons.Default.PanTool,
                    part = "Arms & Hands",
                    cue = selectedSubject.armsAndHands
                )
                BodyCueRow(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    part = "Legs & Weight Shift",
                    cue = selectedSubject.legsAndStance
                )
            }
        }
    }

    // Cross-feature bridge button to Feature 2!
    OutlinedButton(
        onClick = onSendToBlend,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("send_to_blend_button"),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.action_send_to_blend),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TelemetryPill(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BodyCueRow(
    icon: ImageVector,
    part: String,
    cue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = part,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = cue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
