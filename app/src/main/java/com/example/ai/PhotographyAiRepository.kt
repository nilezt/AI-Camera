package com.example.ai

import android.graphics.Bitmap
import com.example.model.FramingPreference
import com.example.model.LightingPreset
import com.example.model.PersonPlacement
import com.example.model.PoseCoachResult
import com.example.model.PoseConcept
import com.example.model.PoseStyle
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

class PhotographyAiRepository {

    private val subjectSchema = Schema.obj(
        properties = mapOf(
            "personNumber" to Schema.integer(description = "1-based index of the person"),
            "roleLabel" to Schema.string(description = "Short role label e.g. 'Anchor Left', 'Center Lead', 'Foreground Seated'"),
            "xNorm" to Schema.double(description = "Normalized horizontal position 0.12 to 0.88 on the scene image"),
            "yNorm" to Schema.double(description = "Normalized vertical position 0.25 to 0.85 on the scene image"),
            "postureTag" to Schema.string(description = "One of: standing_casual, leaning, sitting, walking, angled_turn, playful_group"),
            "headAndGaze" to Schema.string(description = "Specific chin tilt, head turn, and eye gaze direction"),
            "shouldersAndTorso" to Schema.string(description = "Exact torso twist, shoulder drop, and posture cue"),
            "armsAndHands" to Schema.string(description = "Natural hand placement, pockets, props, or arm connection with friends"),
            "legsAndStance" to Schema.string(description = "Weight shift, knee bend, foot angle, or stride instruction")
        )
    )

    private val conceptSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Memorable 2-4 word pose concept title"),
            "vibeTag" to Schema.string(description = "Short 1-2 word mood tag"),
            "whyItWorks" to Schema.string(description = "1-2 sentences explaining how this pose uses the scene geometry and light"),
            "placementSummary" to Schema.string(description = "Clear summary of where to stand/sit in this specific background"),
            "cameraAngle" to Schema.string(description = "Exact camera height and angle e.g. 'Waist-level, slight upward tilt'"),
            "framingTip" to Schema.string(description = "Framing & focal length cue e.g. '35mm full-body, leave arch overhead'"),
            "subjects" to Schema.array(
                items = subjectSchema,
                description = "Individual body-posing instructions for each person in the group"
            )
        )
    )

    private val poseCoachSchema = Schema.obj(
        properties = mapOf(
            "sceneRead" to Schema.string(description = "Concise photography analysis of background lines, depth, and surfaces"),
            "lightingDirection" to Schema.string(description = "Where key light & shadows fall in the photo"),
            "bestSpot" to Schema.string(description = "Exact best anchor zone in the scene for flattering portraits"),
            "concepts" to Schema.array(
                items = conceptSchema,
                description = "Exactly 3 distinct, practical pose concepts tailored to this scene and headcount"
            )
        )
    )

    private val poseCoachModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = poseCoachSchema
                temperature = 0.7f
            },
            systemInstruction = content {
                text(
                    "You are an elite editorial & lifestyle portrait photographer coaching friends who feel awkward in front of a camera. " +
                        "Inspect the uploaded background photo carefully: read its real architectural lines, furniture, steps, walls, foliage, horizon, and natural light direction. " +
                        "Give concrete, flattering, easy-to-follow body-figure posing instructions (exact weight shift, chin/jawline angle, hand placement so hands never hang awkwardly, shoulder angle, and natural group spacing/triangles). " +
                        "Always return one subject entry per requested person."
                )
            }
        )
    }

    private val photoBlendModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    suspend fun analyzeSceneAndSuggestPoses(
        backgroundBitmap: Bitmap,
        peopleCount: Int,
        style: PoseStyle,
        extraNotes: String
    ): PoseCoachResult {
        val prompt = buildString {
            appendLine("Analyze this background scene photo and create 3 distinct, flattering pose setups for $peopleCount ${if (peopleCount == 1) "person" else "people"}.")
            appendLine("Desired shoot vibe: ${style.label} (${style.promptHint}).")
            if (extraNotes.isNotBlank()) {
                appendLine("Additional user context: ${extraNotes.trim()}")
            }
            appendLine()
            appendLine("Requirements:")
            appendLine("1. Each concept MUST include exactly $peopleCount entries in `subjects` (numbered 1 to $peopleCount).")
            appendLine("2. Set `xNorm` (0.15..0.85) and `yNorm` (0.30..0.82) to place each person naturally inside THIS background photo (e.g., leaning on visible railings/walls, standing in open floor/archway space, sitting on visible chairs/steps).")
            appendLine("3. Give ultra-practical, non-generic body cues: where weight goes, how hips/shoulders angle toward light, what hands do (pocket, hair, railing, shoulder, cup), and chin/eye direction.")
            if (peopleCount > 1) {
                appendLine("4. For groups, create depth staggering, varied head heights (triangle/diagonal composition), and natural interaction between friends rather than a stiff lineup.")
            }
        }

        val response = withTimeout(60_000L) {
            poseCoachModel.generateContent(
                content {
                    image(backgroundBitmap)
                    text(prompt)
                }
            )
        }

        val rawJson = response.text
            ?: throw IllegalStateException("Pose Coach returned an empty response.")

        return parsePoseCoachJson(rawJson, peopleCount)
    }

    suspend fun blendSubjectIntoBackground(
        backgroundBitmap: Bitmap,
        subjectBitmap: Bitmap,
        lightingPreset: LightingPreset,
        framingPreference: FramingPreference,
        customDirection: String
    ): Bitmap {
        val prompt = buildString {
            appendLine("You are a master photographic retoucher and lighting compositor.")
            appendLine("Image 1 is the target BACKGROUND SCENE.")
            appendLine("Image 2 contains the SUBJECT(S) (1 or multiple people).")
            appendLine()
            appendLine("Task: Seamlessly composite ALL people from Image 2 into Image 1 as one unified, photorealistic photograph.")
            appendLine("Strict Fidelity & Lighting Rules:")
            appendLine("- Preserve the exact facial identity, features, expressions, hairstyles, skin tones, clothing, and count of people from Image 2.")
            appendLine("- Preserve the architecture, scenery, textures, and perspective of Image 1.")
            appendLine("- Scale and position the subject(s) so their proportions, eye-level horizon, and foot/ground placement look physically real inside Image 1.")
            appendLine("- Relight the subject(s) completely to match Image 1: match light direction, specular highlights, warm/cool color temperature, ambient bounce light, and realistic contact shadows on the ground or nearby surfaces.")
            appendLine("- Apply realistic depth-of-field edge blending with zero cutout halos or sticker artifacts.")
            appendLine("- Lighting look: ${lightingPreset.label} — ${lightingPreset.promptHint}.")
            appendLine("- Framing & scale: ${framingPreference.label} — ${framingPreference.promptHint}.")
            if (customDirection.isNotBlank()) {
                appendLine("- User placement note: ${customDirection.trim()}")
            }
        }

        val response = withTimeout(60_000L) {
            photoBlendModel.generateContent(
                content {
                    image(backgroundBitmap)
                    image(subjectBitmap)
                    text(prompt)
                }
            )
        }

        val imagePart = response.candidates.firstOrNull()
            ?.content?.parts
            ?.filterIsInstance<ImagePart>()
            ?.firstOrNull()

        return imagePart?.image
            ?: throw IllegalStateException("The compositor model did not return an image. Please try again.")
    }

    private fun parsePoseCoachJson(jsonString: String, expectedPeople: Int): PoseCoachResult {
        val root = JSONObject(jsonString)
        val conceptsArr = root.optJSONArray("concepts")
            ?: throw IllegalStateException("Missing pose concepts in model response.")

        val concepts = (0 until conceptsArr.length()).map { cIdx ->
            val cObj = conceptsArr.getJSONObject(cIdx)
            val subjectsArr = cObj.optJSONArray("subjects")
            val subjects = if (subjectsArr != null) {
                (0 until subjectsArr.length()).map { sIdx ->
                    val sObj = subjectsArr.getJSONObject(sIdx)
                    PersonPlacement(
                        personNumber = sObj.optInt("personNumber", sIdx + 1),
                        roleLabel = sObj.optString("roleLabel", "Person ${sIdx + 1}"),
                        xNorm = sObj.optDouble("xNorm", 0.5).toFloat().coerceIn(0.10f, 0.90f),
                        yNorm = sObj.optDouble("yNorm", 0.6).toFloat().coerceIn(0.20f, 0.88f),
                        postureTag = sObj.optString("postureTag", "standing_casual"),
                        headAndGaze = sObj.optString("headAndGaze", "Relaxed chin, natural gaze toward lens"),
                        shouldersAndTorso = sObj.optString("shouldersAndTorso", "Angle torso 30 degrees to camera"),
                        armsAndHands = sObj.optString("armsAndHands", "Rest one hand lightly in pocket or at side"),
                        legsAndStance = sObj.optString("legsAndStance", "Shift weight onto back foot, slight knee bend")
                    )
                }
            } else {
                emptyList()
            }

            PoseConcept(
                title = cObj.optString("title", "Pose Setup ${cIdx + 1}"),
                vibeTag = cObj.optString("vibeTag", "Natural"),
                whyItWorks = cObj.optString("whyItWorks", ""),
                placementSummary = cObj.optString("placementSummary", ""),
                cameraAngle = cObj.optString("cameraAngle", "Eye level"),
                framingTip = cObj.optString("framingTip", "35mm natural framing"),
                subjects = subjects.ifEmpty {
                    (1..expectedPeople).map { num ->
                        PersonPlacement(
                            personNumber = num,
                            roleLabel = "Person $num",
                            xNorm = (num.toFloat() / (expectedPeople + 1)),
                            yNorm = 0.62f,
                            postureTag = "standing_casual",
                            headAndGaze = "Turn chin slightly toward light",
                            shouldersAndTorso = "Angled torso, relaxed shoulders",
                            armsAndHands = "Lightly engaged hands",
                            legsAndStance = "Weight on back leg"
                        )
                    }
                }
            )
        }

        return PoseCoachResult(
            sceneRead = root.optString("sceneRead", ""),
            lightingDirection = root.optString("lightingDirection", ""),
            bestSpot = root.optString("bestSpot", ""),
            concepts = concepts
        )
    }
}
