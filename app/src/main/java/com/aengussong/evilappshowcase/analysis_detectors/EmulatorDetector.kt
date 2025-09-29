package com.aengussong.evilappshowcase.analysis_detectors

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.aengussong.evilappshowcase.util.beginsWith
import com.aengussong.evilappshowcase.util.fileExists
import com.aengussong.evilappshowcase.util.includes
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

enum class EmulatorProbability {
    IS_EMULATOR,
    PROBABLY_EMULATOR,
    NOT_EMULATOR
}

object EmulatorDetector {
    fun emulatorCheckup(context: Context, telephonyManager: TelephonyManager): Map<String, EmulatorProbability> {
        return mapOf(
            checkProperty("Build.CPU_ABI", { Build.CPU_ABI }, probablyEmulatorCanary = { it == "armeabi" }),
            checkProperty("Build.CPU_ABI2", { Build.CPU_ABI2 }, probablyEmulatorCanary = { it == "unknown" }),
            checkProperty("Build.BOARD", { Build.BOARD }, emulatorCanary = { it == "unknown" }),
            checkProperty("Build.BRAND", { Build.BRAND }, emulatorCanary = { it.beginsWith("generic") }),
            checkProperty("Build.DEVICE", { Build.DEVICE }, emulatorCanary = { it.beginsWith("generic") }),
            checkProperty("Build.FINGERPRINT", { Build.FINGERPRINT }, emulatorCanary = {
                it.beginsWith("generic") || it.beginsWith("unknown") || it.includes("vbox") || it.includes("test-keys")
            }),
            checkProperty("Build.HARDWARE", { Build.HARDWARE }, emulatorCanary = { it.includes("goldfish") }),
            checkProperty("Build.HOST", { Build.HOST }, probablyEmulatorCanary = { it == "android-test" }),
            checkProperty("Build.ID", { Build.ID }, emulatorCanary = { it == "FRF91" }),
            checkProperty("Build.MANUFACTURER", { Build.MANUFACTURER }, emulatorCanary = {
                it == "unknown" || it.includes("Genymotion")
            }),
            checkProperty("Build.MODEL", { Build.MODEL }, emulatorCanary = {
                it == "sdk" ||
                        it.includes("google_sdk") ||
                        it.includes("Emulator") ||
                        it.includes("Android SDK built for x86")
            }),
            checkProperty("Build.PRODUCT", { Build.PRODUCT }, emulatorCanary = {
                it == null || it == "sdk" || it == "sdk_x86" || it == "google_sdk" || it == "vbox86p" || it == "emulator"
            }),
            checkProperty("Build.RADIO", { Build.RADIO }, probablyEmulatorCanary = { it == "unknown" }),
            checkProperty(
                "Build.SERIAL",
                { Build.SERIAL },
                emulatorCanary = { it == null || it == "null" }),
            checkProperty("Build.USER", { Build.USER }, emulatorCanary = { it == "android-build" }),

            checkProperty("TelephonyManager.getNetworkCountryIso()", { telephonyManager.networkCountryIso }, probablyEmulatorCanary = { it == "us" }),
            checkProperty("TelephonyManager.getNetworkOperator().substring(0,3)", { telephonyManager.networkOperator?.substring(0, 3) }, probablyEmulatorCanary = { it == "310" }),
            checkProperty("TelephonyManager.getNetworkOperator().substring(3)", { telephonyManager.networkOperator?.substring(3) }, probablyEmulatorCanary = { it == "260" }),
            checkProperty("TelephonyManager.getPhoneType()", { telephonyManager.phoneType.toString() }, probablyEmulatorCanary = { it == "1" }),
            checkProperty("TelephonyManager.getSimCountryIso()", { telephonyManager.simCountryIso }, probablyEmulatorCanary = { it == "us" }),
            checkProperty("TelephonyManager.getNetworkOperatorName()", { telephonyManager.networkOperatorName }, probablyEmulatorCanary = { it == "Android" }),

            checkProperty("ro.kernel.qemu in System.getProperty", { System.getProperty("ro.kernel.qemu") }, emulatorCanary = { it != null }),

            checkBuildPropProperty("ro,kernel.qemu", emulatorCanary = { !it.isNullOrEmpty() }),
            checkBuildPropProperty("ro.product.device", emulatorCanary = { it == "qemu" }),
            checkBuildPropProperty("ro.kernel.android.qemud", emulatorCanary = { !it.isNullOrEmpty() }),

            checkFile("/dev/docket/qemud"),
            checkFile("/dev/qemu_pipe"),
            checkFile("/system/lib/libc_malloc_debug_qemu.so"),
            checkFile("/sys/qemu_trace"),
            checkFile("/system/bin/qemu_props"),

            checkDialActionAvailability(context),
            readCpuInfo(),
            checkDefaultBluetoothAdapterPresence(),
            checkDefaultBluetoothAdapterName(context),
            checkAmbientLightSensor(context)
        )
    }

    private fun checkDialActionAvailability(context: Context): Pair<String, EmulatorProbability> {
        val intent = Intent().apply {
            setData("tel:123456".toUri())
            setAction("android.intent.action.DIAL")
        }
        val canResolveActivity = intent.resolveActivity(context.packageManager) != null
        val probability = if (canResolveActivity) EmulatorProbability.NOT_EMULATOR else EmulatorProbability.IS_EMULATOR

        return "Can resolve dial action: $canResolveActivity" to probability
    }

    private fun checkProperty(
        propertyName: String,
        propertyValue: () -> String?,
        emulatorCanary: (String?) -> Boolean = { false },
        probablyEmulatorCanary: (String?) -> Boolean = { false }
    ): Pair<String, EmulatorProbability> {
        fun getProbability(): ProbabilityResult {
            val value = propertyValue()
            var probability = EmulatorProbability.NOT_EMULATOR

            if (emulatorCanary(value)) probability = EmulatorProbability.IS_EMULATOR
            if (probablyEmulatorCanary(value)) probability = EmulatorProbability.PROBABLY_EMULATOR

            return value to probability
        }

        val (value, probability) = safeCheck(::getProbability)
        val entry = "$propertyName: $value"
        return entry to probability
    }

    private fun checkBuildPropProperty(
        propertyName: String,
        emulatorCanary: (String?) -> Boolean = { false }
    ): Pair<String, EmulatorProbability> {
        return checkProperty(
            propertyName = "$propertyName in /build.prop",
            propertyValue = {
                val properties = Properties()
                properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
                properties.getProperty(propertyName)
            }, emulatorCanary = emulatorCanary
        )
    }

    private fun checkFile(
        filePath: String,
        emulatorCanary: (Boolean) -> Boolean = { fileExists -> fileExists },
        probablyEmulatorCanary: (Boolean?) -> Boolean = { false }
    ): Pair<String, EmulatorProbability> {
        val fileExists = filePath.fileExists()
        fun getProbability(): EmulatorProbability {
            if (emulatorCanary(fileExists)) return EmulatorProbability.IS_EMULATOR
            if (probablyEmulatorCanary(fileExists)) return EmulatorProbability.PROBABLY_EMULATOR
            return EmulatorProbability.NOT_EMULATOR
        }

        val entry = "$filePath file ${if (fileExists) "was found" else "not found"}"
        return entry to getProbability()
    }

    private fun readCpuInfo(): Pair<String, EmulatorProbability> {
        fun getProbability(): SafeProbabilityResult {
            val process = ProcessBuilder("/system/bin/cat", "/proc/cpuinfo").start()
            val stringBuffer = StringBuffer()
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream, "utf-8"))
            while (true) {
                bufferedReader.readLine()?.let(stringBuffer::append) ?: break
            }
            bufferedReader.close()
            val cpuInfo = stringBuffer.toString().lowercase()

            val probability = if (cpuInfo.includes("intel") || cpuInfo.includes("amd")) EmulatorProbability.NOT_EMULATOR else EmulatorProbability.IS_EMULATOR
            return cpuInfo to probability
        }

        val (_, probability) = safeCheck { getProbability() }
        val entry = if (probability == EmulatorProbability.NOT_EMULATOR) "/proc/cpuinfo includes 'amd' or 'intel'" else "/proc/cpuinfo does not include 'amd' or 'intel'"
        return entry to probability
    }

    private fun checkDefaultBluetoothAdapterPresence(): Pair<String, EmulatorProbability> {
        val isDefaultAdapterPresent = BluetoothAdapter.getDefaultAdapter() != null
        val probability = if (isDefaultAdapterPresent) EmulatorProbability.NOT_EMULATOR else EmulatorProbability.IS_EMULATOR

        val entry = "Is default bluetooth adapter present: $isDefaultAdapterPresent"
        return entry to probability
    }

    private fun checkDefaultBluetoothAdapterName(context: Context): Pair<String, EmulatorProbability> {
        val isRequiredPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        if (!isRequiredPermissionGranted) return "BLUETOOTH_CONNECT permission isn't granted" to EmulatorProbability.NOT_EMULATOR

        return checkProperty("BluetoothAdapter.getDefaultAdapter().name", propertyValue = {
            BluetoothAdapter.getDefaultAdapter().name
        }, probablyEmulatorCanary = { it.isNullOrEmpty() })
    }

    private fun checkAmbientLightSensor(context: Context): SafeProbabilityResult {
        val isAmbientLightSensorPresent = (context.getSystemService("sensor") as SensorManager).getDefaultSensor(5) != null
        val probability = if (isAmbientLightSensorPresent) EmulatorProbability.NOT_EMULATOR else EmulatorProbability.IS_EMULATOR

        val entry = "Ambient light sensor detected: $isAmbientLightSensorPresent"
        return entry to probability
    }

    private fun safeCheck(emulatorCheck: () -> ProbabilityResult): ProbabilityResult {
        return runCatching { emulatorCheck() }.getOrNull() ?: (null to EmulatorProbability.NOT_EMULATOR)
    }
}

typealias ProbabilityResult = Pair<String?, EmulatorProbability>
typealias SafeProbabilityResult = Pair<String, EmulatorProbability>