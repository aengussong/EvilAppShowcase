package com.aengussong.evilappshowcase.screen.detector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aengussong.evilappshowcase.analysis_detectors.FridaDetector
import com.aengussong.evilappshowcase.analysis_detectors.FridaProbability

@Composable
fun FridaCheckupScreen(innerPadding: PaddingValues) {
    fun Map<String, FridaProbability>.toUI(): Map<String, CheckupProbability> = this.mapValues { (_, probability) ->
        when (probability) {
            FridaProbability.FRIDA_DETECTED -> CheckupProbability.DETECTED
            FridaProbability.NOT_DETECTED -> CheckupProbability.NOT_DETECTED
        }
    }

    CheckupScreen(
        colorCodingHints = mapOf(
            "   Frida presence indicator detected" to CheckupProbability.DETECTED,
            "   Frida presence indicator not detected" to CheckupProbability.NOT_DETECTED
        ),
        checkupList = FridaDetector.fridaCheckup().toUI(),
        modifier = Modifier.padding(innerPadding)
    )
}
