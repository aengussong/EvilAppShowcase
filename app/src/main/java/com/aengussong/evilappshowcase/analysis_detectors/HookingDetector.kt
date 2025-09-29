package com.aengussong.evilappshowcase.analysis_detectors

enum class HookingProbability {
    HOOKING_DETECTED,
    NOT_DETECTED
}

object HookingDetector {
    fun hookingCheckup(): Map<String, HookingProbability> = fridaCheckup() + mapOf(
        checkStackTrace()
    )

    private fun fridaCheckup(): Map<String, HookingProbability> = mapOf(
        checkClass("re.frida.ServerManager"),
        checkClass("frida"),
        checkClass("fum-js-loop")
    )

    private fun checkClass(
        className: String
    ): Pair<String, HookingProbability> {
        val classExists = kotlin.runCatching {
            Class.forName(className)
        }.getOrNull() != null

        val probability = if (classExists) HookingProbability.HOOKING_DETECTED else HookingProbability.NOT_DETECTED

        return "$className class ${if (classExists) "found" else "not found"}" to probability
    }

    private fun checkStackTrace(): Pair<String, HookingProbability> {
        val suspiciousValues = setOf("hook", "frida", "xpose")
        
        var detectedValue: String? = null
        kotlin.runCatching {
            for (stackTraceElement: StackTraceElement in Thread.currentThread().stackTrace) {
                val trace = "${stackTraceElement.className}#${stackTraceElement.methodName}"
                detectedValue = suspiciousValues.find { trace.lowercase().contains(it) }
                if (detectedValue != null) break
            }
        }

        val stacktraceContainsSuspiciousValues = detectedValue != null
        val probability = if (stacktraceContainsSuspiciousValues) HookingProbability.HOOKING_DETECTED else HookingProbability.NOT_DETECTED
        val entry = if (stacktraceContainsSuspiciousValues) "\"$detectedValue\" detected in the stacktrace" else "Nothing suspicious in the stacktrace"

        return entry to probability
    }
}
