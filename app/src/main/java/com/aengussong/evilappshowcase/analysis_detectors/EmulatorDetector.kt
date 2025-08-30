package com.aengussong.evilappshowcase.analysis_detectors

import android.os.Build
import android.telephony.TelephonyManager
import com.aengussong.evilappshowcase.util.beginsWith
import com.aengussong.evilappshowcase.util.fileExists
import com.aengussong.evilappshowcase.util.includes

enum class EmulatorProbability {
    IS_EMULATOR,
    PROBABLY_EMULATOR,
    NOT_EMULATOR
}

object EmulatorDetector {
    fun emulatorCheckup(telephonyManager: TelephonyManager): Map<String, EmulatorProbability> {
        return mapOf(
            checkProperty("Build.CPU_ABI", Build.CPU_ABI, probablyEmulatorCanary = { it == "armeabi" }),
            checkProperty("Build.CPU_ABI2", Build.CPU_ABI2, probablyEmulatorCanary = { it == "unknown" }),
            checkProperty("Build.BOARD", Build.BOARD, emulatorCanary = { it == "unknown" }),
            checkProperty("Build.BRAND", Build.BRAND, emulatorCanary = { it.beginsWith("generic") }),
            checkProperty("Build.DEVICE", Build.DEVICE, emulatorCanary = { it.beginsWith("generic") }),
            checkProperty("Build.FINGERPRINT", Build.FINGERPRINT, emulatorCanary = {
                it.beginsWith("generic") || it.beginsWith("unknown")
            }),
            checkProperty("Build.HARDWARE", Build.HARDWARE, emulatorCanary = { it.includes("goldfish") }),
            checkProperty("Build.HOST", Build.HOST, probablyEmulatorCanary = { it == "android-test" }),
            checkProperty("Build.ID", Build.ID, emulatorCanary = { it == "FRF91" }),
            checkProperty("Build.MANUFACTURER", Build.MANUFACTURER, emulatorCanary = {
                it == "unknown" || it.includes("Genymotion")
            }),
            checkProperty("Build.MODEL", Build.MODEL, emulatorCanary = {
                it == "sdk" ||
                        it.includes("google_sdk") ||
                        it.includes("Emulator") ||
                        it.includes("Android SDK built for x86")
            }),
            checkProperty("Build.PRODUCT", Build.PRODUCT, emulatorCanary = {
                it == "sdk" ||
                        it == "google_sdk" ||
                        it == "sdk_x86"
            }),
            checkProperty("Build.RADIO", Build.RADIO, probablyEmulatorCanary = { it == "unknown" }),
            checkProperty(
                "Build.SERIAL",
                Build.SERIAL,
                emulatorCanary = { it == null || it == "null" }),
            checkProperty("Build.USER", Build.USER, emulatorCanary = { it == "android-build" }),

            checkProperty("TelephonyManager.getNetworkCountryIso()", telephonyManager.networkCountryIso, probablyEmulatorCanary = { it == "us" }),
            checkProperty("TelephonyManager.getNetworkOperator().substring(0,3)", telephonyManager.networkOperator?.substring(0, 3), probablyEmulatorCanary = { it == "310" }),
            checkProperty("TelephonyManager.getNetworkOperator().substring(3)", telephonyManager.networkOperator?.substring(3), probablyEmulatorCanary = { it == "260" }),
            checkProperty("TelephonyManager.getPhoneType()", telephonyManager.phoneType.toString(), probablyEmulatorCanary = { it == "1" }),
            checkProperty("TelephonyManager.getSimCountryIso()", telephonyManager.simCountryIso, probablyEmulatorCanary = { it == "us" }),
            checkProperty("TelephonyManager.getNetworkOperatorName()", telephonyManager.networkOperatorName, probablyEmulatorCanary = { it == "Android" }),

            checkProperty("ro.kernel.qemu", System.getProperty("ro.kernel.qemu"), emulatorCanary = { it != null}),

            checkFile("/dev/docket/qemud"),
            checkFile("/dev/qemu_pipe"),
            checkFile("/system/lib/libc_malloc_debug_qemu.so"),
            checkFile("/sys/qemu_trace"),
            checkFile("/system/bin/qemu_props")
        )
    }

    private fun checkProperty(
        propertyName: String,
        propertyValue: String?,
        emulatorCanary: (String?) -> Boolean = { false },
        probablyEmulatorCanary: (String?) -> Boolean = { false }
    ): Pair<String, EmulatorProbability> {
        fun getProbability(): EmulatorProbability {
            if (emulatorCanary(propertyValue)) return EmulatorProbability.IS_EMULATOR
            if (probablyEmulatorCanary(propertyValue)) return EmulatorProbability.PROBABLY_EMULATOR

            return EmulatorProbability.NOT_EMULATOR
        }

        val entry = "$propertyName: $propertyValue"
        return entry to getProbability()
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
}
