package com.aengussong.evilappshowcase.analysis_detectors

enum class FridaProbability {
    FRIDA_DETECTED,
    NOT_DETECTED
}

object FridaDetector {
    fun fridaCheckup(): Map<String, FridaProbability> = mapOf(
        checkClass("re.frida.ServerManager"),
        checkClass("frida"),
        checkClass("fum-js-loop")
    )

    private fun checkClass(
        className: String
    ): Pair<String, FridaProbability> {
        val classExists = kotlin.runCatching {
            Class.forName(className)
        }.getOrNull() != null

        val probability = if (classExists) FridaProbability.FRIDA_DETECTED else FridaProbability.NOT_DETECTED

        return "$className class ${if (classExists) "found" else "not found"}" to probability
    }
}
