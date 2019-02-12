@file:Suppress("unused")

package ca.warp7.android.scouting.v5.entry

fun String.toBoard() = Board.values().firstOrNull { it.name == this }