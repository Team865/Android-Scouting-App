package ca.warp7.android.scouting

import android.content.Context
import android.preference.PreferenceManager

/**
 * @since v0.4.1
 */

class ManagedPreferences(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val vibrator: AbstractActionVibrator = ActionVibrator(
        context, sharedPreferences.getBoolean(context.getString(R.string.pref_use_vibration_key), true)
    )
}
