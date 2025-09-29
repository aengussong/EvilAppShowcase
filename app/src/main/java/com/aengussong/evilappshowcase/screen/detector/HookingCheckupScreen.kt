package com.aengussong.evilappshowcase.screen.detector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aengussong.evilappshowcase.analysis_detectors.HookingDetector
import com.aengussong.evilappshowcase.analysis_detectors.HookingProbability

@Composable
fun HookingCheckupScreen(innerPadding: PaddingValues) {
    fun Map<String, HookingProbability>.toUI(): Map<String, CheckupProbability> = this.mapValues { (_, probability) ->
        when (probability) {
            HookingProbability.HOOKING_DETECTED -> CheckupProbability.DETECTED
            HookingProbability.NOT_DETECTED -> CheckupProbability.NOT_DETECTED
        }
    }

    CheckupScreen(
        colorCodingHints = mapOf(
            "   Hooking presence indicator detected" to CheckupProbability.DETECTED,
            "   Hooking presence indicator not detected" to CheckupProbability.NOT_DETECTED
        ),
        checkupList = HookingDetector.hookingCheckup().toUI(),
        modifier = Modifier.padding(innerPadding)
    )
}
