package com.jobcoin

import java.text.SimpleDateFormat
import java.util.*

fun String.isNumeric(): Boolean {
    try {
        this.toDouble()
    } catch (e: NumberFormatException) {
        return false
    }

    return true
}

fun String.toFullDate(): Date {
    //"2014-04-23T18:25:43.511Z"
    val sdf = SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss.SSSX", Locale.ENGLISH)
    return sdf.parse(this) ?: throw Exception("Invalid Date")
}

// We don't have to worry about data loss while converting from Float -> Long
// in this instance because the float will contain trailing zeroes
fun String.timestampToFloat(): Float {
    return this.toFullDate().time.toFloat()
}