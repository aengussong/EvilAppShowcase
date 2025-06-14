package com.aengussong.evilappshowcase.obfuscation

class NativeLibrariesExecutor {

    fun executeNativeLibs() {
        noObfuscation()
        armaririsObfuscation()
    }

    private external fun noObfuscation()
    private external fun armaririsObfuscation()

    companion object {
        init {
            System.loadLibrary("no_obfuscation")
            System.loadLibrary("armariris_example")
        }
    }
}