package com.aengussong.evilappshowcase

import android.app.Application
import com.aengussong.evilappshowcase.obfuscation.NativeLibrariesExecutor

class EvilShowcaseApp: Application() {

    override fun onCreate() {
        super.onCreate()

        NativeLibrariesExecutor().executeNativeLibs()
    }
}