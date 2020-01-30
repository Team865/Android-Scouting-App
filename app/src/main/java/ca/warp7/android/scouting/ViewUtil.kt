package ca.warp7.android.scouting

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.View

fun View.show(){
    visibility = View.VISIBLE
}

fun View.hide(){
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


object ScoutingIntentKey {
    const val kScout = "intent.scout"
    const val kEntryInMatch = "intent.entry"
}

fun validateName(str: String): Boolean {
    val name = str.trim()
    if (name.isEmpty()) return false
    val split = name.split(" ")
    return split.size >= 2 && split[0][0].isUpperCase() &&
            split.subList(1, split.size).all { it.length == 1 && it[0].isUpperCase() }
}