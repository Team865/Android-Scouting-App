@file:Suppress("unused")

package ca.warp7.android.scouting.entry

fun String.toBoard() = Board.values().firstOrNull { it.name == this }