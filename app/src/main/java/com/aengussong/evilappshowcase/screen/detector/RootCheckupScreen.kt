package com.aengussong.evilappshowcase.screen.detector

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aengussong.evilappshowcase.analysis_detectors.RootDetector
import com.aengussong.evilappshowcase.analysis_detectors.RootProbability

@Composable
fun RootCheckupScreen(packageManager: PackageManager, innerPadding: PaddingValues) {
    fun Map<String, RootProbability>.toUI(): Map<String, CheckupProbability> = this.mapValues { (_, probability) ->
        when (probability) {
            RootProbability.ROOT_DETECTED -> CheckupProbability.DETECTED
            RootProbability.NOT_DETECTED -> CheckupProbability.NOT_DETECTED
        }
    }

    CheckupScreen(
        colorCodingHints = mapOf(
            "   Root detected" to CheckupProbability.DETECTED,
            "   Root presence indicator not detected" to CheckupProbability.NOT_DETECTED
        ),
        checkupList = RootDetector.rootCheckup(packageManager).toUI(),
        modifier = Modifier.padding(innerPadding)
    )
}
