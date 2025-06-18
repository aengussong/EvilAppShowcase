package com.aengussong.evilappshowcase

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aengussong.evilappshowcase.screen.detector.EmulatorCheckupScreen
import com.aengussong.evilappshowcase.screen.detector.FridaCheckupScreen
import com.aengussong.evilappshowcase.screen.detector.RootCheckupScreen
import com.aengussong.evilappshowcase.screen.navigation.ANALYSIS_DETECTOR_CHOOSER
import com.aengussong.evilappshowcase.screen.navigation.EMULATOR_CHECKUP_SCREEN
import com.aengussong.evilappshowcase.screen.navigation.FRIDA_CHECKUP_SCREEN
import com.aengussong.evilappshowcase.screen.navigation.ROOT_CHECKUP_SCREEN
import com.aengussong.evilappshowcase.ui.theme.EvilAppShowcaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        setContent {
            EvilAppShowcaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(telephonyManager, packageManager, innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainScreen(telephonyManager: TelephonyManager, packageManager: PackageManager, innerPadding: PaddingValues) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = ANALYSIS_DETECTOR_CHOOSER) {
        composable(ANALYSIS_DETECTOR_CHOOSER) { AnalysisDetectorChooserScreen(navController) }
        composable(EMULATOR_CHECKUP_SCREEN) { EmulatorCheckupScreen(telephonyManager, innerPadding) }
        composable(FRIDA_CHECKUP_SCREEN) { FridaCheckupScreen(innerPadding) }
        composable(ROOT_CHECKUP_SCREEN) { RootCheckupScreen(packageManager, innerPadding) }
    }
}

@Composable
fun AnalysisDetectorChooserScreen(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            navController.navigate(EMULATOR_CHECKUP_SCREEN)
        }) {
            Text("Emulator Checkup")
        }
        Button(onClick = {
            navController.navigate(FRIDA_CHECKUP_SCREEN)
        }) {
            Text("Frida Checkup")
        }
        Button(onClick = {
            navController.navigate(ROOT_CHECKUP_SCREEN)
        }) {
            Text("Root Checkup")
        }
    }
}