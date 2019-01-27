package ca.warp7.android.scouting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.preference.Preference
import ca.warp7.android.scouting.v4.V4ScheduleActivity

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
        val intent = Intent(context, V4ScheduleActivity::class.java)
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
                        + AppResources.specsRoot.absolutePath
                        + "\" and \""
                        + AppResources.eventsRoot.absolutePath
                        + "\" will be overwritten."
            )
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _ ->
                AppResources.copySpecsAssets(context)
                AppResources.copyEventAssets(context)
            }
            .create().show()
    }
}
