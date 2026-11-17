package com.example.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.DrawableRes

sealed interface PhotoSource {
    data class UriSource(val uri: Uri) : PhotoSource
    data class SampleDrawable(@DrawableRes val resId: Int, val label: String) : PhotoSource
}

enum class PoseStyle(val label: String, val promptHint: String) {
    CASUAL("Casual Candid", "Natural, relaxed, candid lifestyle energy with effortless motion"),
    EDITORIAL("Editorial", "Magazine-worthy fashion geometry, confident posture, clean angles"),
    PLAYFUL("Friends Fun", "Warm interaction, laughter, staggered heights, dynamic connection"),
    ROMANTIC("Golden Warmth", "Intimate, soft body orientation, gentle connection and closeness"),
    TRAVEL("Travel Story", "Environmental framing showing off the destination and natural curiosity")
}

enum class LightingPreset(val label: String, val promptHint: String) {
    AUTO_MATCH("Auto Match", "Match the exact ambient direction, color temperature, and contrast of the background scene"),
    GOLDEN_HOUR("Golden Hour", "Add warm late-afternoon golden rim lighting and soft glowing highlights consistent with the scene"),
    SOFT_STUDIO("Soft Diffused", "Even, flattering soft editorial fill lighting with gentle natural shadows"),
    CRISP_DAYLIGHT("Crisp Daylight", "Clean, vibrant natural daylight with realistic sun-cast shadows"),
    MOODY_FILM("Moody Film", "Cinematic depth with rich contrast, soft shadow falloff, and subtle filmic color harmony")
}

enum class FramingPreference(val label: String, val promptHint: String) {
    AUTO_FIT("Natural Fit", "Place the subject(s) at the most realistic natural scale and spot within the background frame"),
    FULL_BODY("Full Body", "Show the subject(s) full-body grounded naturally on the scene floor with realistic contact shadows"),
    WAIST_UP("Waist-Up", "Frame the subject(s) as a balanced three-quarter or waist-up environmental portrait"),
    ENVIRONMENTAL("Wide Scene", "Keep the subject(s) slightly smaller in frame so the surrounding architecture and scenery shine")
}

data class PersonPlacement(
    val personNumber: Int,
    val roleLabel: String,
    val xNorm: Float,
    val yNorm: Float,
    val postureTag: String,
    val headAndGaze: String,
    val shouldersAndTorso: String,
    val armsAndHands: String,
    val legsAndStance: String
)

data class PoseConcept(
    val title: String,
    val vibeTag: String,
    val whyItWorks: String,
    val placementSummary: String,
    val cameraAngle: String,
    val framingTip: String,
    val subjects: List<PersonPlacement>
)

data class PoseCoachResult(
    val sceneRead: String,
    val lightingDirection: String,
    val bestSpot: String,
    val concepts: List<PoseConcept>
)

sealed interface PoseCoachUiState {
    data object Idle : PoseCoachUiState
    data object Loading : PoseCoachUiState
    data class Success(val result: PoseCoachResult) : PoseCoachUiState
    data class Error(val message: String) : PoseCoachUiState
}

sealed interface PhotoBlendUiState {
    data object Idle : PhotoBlendUiState
    data object Loading : PhotoBlendUiState
    data class Success(
        val blendedBitmap: Bitmap,
        val savedCreationId: String? = null
    ) : PhotoBlendUiState
    data class Error(val message: String) : PhotoBlendUiState
}

data class SavedCreation(
    val id: String,
    val title: String,
    val subtitle: String,
    val filePath: String,
    val createdAtMillis: Long
)
