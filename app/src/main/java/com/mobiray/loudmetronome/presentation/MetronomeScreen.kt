package com.mobiray.loudmetronome.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiray.loudmetronome.ui.theme.LoudMetronome3Theme

@Composable
fun MetronomeScreen() {
    val viewModel: MetronomeViewModel = viewModel()

    LoudMetronome3Theme {
        MetronomeScreenContent(viewModel)
    }
}

@Composable
private fun MetronomeScreenContent(viewModel: MetronomeViewModel?) {
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
            onClick = { viewModel?.play() }
        ) {
            Text(text = "Start service")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { viewModel?.stop() }
        ) {
            Text(text = "Stop service")
        }
    }
}

@Preview
@Composable
fun PreviewMetronomeScreen() {
    LoudMetronome3Theme {
        MetronomeScreenContent(null)
    }
}