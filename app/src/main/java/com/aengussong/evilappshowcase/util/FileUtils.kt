package com.aengussong.evilappshowcase.util

import java.io.File

fun String.fileExists(): Boolean = kotlin.runCatching { File(this).exists() }.getOrNull() ?: false
