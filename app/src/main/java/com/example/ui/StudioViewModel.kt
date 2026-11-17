package com.example.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.ai.PhotographyAiRepository
import com.example.data.ImageStorageHelper
import com.example.model.FramingPreference
import com.example.model.LightingPreset
import com.example.model.PhotoBlendUiState
import com.example.model.PhotoSource
import com.example.model.PoseCoachUiState
import com.example.model.PoseStyle
import com.example.model.SavedCreation
import com.google.firebase.ai.type.QuotaExceededException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "StudioViewModel"

enum class StudioTab {
    POSE_COACH,
    PHOTO_BLEND,
    VAULT
}

data class StudioUiState(
    val activeTab: StudioTab = StudioTab.POSE_COACH,

    // Feature 1: Pose Coach
    val coachBackground: PhotoSource? = null,
    val peopleCount: Int = 2,
    val selectedPoseStyle: PoseStyle = PoseStyle.CASUAL,
    val poseExtraNotes: String = "",
    val poseCoachState: PoseCoachUiState = PoseCoachUiState.Idle,
    val selectedConceptIndex: Int = 0,
    val selectedPersonIndex: Int = 0,
    val showSceneOverlay: Boolean = true,

    // Feature 2: Photo Blend
    val blendBackground: PhotoSource? = null,
    val blendBackgroundPreviewBitmap: Bitmap? = null,
    val blendSubject: PhotoSource? = null,
    val selectedLighting: LightingPreset = LightingPreset.AUTO_MATCH,
    val selectedFraming: FramingPreference = FramingPreference.AUTO_FIT,
    val blendCustomNotes: String = "",
    val photoBlendState: PhotoBlendUiState = PhotoBlendUiState.Idle,

    // Vault / Gallery
    val vaultItems: List<SavedCreation> = emptyList(),
    val statusMessage: String? = null
)

class StudioViewModel(
    private val storageHelper: ImageStorageHelper,
    private val aiRepository: PhotographyAiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    init {
        refreshVault()
    }

    fun selectTab(tab: StudioTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun createCameraUri(): Uri = storageHelper.createCameraCaptureUri()

    // --- Feature 1 Actions ---

    fun setCoachBackground(source: PhotoSource?) {
        _uiState.update {
            it.copy(
                coachBackground = source,
                poseCoachState = PoseCoachUiState.Idle,
                selectedConceptIndex = 0,
                selectedPersonIndex = 0
            )
        }
    }

    fun useSampleSceneForCoach() {
        setCoachBackground(
            PhotoSource.SampleDrawable(
                resId = R.drawable.img_sample_cafe_scene,
                label = "Golden Hour Cafe Arch"
            )
        )
    }

    fun setPeopleCount(count: Int) {
        val clamped = count.coerceIn(1, 10)
        _uiState.update { it.copy(peopleCount = clamped) }
    }

    fun selectPoseStyle(style: PoseStyle) {
        _uiState.update { it.copy(selectedPoseStyle = style) }
    }

    fun updatePoseExtraNotes(notes: String) {
        _uiState.update { it.copy(poseExtraNotes = notes) }
    }

    fun selectConcept(index: Int) {
        _uiState.update { it.copy(selectedConceptIndex = index, selectedPersonIndex = 0) }
    }

    fun selectPersonMarker(index: Int) {
        _uiState.update { it.copy(selectedPersonIndex = index) }
    }

    fun toggleSceneOverlay() {
        _uiState.update { it.copy(showSceneOverlay = !it.showSceneOverlay) }
    }

    fun generatePoseSuggestions() {
        val bgSource = _uiState.value.coachBackground
        if (bgSource == null) {
            _uiState.update {
                it.copy(poseCoachState = PoseCoachUiState.Error("Please capture or select a background photo first."))
            }
            return
        }

        val count = _uiState.value.peopleCount
        val style = _uiState.value.selectedPoseStyle
        val notes = _uiState.value.poseExtraNotes

        viewModelScope.launch {
            _uiState.update { it.copy(poseCoachState = PoseCoachUiState.Loading) }
            try {
                val bitmap = storageHelper.loadBitmapForAi(bgSource)
                    ?: throw IllegalStateException("Could not decode the selected background image.")

                val result = aiRepository.analyzeSceneAndSuggestPoses(
                    backgroundBitmap = bitmap,
                    peopleCount = count,
                    style = style,
                    extraNotes = notes
                )

                _uiState.update {
                    it.copy(
                        poseCoachState = PoseCoachUiState.Success(result),
                        selectedConceptIndex = 0,
                        selectedPersonIndex = 0,
                        showSceneOverlay = true
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Pose suggestion timed out", e)
                _uiState.update {
                    it.copy(
                        poseCoachState = PoseCoachUiState.Error(
                            "Pose analysis timed out after 60 seconds. Please try again."
                        )
                    )
                }
            } catch (e: QuotaExceededException) {
                Log.e(TAG, "Quota exceeded during pose analysis", e)
                _uiState.update {
                    it.copy(
                        poseCoachState = PoseCoachUiState.Error(
                            "AI quota limit reached. Please wait a moment or check your Firebase billing tier."
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Pose analysis failed", e)
                _uiState.update {
                    it.copy(
                        poseCoachState = PoseCoachUiState.Error(
                            e.localizedMessage ?: "Failed to analyze scene for poses."
                        )
                    )
                }
            }
        }
    }

    fun sendCoachBackgroundToBlend() {
        val bg = _uiState.value.coachBackground ?: return
        setBlendBackground(bg)
        _uiState.update { it.copy(activeTab = StudioTab.PHOTO_BLEND) }
    }

    // --- Feature 2 Actions ---

    fun setBlendBackground(source: PhotoSource?) {
        _uiState.update {
            it.copy(
                blendBackground = source,
                blendBackgroundPreviewBitmap = null,
                photoBlendState = PhotoBlendUiState.Idle
            )
        }
        if (source != null) {
            viewModelScope.launch {
                val bmp = storageHelper.loadBitmapForAi(source)
                _uiState.update { current ->
                    if (current.blendBackground == source) {
                        current.copy(blendBackgroundPreviewBitmap = bmp)
                    } else current
                }
            }
        }
    }

    fun setBlendSubject(source: PhotoSource?) {
        _uiState.update {
            it.copy(
                blendSubject = source,
                photoBlendState = PhotoBlendUiState.Idle
            )
        }
    }

    fun useSampleSceneForBlend() {
        setBlendBackground(
            PhotoSource.SampleDrawable(
                resId = R.drawable.img_sample_cafe_scene,
                label = "Golden Hour Cafe Arch"
            )
        )
    }

    fun useSampleSubjectsForBlend() {
        setBlendSubject(
            PhotoSource.SampleDrawable(
                resId = R.drawable.img_sample_friends_portrait,
                label = "Two Friends Studio"
            )
        )
    }

    fun selectLightingPreset(preset: LightingPreset) {
        _uiState.update { it.copy(selectedLighting = preset) }
    }

    fun selectFramingPreference(framing: FramingPreference) {
        _uiState.update { it.copy(selectedFraming = framing) }
    }

    fun updateBlendCustomNotes(notes: String) {
        _uiState.update { it.copy(blendCustomNotes = notes) }
    }

    fun generatePhotoBlend() {
        val bgSource = _uiState.value.blendBackground
        val subjSource = _uiState.value.blendSubject

        if (bgSource == null || subjSource == null) {
            _uiState.update {
                it.copy(
                    photoBlendState = PhotoBlendUiState.Error(
                        "Please select both a Background Scene photo and a Person/Group photo."
                    )
                )
            }
            return
        }

        val lighting = _uiState.value.selectedLighting
        val framing = _uiState.value.selectedFraming
        val customNotes = _uiState.value.blendCustomNotes

        viewModelScope.launch {
            _uiState.update { it.copy(photoBlendState = PhotoBlendUiState.Loading) }
            try {
                val bgBitmap = storageHelper.loadBitmapForAi(bgSource)
                    ?: throw IllegalStateException("Could not load the background image.")
                val subjBitmap = storageHelper.loadBitmapForAi(subjSource)
                    ?: throw IllegalStateException("Could not load the subject image.")

                val blendedBitmap = aiRepository.blendSubjectIntoBackground(
                    backgroundBitmap = bgBitmap,
                    subjectBitmap = subjBitmap,
                    lightingPreset = lighting,
                    framingPreference = framing,
                    customDirection = customNotes
                )

                val saved = storageHelper.saveCreationToVault(
                    bitmap = blendedBitmap,
                    title = "${lighting.label} Composite",
                    subtitle = framing.label
                )

                _uiState.update {
                    it.copy(
                        blendBackgroundPreviewBitmap = bgBitmap,
                        photoBlendState = PhotoBlendUiState.Success(
                            blendedBitmap = blendedBitmap,
                            savedCreationId = saved.id
                        ),
                        vaultItems = listOf(saved) + it.vaultItems
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Photo blend timed out", e)
                _uiState.update {
                    it.copy(
                        photoBlendState = PhotoBlendUiState.Error(
                            "Photo compositing timed out after 60 seconds. Please try again."
                        )
                    )
                }
            } catch (e: QuotaExceededException) {
                Log.e(TAG, "Quota exceeded during image blend", e)
                _uiState.update {
                    it.copy(
                        photoBlendState = PhotoBlendUiState.Error(
                            "Image generation quota exceeded or Blaze plan required on Firebase project."
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Photo blend failed", e)
                _uiState.update {
                    it.copy(
                        photoBlendState = PhotoBlendUiState.Error(
                            e.localizedMessage ?: "Failed to composite photos."
                        )
                    )
                }
            }
        }
    }

    fun exportBitmapToGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            storageHelper.exportToDeviceGallery(bitmap)
                .onSuccess { msg ->
                    _uiState.update { it.copy(statusMessage = msg) }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(statusMessage = err.localizedMessage ?: "Could not export photo.")
                    }
                }
        }
    }

    fun buildShareIntent(bitmap: Bitmap): Intent = storageHelper.buildShareIntent(bitmap)

    fun buildShareIntentForFile(filePath: String): Intent =
        storageHelper.buildShareIntentForFile(filePath)

    // --- Vault Actions ---

    fun refreshVault() {
        viewModelScope.launch {
            val items = storageHelper.loadVaultCreations()
            _uiState.update { it.copy(vaultItems = items) }
        }
    }

    fun deleteVaultItem(id: String) {
        viewModelScope.launch {
            val updated = storageHelper.deleteCreation(id)
            _uiState.update { it.copy(vaultItems = updated, statusMessage = "Removed from Studio Vault") }
        }
    }

    companion object {
        fun provideFactory(
            storageHelper: ImageStorageHelper,
            aiRepository: PhotographyAiRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudioViewModel(storageHelper, aiRepository)
            }
        }
    }
}
