package com.aengussong.evilappshowcase.util

fun String?.beginsWith(other: String): Boolean {
    return this?.startsWith(other) ?: false
}

fun String?.includes(other: String): Boolean {
    return this?.contains(other) ?: false
}
