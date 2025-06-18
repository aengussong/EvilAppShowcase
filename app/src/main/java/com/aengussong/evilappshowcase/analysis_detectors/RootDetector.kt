package com.aengussong.evilappshowcase.analysis_detectors

import android.content.pm.PackageManager
import com.aengussong.evilappshowcase.util.fileExists

enum class RootProbability {
    ROOT_DETECTED,
    NOT_DETECTED
}

object RootDetector {
    fun rootCheckup(packageManager: PackageManager): Map<String, RootProbability> = mapOf(
        checkFile("/sbin/su"),
        checkFile("/system/bin/su"),
        checkFile("/system/xbin/su"),
        checkFile("/data/local/xbin/su"),
        checkFile("/data/local/bin/su"),
        checkFile("/system/sd/xbin/su"),

        checkPackage(packageManager, "de.robv.android.xposed.installer"),
        checkPackage(packageManager, "com.topjohnwu.magisk"),
        checkPackage(packageManager, "frida"),
        checkPackage(packageManager, "com.devadvance.rootcloak")
    )

    private fun checkFile(filePath: String): Pair<String, RootProbability> {
        val fileExists = filePath.fileExists()

        val probability = if (fileExists) RootProbability.ROOT_DETECTED else RootProbability.NOT_DETECTED

        return "$filePath file ${if (fileExists) "found" else "not found"}" to probability
    }

    private fun checkPackage(packageManager: PackageManager, packageName: String): Pair<String, RootProbability> {
        val packageExists = kotlin.runCatching { packageManager.getPackageInfo(packageName, 0) }.getOrNull() != null

        val probability = if (packageExists) RootProbability.ROOT_DETECTED else RootProbability.NOT_DETECTED

        return "$packageName package ${if (packageExists) "found" else "not found"}" to probability
    }
}
