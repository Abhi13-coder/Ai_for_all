package com.aiforall.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aiforall.app.presentation.navigation.AiForAllNavGraph
import com.aiforall.app.presentation.theme.AIForAllTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity host. All screens are Composables reached through
 * AiForAllNavGraph — this keeps navigation, state, and theming unified
 * instead of scattering them across multiple Activities.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // lets gradient backgrounds draw behind system bars
        setContent {
            AIForAllTheme {
                AiForAllNavGraph()
            }
        }
    }
}
