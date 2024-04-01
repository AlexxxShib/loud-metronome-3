package com.mobiray.loudmetronome.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobiray.loudmetronome.soundengine.preset.Segment
import com.mobiray.loudmetronome.ui.theme.LoudMetronome3Theme

@Composable
fun MetronomeScreenContent(viewModel: MainViewModel?) {
    val screenState = viewModel?.screenStateFlow?.collectAsState()
        ?: throw NullPointerException("Main view model is null!")

    when (val screenStateValue = screenState.value) {

        is ScreenState.Loading -> LoadingScreen()

        is ScreenState.Metronome -> MetronomeSkinScreen(
            screenState = screenStateValue,
            onClickPlayStop = {
                viewModel.playStop()
            }
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

@Composable
private fun MetronomeSkinScreen(
    screenState: ScreenState.Metronome,
    onClickPlayStop: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { onClickPlayStop?.invoke() }
        ) {
            Text(text = if (screenState.isPlaying) "Stop" else "Play")
        }
    }
}

@Preview
@Composable
fun PreviewMetronomeScreen() {
    LoudMetronome3Theme {
//        LoadingScreen()
        MetronomeSkinScreen(
            screenState = ScreenState.Metronome(
                isPlaying = false,
                segment = Segment()
            ),
            null
        )
    }
}