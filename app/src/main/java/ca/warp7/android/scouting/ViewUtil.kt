package ca.warp7.android.scouting

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.View
import ca.warp7.android.scouting.entry.Board
import ca.warp7.android.scouting.entry.RelativeBoard
import ca.warp7.android.scouting.ui.EntryInMatch

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}


private fun getRaw(context: Context, id: Int): String {
    val resources = context.resources
    return try {
        resources.openRawResource(id).bufferedReader().readText()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

@Suppress("DEPRECATION")
fun getHTMLFromContext(context: Context, id: Int): Spanned {
    return Html.fromHtml(getRaw(context, id))
}

const val kTimerLimit = 150
const val kAutonomousTime = 15
const val kFadeDuration = 100
const val kTotalTimerDigits = 3

const val kScoutIntent = "intent.scout"
const val kEntryInMatchIntent = "intent.entry"

fun validateName(str: String): Boolean {
    val name = str.trim()
    if (name.isEmpty()) return false
    val split = name.split(" ")
    return split.size >= 2 && split[0][0].isUpperCase() &&
            split.subList(1, split.size).all { it.length == 1 && it[0].isUpperCase() }
}

val boards = Board.values()
val relBoards = RelativeBoard.values()

@SuppressLint("DefaultLocale")
fun modifyNameForDisplay(eim: EntryInMatch?, name: String): String {
    var varName = name
    if (eim != null && eim.teams.size > 5) {
        for (i in 0 until 6) {
            varName = varName.replace(boards[i].name, eim.teams[i].toString())
        }
        for (i in 0 until 6) {
            val relBoard = relBoards[i]
            val team = eim.teams[relBoard.relativeTo(eim.board).ordinal].toString()
            varName = varName.replace(relBoard.name, team)
        }
    }
    return varName.split("_".toRegex()).joinToString(" ") { it.capitalize() }
}