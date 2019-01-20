@file:Suppress("unused")

package ca.warp7.android.scouting.model2019

import android.util.Base64

data class DataPoint(
    val type: Byte,
    val value: Byte,
    val time: Byte
) {
    fun toBase64(): String {
        return Base64.encodeToString(byteArrayOf(type, value, time), Base64.DEFAULT)
    }

    companion object {
        fun fromBase64(string: String): DataPoint {
            return Base64.decode(string, Base64.DEFAULT)
                .let { DataPoint(it[0], it[1], it[2]) }
        }
    }
}