package ca.warp7.android.scouting.model2019

data class DataPoint(
    val type: Byte,
    val value: Byte,
    val time: Byte
) {
    val byteArray get() = byteArrayOf(type, value, time)
}