package ca.warp7.android.scouting.components

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.preference.Preference
import ca.warp7.android.scouting.LicensesActivity
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.ScheduleActivity
import ca.warp7.android.scouting.res.AppResources

/**
 * @since v0.4.1
 */

internal class SettingsClickListener : Preference.OnPreferenceClickListener {
    override fun onPreferenceClick(preference: Preference): Boolean {
        val context = preference.context
        val key = preference.key
        when (key) {
            context.getString(R.string.pref_copy_assets_key) -> onCopyAssets(context)
            context.getString(R.string.pref_x_schedule_key) -> onScheduleActivityIntent(context)
            context.getString(R.string.pref_licenses_key) -> onLicensesIntent(context)
            else -> return false
        }
        return true
    }

    private fun onScheduleActivityIntent(context: Context) {
        val intent = Intent(context, ScheduleActivity::class.java)
        context.startActivity(intent)
    }

    private fun onLicensesIntent(context: Context) {
        val intent = Intent(context, LicensesActivity::class.java)
        context.startActivity(intent)
    }

    private fun onCopyAssets(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Are you sure?")
            .setMessage(
                "Any files stored at \""
                        + AppResources.getSpecsRoot().absolutePath
                        + "\" and \""
                        + AppResources.getEventsRoot().absolutePath
                        + "\" will be overwritten."
            )
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { dialog, which ->
                AppResources.copySpecsAssets(context)
                AppResources.copyEventAssets(context)
            }
            .create().show()
    }
}
