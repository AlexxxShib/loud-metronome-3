package com.mobiray.loudmetronome.presentation

import android.view.MotionEvent
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobiray.loudmetronome.R
import com.mobiray.loudmetronome.presentation.ui.theme.Gray40
import com.mobiray.loudmetronome.presentation.ui.theme.Gray80
import com.mobiray.loudmetronome.presentation.ui.theme.LoudMetronome3Theme
import com.mobiray.loudmetronome.presentation.ui.theme.Orange40

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

@Composable
fun RequestPermissionScreen(
    onClickContinue: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.permission_request_message),
            color = Color.White,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.size(32.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange40
            ),
            onClick = { onClickContinue?.invoke() }
        ) {
            Text(
                text = stringResource(R.string.button_continue),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetronomeSkinScreen(
    screenState: ScreenState.Metronome,
    onClickPlayStop: (() -> Unit) = { },
    onClickAddBpm: ((Int) -> Unit) = { },
    onClickNumerator: ((Int) -> Unit) = { },
    onClickDenominator: ((Int) -> Unit) = { },
    onClickAccent: (() -> Unit) = { },
    onClickSubbeat: ((Int) -> Unit) = { },
    onTapTempo: (() -> Unit) = { }
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
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 5.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.text_field_bpm, screenState.bpm),
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
                .weight(1f)
        ) {

            StateButton(
                text = "-1",
                containerColor = Gray80,
                onClickListener = { onClickAddBpm(-1) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            StateButton(
                text = "-10",
                containerColor = Gray80,
                onClickListener = { onClickAddBpm(-10) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            StateButton(
                text = "+10",
                containerColor = Gray80,
                onClickListener = { onClickAddBpm(10) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            StateButton(
                text = "+1",
                containerColor = Gray80,
                onClickListener = { onClickAddBpm(1) }
            )

        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            PickerButton(
                pickerValue = screenState.numerator,
                pickerValues = (1..12).toList()
            ) {
                onClickNumerator(it)
            }

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "/",
                color = Color.White,
                fontSize = 75.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(4.dp))

            PickerButton(
                pickerValue = screenState.denominator,
                pickerValues = listOf(4, 8)
            ) {
                onClickDenominator(it)
            }

            Spacer(modifier = Modifier.size(8.dp))

            StateButton(
                isActive = screenState.accent,
                onClickListener = { onClickAccent() },
            ) {
                Text(
                    text = stringResource(R.string.button_accent),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            ImageStateButton(
                isActive = screenState.subbeat == 0,
                drawableResId = R.drawable.note_quarter,
                onClickListener = { onClickSubbeat(0) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            ImageStateButton(
                isActive = screenState.subbeat == 1,
                drawableResId = R.drawable.note_eighth,
                onClickListener = { onClickSubbeat(1) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            ImageStateButton(
                isActive = screenState.subbeat == 2,
                drawableResId = R.drawable.note_triplet,
                onClickListener = { onClickSubbeat(2) }
            )

            Spacer(modifier = Modifier.size(4.dp))

            ImageStateButton(
                isActive = screenState.subbeat == 3,
                drawableResId = R.drawable.note_sixteenth,
                onClickListener = { onClickSubbeat(3) }
            )

        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
        ) {

            StateButton(
                isActive = screenState.isPlaying,
                text = if (screenState.isPlaying) {
                    stringResource(R.string.button_stop)
                } else {
                    stringResource(R.string.button_play)
                },
                onClickListener = { onClickPlayStop() }
            )

            Spacer(modifier = Modifier.size(8.dp))

            StateButton(
                text = "TAP TEMPO",
                containerColor = Gray80,
                onTouchDown = {
                    onTapTempo()
                }
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

    }
}

@Composable
private fun RowScope.PickerButton(
    pickerValue: Int,
    pickerValues: List<Int>,
    onPickedValue: (Int) -> Unit
) {
    val openPickerDialog = remember { mutableStateOf(false) }
    val values = remember { pickerValues.map { it.toString() } }
    val index = remember { mutableIntStateOf(0) }

    SideEffect {
        val numeratorValue = pickerValue.toString()
        index.intValue = values.indexOf(numeratorValue)
    }

    StateButton(
        containerColor = Gray40,
        onClickListener = { openPickerDialog.value = true }
    ) {
        Text(
            text = pickerValue.toString(),
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (openPickerDialog.value) {
        PickerDialog(
            values = values,
            startIndex = index.intValue,
            onDismissRequest = {
                openPickerDialog.value = false
                val numeratorValue = values[it].toInt()
                onPickedValue.invoke(numeratorValue)
            }
        )
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RowScope.StateButton(
    isActive: Boolean = false,
    text: String = "",
    containerColor: Color = Orange40,
    onClickListener: (() -> Unit)? = null,
    onTouchDown: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable (() -> Unit)? = null
) {
    Button(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .pointerInteropFilter {
                if (it.action == MotionEvent.ACTION_DOWN) {
                    onTouchDown?.invoke()
                }
                false
            },
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        border = if (isActive) BorderStroke(5.dp, Color.White) else null,
        onClick = { onClickListener?.invoke() },
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
//        RequestPermissionScreen()
    }
}