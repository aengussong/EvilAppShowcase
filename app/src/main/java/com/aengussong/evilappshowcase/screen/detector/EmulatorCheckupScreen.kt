package com.aengussong.evilappshowcase.screen.detector

import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.aengussong.evilappshowcase.analysis_detectors.EmulatorDetector
import com.aengussong.evilappshowcase.analysis_detectors.EmulatorProbability

@Composable
fun EmulatorCheckupScreen(telephonyManager: TelephonyManager, innerPadding: PaddingValues) {
    fun Map<String, EmulatorProbability>.toUI(): Map<String, CheckupProbability> {
        return this.mapValues { (_, probability) ->
            when (probability) {
                EmulatorProbability.IS_EMULATOR -> CheckupProbability.DETECTED
                EmulatorProbability.NOT_EMULATOR -> CheckupProbability.NOT_DETECTED
                EmulatorProbability.PROBABLY_EMULATOR -> CheckupProbability.PROBABLY_DETECTED
            }
        }
    }

    CheckupScreen(
        colorCodingHints = mapOf(
            "   is emulator" to CheckupProbability.DETECTED,
            "   probably is emulator" to CheckupProbability.PROBABLY_DETECTED,
            "   not emulator" to CheckupProbability.NOT_DETECTED
        ),
        checkupList = EmulatorDetector.emulatorCheckup(LocalContext.current, telephonyManager).toUI(),
        modifier = Modifier.padding(innerPadding)
    )
}
