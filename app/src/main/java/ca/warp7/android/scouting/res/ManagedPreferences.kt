package ca.warp7.android.scouting.res

import android.content.Context
import android.preference.PreferenceManager
import ca.warp7.android.scouting.AbstractActionVibrator
import ca.warp7.android.scouting.R

/**
 * @since v0.4.1
 */

class ManagedPreferences(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val vibrator: AbstractActionVibrator = ActionVibrator(
        context, sharedPreferences.getBoolean(getString(R.string.pref_use_vibration_key), true)
    )

    fun shouldShowPause() = sharedPreferences.getBoolean(getString(R.string.pref_show_pause_key), false)

    private fun getString(id: Int) = context.getString(id)
}
