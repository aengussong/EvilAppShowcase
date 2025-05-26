package com.aengussong.evilappshowcase

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
import com.aengussong.evilappshowcase.ui.theme.EvilAppShowcaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        setContent {
            EvilAppShowcaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EmulatorCheckup(
                        EmulatorDetector.emulatorCheckup(telephonyManager),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EmulatorCheckup(
    emulatorCheckupList: Map<String, EmulatorProbability>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(10.dp)) {
        Text(text = "Color coding:")
        val colorCodingHints = mapOf(
            "   is emulator" to EmulatorProbability.IS_EMULATOR,
            "   probably is emulator" to EmulatorProbability.PROBABLY_EMULATOR,
            "   not emulator" to EmulatorProbability.NOT_EMULATOR
        )

        EmulatorCheckupList(colorCodingHints)

        Text(
            text = "Emulator checkup results",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp)
        )
        EmulatorCheckupList(emulatorCheckupList)
    }
}

@Composable
private fun EmulatorCheckupList(
    emulatorCheckupList: Map<String, EmulatorProbability>,
    modifier: Modifier = Modifier
) {
    fun EmulatorProbability.asColor(): Color {
        return when (this) {
            EmulatorProbability.IS_EMULATOR -> Color.Red
            EmulatorProbability.NOT_EMULATOR -> Color.Black
            EmulatorProbability.PROBABLY_EMULATOR -> Color(255, 140, 0)
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
