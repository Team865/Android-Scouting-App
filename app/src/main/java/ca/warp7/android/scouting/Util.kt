package ca.warp7.android.scouting

import android.content.Context
import android.preference.PreferenceManager
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

    private const val v5Root = "ca.warp7.android.scouting.intent"

    const val kMatch = "$v5Root.match"
    const val kTeam = "$v5Root.team"
    const val kScout = "$v5Root.scout"
    const val kBoard = "$v5Root.board"

    const val kResult = "$v5Root.result"
}

object MainSettingsKey {

    private const val kMainSettingsRoot = "ca.warp7.android.scouting.main"

    const val kBoard = "$kMainSettingsRoot.board"
    const val kScout = "$kMainSettingsRoot.scout"
}

class ManagedPreferences(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val vibrator: AbstractActionVibrator = ActionVibrator(
        context, sharedPreferences.getBoolean(context.getString(R.string.pref_use_vibration_key), true)
    )
}
