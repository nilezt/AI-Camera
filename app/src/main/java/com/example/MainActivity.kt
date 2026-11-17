package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai.PhotographyAiRepository
import com.example.ai.configureAppCheck
import com.example.data.ImageStorageHelper
import com.example.ui.PoseCraftApp
import com.example.ui.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Firebase App Check with launch intent before any UI, ViewModels, or AI calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        val storageHelper = ImageStorageHelper(applicationContext)
        val aiRepository = PhotographyAiRepository()

        setContent {
            val studioViewModel: StudioViewModel = viewModel(
                factory = StudioViewModel.provideFactory(
                    storageHelper = storageHelper,
                    aiRepository = aiRepository
                )
            )
            PoseCraftApp(viewModel = studioViewModel)
        }
    }
}
