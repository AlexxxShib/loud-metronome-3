package com.mobiray.loudmetronome.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiray.loudmetronome.R
import com.mobiray.loudmetronome.ui.theme.Gray40
import com.mobiray.loudmetronome.ui.theme.Gray80
import com.mobiray.loudmetronome.ui.theme.LoudMetronome3Theme
import com.mobiray.loudmetronome.ui.theme.Orange40

@Composable
fun MetronomeScreen() {
    val viewModel: MainViewModel = viewModel()
    val screenState = viewModel.screenStateFlow.collectAsState()

    LoudMetronome3Theme {
        when (val screenStateValue = screenState.value) {

            is ScreenState.Loading -> LoadingScreen()

            is ScreenState.Metronome -> MetronomeSkinScreen(
                screenState = screenStateValue,
                onClickPlayStop = {
                    viewModel.playStop()
                },
                onClickChangeBpm = {
                    viewModel.changeBpm(it)
                }
            )
        }
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
    onClickPlayStop: (() -> Unit)? = null,
    onClickChangeBpm: ((Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "${screenState.bpm} bpm",
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            StateButton(
                text = "-1",
                containerColor = Gray80,
                onClickListener = { onClickChangeBpm?.invoke(-1) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            StateButton(
                text = "-10",
                containerColor = Gray80,
                onClickListener = { onClickChangeBpm?.invoke(-10) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            StateButton(
                text = "+10",
                containerColor = Gray80,
                onClickListener = { onClickChangeBpm?.invoke(10) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            StateButton(
                text = "+1",
                containerColor = Gray80,
                onClickListener = { onClickChangeBpm?.invoke(1) }
            )

        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val openNumeratorPickerDialog = remember { mutableStateOf(false) }
            val numeratorValues = remember { (1..12).map { it.toString() } }
            val numeratorIndex = remember { mutableIntStateOf(3) }

            StateButton(
                containerColor = Gray40,
                onClickListener = { openNumeratorPickerDialog.value = true }
            ) {
                Text(
                    text = numeratorValues[numeratorIndex.intValue],
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (openNumeratorPickerDialog.value) {
                PickerDialog(
                    values = numeratorValues,
                    startIndex = numeratorIndex.intValue,
                    onDismissRequest = {
                        openNumeratorPickerDialog.value = false
                        numeratorIndex.intValue = it
                    }
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "/",
                color = Color.White,
                fontSize = 75.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(4.dp))

            val openDenomeratorPickerDialog = remember { mutableStateOf(false) }
            val denomeratorValues = remember { listOf("4", "8") }
            val denomeratorIndex = remember { mutableIntStateOf(0) }

            StateButton(
                containerColor = Gray40,
                onClickListener = { openDenomeratorPickerDialog.value = true }
            ) {
                Text(
                    text = denomeratorValues[denomeratorIndex.intValue],
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (openDenomeratorPickerDialog.value) {
                PickerDialog(
                    values = denomeratorValues,
                    startIndex = denomeratorIndex.intValue,
                    onDismissRequest = {
                        openDenomeratorPickerDialog.value = false
                        denomeratorIndex.intValue = it
                    }
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            StateButton(
                isActive = true,
                onClickListener = { }
            ) {
                Text(
                    text = "Accent",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            ImageStateButton(
                isActive = true,
                drawableResId = R.drawable.note_quarter,
                onClickListener = { }
            )

            Spacer(modifier = Modifier.size(4.dp))

            ImageStateButton(
                isActive = false,
                drawableResId = R.drawable.note_eighth,
                onClickListener = { }
            )

            Spacer(modifier = Modifier.size(4.dp))

            ImageStateButton(
                isActive = false,
                drawableResId = R.drawable.note_triplet,
                onClickListener = { }
            )

            Spacer(modifier = Modifier.size(4.dp))

            ImageStateButton(
                isActive = false,
                drawableResId = R.drawable.note_sixteenth,
                onClickListener = { }
            )

        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {

            StateButton(
                isActive = screenState.isPlaying,
                text = if (screenState.isPlaying) "STOP" else "PLAY",
                onClickListener = { onClickPlayStop?.invoke() }
            )

            Spacer(modifier = Modifier.size(8.dp))

            StateButton(
                text = "TAP TEMPO",
                containerColor = Gray80,
                onClickListener = { }
            )
        }

        Spacer(modifier = Modifier.size(32.dp))

    }
}

@Composable
private fun RowScope.ImageStateButton(
    isActive: Boolean = false,
    drawableResId: Int,
    onClickListener: () -> Unit,
) {
    StateButton(
        isActive = isActive,
        containerColor = Gray80,
        onClickListener = onClickListener,
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            modifier = Modifier.size(72.dp),
            painter = painterResource(id = drawableResId),
            contentDescription = null
        )
    }
}

@Composable
private fun RowScope.StateButton(
    isActive: Boolean = false,
    text: String = "",
    containerColor: Color = Orange40,
    onClickListener: () -> Unit,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable (() -> Unit)? = null
) {
    Button(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        border = if (isActive) BorderStroke(5.dp, Color.White) else null,
        onClick = { onClickListener() },
        contentPadding = contentPadding
    ) {
        if (content != null) {
            content()
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
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
                isPlaying = true,
                bpm = 120,
                numerator = 4,
                denominator = 4,
                accent = true,
                subbeat = 0
            )
        )
    }
}