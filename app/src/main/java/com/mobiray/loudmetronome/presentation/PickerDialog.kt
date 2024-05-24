package com.mobiray.loudmetronome.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mobiray.loudmetronome.presentation.picker.Picker
import com.mobiray.loudmetronome.presentation.picker.rememberPickerState
import com.mobiray.loudmetronome.presentation.ui.theme.Gray40

@Composable
fun PickerDialog(
    values: List<String>,
    startIndex: Int,
    onValueSelected: (Int) -> Unit
) {
    val valuesPickerState = rememberPickerState()

    Dialog(
        onDismissRequest = {
            val index = values.indexOf(valuesPickerState.selectedItem)
            onValueSelected(index)
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Gray40)
        ) {
            Picker(
                state = valuesPickerState,
                items = values,
                startIndex = startIndex,
                modifier = Modifier.weight(0.3f),
                textModifier = Modifier.padding(8.dp),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 32.sp
                ),
                dividerColor = Color.White,
                onValueSelected = onValueSelected
            )
        }
    }
}

@Preview
@Composable
fun PickerDialogPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        PickerDialog(
            values = remember { (1..11).map { it.toString() } },
            startIndex = 3,
            onValueSelected = {}
        )
    }
}
