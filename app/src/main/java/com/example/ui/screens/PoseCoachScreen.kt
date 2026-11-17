package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PanTool
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PoseCoachResult
import com.example.model.PoseCoachUiState
import com.example.model.PoseStyle
import com.example.ui.StudioUiState
import com.example.ui.components.PhotoSlotCard
import com.example.ui.components.PoseOverlayCanvas
import com.example.ui.components.StudioShimmerLoader
import kotlin.math.roundToInt

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
    var resultSectionY by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.poseCoachState) {
        if (!isWideScreen && (uiState.poseCoachState is PoseCoachUiState.Loading || uiState.poseCoachState is PoseCoachUiState.Success)) {
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        resultSectionY = coords.positionInParent().y.roundToInt()
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        PoseCoachResultSection(
                            uiState = uiState,
                            onGeneratePoses = onGeneratePoses,
                            onSelectConcept = onSelectConcept,
                            onSelectPersonMarker = onSelectPersonMarker,
                            onToggleOverlay = onToggleOverlay,
                            onSendToBlend = onSendToBlend
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PoseCoachHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "SCENE & BODY COACHING",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
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

@OptIn(ExperimentalLayoutApi::class)
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
    var showNotes by remember { mutableStateOf(uiState.poseExtraNotes.isNotBlank()) }

    PhotoSlotCard(
        title = stringResource(R.string.label_scene_background),
        badge = "01 SCENE",
        source = uiState.coachBackground,
        emptyHint = "Capture or choose your background location",
        contentDescription = stringResource(R.string.cd_background_preview),
        onTakePhoto = onTakeBackgroundPhoto,
        onPickPhoto = onPickBackgroundPhoto,
        onTrySample = onUseSampleBackground,
        onClear = onClearBackground,
        tagPrefix = "coach_bg"
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "02 SUBJECTS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.label_people_count),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = when (uiState.peopleCount) {
                            1 -> "Solo portrait focus"
                            2 -> "Duo / pair chemistry"
                            in 3..4 -> "Group triangle spacing"
                            else -> "Multi-tier group choreography"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Unified industrial segmented stepper [ - | 2 | + ]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                ) {
                    IconButton(
                        onClick = { onPeopleCountChange(uiState.peopleCount - 1) },
                        enabled = uiState.peopleCount > 1,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("decrease_people_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.cd_decrease_people),
                            tint = if (uiState.peopleCount > 1) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(22.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )

                    Box(
                        modifier = Modifier.width(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${uiState.peopleCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(22.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )

                    IconButton(
                        onClick = { onPeopleCountChange(uiState.peopleCount + 1) },
                        enabled = uiState.peopleCount < 10,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("increase_people_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.cd_increase_people),
                            tint = if (uiState.peopleCount < 10) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            }
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.label_pose_vibe),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PoseStyle.entries.forEach { style ->
                        val selected = uiState.selectedPoseStyle == style
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectStyle(style) },
                            label = { Text(style.label) },
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
                            modifier = Modifier.testTag("pose_style_${style.name.lowercase()}")
                        )
                    }
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
                    text = if (uiState.poseExtraNotes.isNotBlank()) {
                        "Notes: ${uiState.poseExtraNotes}"
                    } else {
                        "Add optional notes (outfit, props, mood)"
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
    }

    Button(
        onClick = onGeneratePoses,
        enabled = uiState.poseCoachState !is PoseCoachUiState.Loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
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
            tint = MaterialTheme.colorScheme.secondary,
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
                                imageVector = Icons.Rounded.AccessibilityNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Ready for scene analysis",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap Direct Poses for 3 tailored setups, hand & stance cues, and interactive placement pins.",
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

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SCENE SCOUT REPORT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = result.sceneRead,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TelemetryPill(
                    icon = Icons.Default.LightMode,
                    label = "LIGHTING",
                    value = result.lightingDirection,
                    modifier = Modifier.weight(1f)
                )
                TelemetryPill(
                    icon = Icons.Default.CenterFocusStrong,
                    label = "FOCAL ZONE",
                    value = result.bestSpot,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

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
                        text = "0${idx + 1} • ${item.title}",
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("pose_concept_tab_${idx + 1}")
            )
        }
    }

    val bgSource = uiState.coachBackground
    if (bgSource != null) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Placement Blueprint",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tap any numbered pin to inspect posture",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onToggleOverlay,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("toggle_pose_overlay_button")
                ) {
                    Icon(
                        imageVector = if (uiState.showSceneOverlay) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = stringResource(R.string.cd_toggle_overlay),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.showSceneOverlay) "Pins On" else "Pins Off",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
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

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = concept.vibeTag.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${concept.cameraAngle} • ${concept.framingTip}",
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

    if (concept.subjects.isNotEmpty()) {
        val selectedSubject = concept.subjects[activePersonIndex]
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f))
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
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = selectedSubject.postureTag.replace("_", " ").uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (concept.subjects.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        concept.subjects.forEachIndexed { idx, subj ->
                            val selected = idx == activePersonIndex
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectPersonMarker(idx) },
                                label = { Text("#${subj.personNumber} ${subj.roleLabel}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
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

                BodyCueRow(Icons.Default.Face, "Head & Gaze", selectedSubject.headAndGaze)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                BodyCueRow(Icons.Rounded.AccessibilityNew, "Shoulders & Torso", selectedSubject.shouldersAndTorso)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                BodyCueRow(Icons.Default.PanTool, "Arms & Hands", selectedSubject.armsAndHands)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                BodyCueRow(Icons.AutoMirrored.Filled.DirectionsWalk, "Legs & Weight Shift", selectedSubject.legsAndStance)
            }
        }
    }

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
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
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
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(17.dp)
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
