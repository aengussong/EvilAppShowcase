package com.aengussong.evilappshowcase.screen.detector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CheckupProbability {
    DETECTED,
    PROBABLY_DETECTED,
    NOT_DETECTED
}

@Composable
fun CheckupScreen(
    colorCodingHints: Map<String, CheckupProbability>,
    checkupList: Map<String, CheckupProbability>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(10.dp)) {
        Text(text = "Color coding:")
        CheckupList(colorCodingHints)

        Text(
            text = "Checkup results",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp)
        )
        CheckupList(checkupList)
    }
}

@Composable
private fun CheckupList(
    emulatorCheckupList: Map<String, CheckupProbability>,
    modifier: Modifier = Modifier
) {
    fun CheckupProbability.asColor(): Color {
        return when (this) {
            CheckupProbability.DETECTED -> Color.Red
            CheckupProbability.NOT_DETECTED -> Color.Black
            CheckupProbability.PROBABLY_DETECTED -> Color(255, 140, 0)
        }
    }

    LazyColumn(modifier = modifier) {
        items(emulatorCheckupList.entries.toList()) { (property, emulatorProbability) ->
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = emulatorProbability.asColor())) {
                    append(property)
                }
            })
        }
    }
}
