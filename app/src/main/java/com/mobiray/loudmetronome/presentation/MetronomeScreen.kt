package com.mobiray.loudmetronome.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiray.loudmetronome.ui.theme.LoudMetronome3Theme

@Composable
fun MetronomeScreen() {
    val viewModel: MainViewModel = viewModel()

    LoudMetronome3Theme {
        MetronomeScreenContent(viewModel)
    }
}