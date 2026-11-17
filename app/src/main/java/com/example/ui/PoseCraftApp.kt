package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.PhotoSource
import com.example.ui.screens.PhotoBlendScreen
import com.example.ui.screens.PoseCoachScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.PoseCraftTheme

private enum class ImageTargetSlot {
    COACH_BG,
    BLEND_BG,
    BLEND_SUBJECT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoseCraftApp(
    viewModel: StudioViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingSlot by remember { mutableStateOf(ImageTargetSlot.COACH_BG) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val source = PhotoSource.UriSource(uri)
            when (pendingSlot) {
                ImageTargetSlot.COACH_BG -> viewModel.setCoachBackground(source)
                ImageTargetSlot.BLEND_BG -> viewModel.setBlendBackground(source)
                ImageTargetSlot.BLEND_SUBJECT -> viewModel.setBlendSubject(source)
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val capturedUri = pendingCameraUri
        if (success && capturedUri != null) {
            val source = PhotoSource.UriSource(capturedUri)
            when (pendingSlot) {
                ImageTargetSlot.COACH_BG -> viewModel.setCoachBackground(source)
                ImageTargetSlot.BLEND_BG -> viewModel.setBlendBackground(source)
                ImageTargetSlot.BLEND_SUBJECT -> viewModel.setBlendSubject(source)
            }
        }
    }

    fun launchCamera(slot: ImageTargetSlot) {
        pendingSlot = slot
        val uri = viewModel.createCameraUri()
        pendingCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    fun launchGallery(slot: ImageTargetSlot) {
        pendingSlot = slot
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    BackHandler(enabled = uiState.activeTab != StudioTab.POSE_COACH) {
        viewModel.selectTab(StudioTab.POSE_COACH)
    }

    PoseCraftTheme(darkTheme = uiState.isDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 680.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_launcher_art),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(7.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = stringResource(R.string.app_name),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = viewModel::toggleTheme,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Switch studio theme",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBarItem(
                                selected = uiState.activeTab == StudioTab.POSE_COACH,
                                onClick = { viewModel.selectTab(StudioTab.POSE_COACH) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.AccessibilityNew,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(stringResource(R.string.nav_pose_coach)) },
                                modifier = Modifier.testTag("nav_pose_coach")
                            )
                            NavigationBarItem(
                                selected = uiState.activeTab == StudioTab.PHOTO_BLEND,
                                onClick = { viewModel.selectTab(StudioTab.PHOTO_BLEND) },
                                icon = {
                                    Icon(
                                        imageVector = if (uiState.activeTab == StudioTab.PHOTO_BLEND) {
                                            Icons.Filled.AutoFixHigh
                                        } else {
                                            Icons.Outlined.AutoFixHigh
                                        },
                                        contentDescription = null
                                    )
                                },
                                label = { Text(stringResource(R.string.nav_photo_blend)) },
                                modifier = Modifier.testTag("nav_photo_blend")
                            )
                            NavigationBarItem(
                                selected = uiState.activeTab == StudioTab.VAULT,
                                onClick = { viewModel.selectTab(StudioTab.VAULT) },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (uiState.vaultItems.isNotEmpty()) {
                                                Badge { Text("${uiState.vaultItems.size}") }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.activeTab == StudioTab.VAULT) {
                                                Icons.Filled.Collections
                                            } else {
                                                Icons.Outlined.Collections
                                            },
                                            contentDescription = null
                                        )
                                    }
                                },
                                label = { Text(stringResource(R.string.nav_gallery)) },
                                modifier = Modifier.testTag("nav_vault")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWideScreen) {
                        NavigationRail(
                            modifier = Modifier.fillMaxHeight(),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationRailItem(
                                selected = uiState.activeTab == StudioTab.POSE_COACH,
                                onClick = { viewModel.selectTab(StudioTab.POSE_COACH) },
                                icon = {
                                    Icon(Icons.Rounded.AccessibilityNew, contentDescription = null)
                                },
                                label = { Text(stringResource(R.string.nav_pose_coach)) },
                                modifier = Modifier.testTag("rail_pose_coach")
                            )
                            NavigationRailItem(
                                selected = uiState.activeTab == StudioTab.PHOTO_BLEND,
                                onClick = { viewModel.selectTab(StudioTab.PHOTO_BLEND) },
                                icon = {
                                    Icon(Icons.Filled.AutoFixHigh, contentDescription = null)
                                },
                                label = { Text(stringResource(R.string.nav_photo_blend)) },
                                modifier = Modifier.testTag("rail_photo_blend")
                            )
                            NavigationRailItem(
                                selected = uiState.activeTab == StudioTab.VAULT,
                                onClick = { viewModel.selectTab(StudioTab.VAULT) },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (uiState.vaultItems.isNotEmpty()) {
                                                Badge { Text("${uiState.vaultItems.size}") }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Filled.Collections, contentDescription = null)
                                    }
                                },
                                label = { Text(stringResource(R.string.nav_gallery)) },
                                modifier = Modifier.testTag("rail_vault")
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                        when (uiState.activeTab) {
                            StudioTab.POSE_COACH -> {
                                PoseCoachScreen(
                                    uiState = uiState,
                                    isWideScreen = isWideScreen,
                                    onTakeBackgroundPhoto = { launchCamera(ImageTargetSlot.COACH_BG) },
                                    onPickBackgroundPhoto = { launchGallery(ImageTargetSlot.COACH_BG) },
                                    onUseSampleBackground = viewModel::useSampleSceneForCoach,
                                    onClearBackground = { viewModel.setCoachBackground(null) },
                                    onPeopleCountChange = viewModel::setPeopleCount,
                                    onSelectStyle = viewModel::selectPoseStyle,
                                    onNotesChange = viewModel::updatePoseExtraNotes,
                                    onGeneratePoses = viewModel::generatePoseSuggestions,
                                    onSelectConcept = viewModel::selectConcept,
                                    onSelectPersonMarker = viewModel::selectPersonMarker,
                                    onToggleOverlay = viewModel::toggleSceneOverlay,
                                    onSendToBlend = viewModel::sendCoachBackgroundToBlend
                                )
                            }

                            StudioTab.PHOTO_BLEND -> {
                                PhotoBlendScreen(
                                    uiState = uiState,
                                    isWideScreen = isWideScreen,
                                    onTakeBackgroundPhoto = { launchCamera(ImageTargetSlot.BLEND_BG) },
                                    onPickBackgroundPhoto = { launchGallery(ImageTargetSlot.BLEND_BG) },
                                    onUseSampleBackground = viewModel::useSampleSceneForBlend,
                                    onClearBackground = { viewModel.setBlendBackground(null) },
                                    onTakeSubjectPhoto = { launchCamera(ImageTargetSlot.BLEND_SUBJECT) },
                                    onPickSubjectPhoto = { launchGallery(ImageTargetSlot.BLEND_SUBJECT) },
                                    onUseSampleSubjects = viewModel::useSampleSubjectsForBlend,
                                    onClearSubject = { viewModel.setBlendSubject(null) },
                                    onSelectLighting = viewModel::selectLightingPreset,
                                    onSelectFraming = viewModel::selectFramingPreference,
                                    onNotesChange = viewModel::updateBlendCustomNotes,
                                    onBlendPhotos = viewModel::generatePhotoBlend,
                                    onSaveToDevice = viewModel::exportBitmapToGallery,
                                    onShareBitmap = { bmp ->
                                        val shareIntent = viewModel.buildShareIntent(bmp)
                                        context.startActivity(
                                            Intent.createChooser(shareIntent, "Share Studio Composite")
                                        )
                                    }
                                )
                            }

                            StudioTab.VAULT -> {
                                VaultScreen(
                                    items = uiState.vaultItems,
                                    isWideScreen = isWideScreen,
                                    onGoToBlend = { viewModel.selectTab(StudioTab.PHOTO_BLEND) },
                                    onExportBitmap = viewModel::exportBitmapToGallery,
                                    onShareFile = { path ->
                                        val shareIntent = viewModel.buildShareIntentForFile(path)
                                        context.startActivity(
                                            Intent.createChooser(shareIntent, "Share Studio Composite")
                                        )
                                    },
                                    onDeleteItem = viewModel::deleteVaultItem
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
