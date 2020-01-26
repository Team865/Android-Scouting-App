package ca.warp7.android.scouting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ca.warp7.android.scouting.tba.EventSimple
import ca.warp7.android.scouting.tba.getEventMatchesSimple
import ca.warp7.android.scouting.tba.getTeamEventsByYearSimple
import kotlin.concurrent.thread

/**
 * @since v0.4.1
 */

class SettingsFragment : PreferenceFragmentCompat() {

    private fun updateEntries(listEvents: List<EventSimple>) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val currentKey = sharedPreferences.getString(getString(R.string.pref_event_key), "") ?: ""
        var i = listEvents.indexOfFirst { it.key == currentKey }
        if (i == -1) {
            i = 0
        }

        // need to make sure we call the dialog on the UI thread
        activity?.runOnUiThread {
            AlertDialog.Builder(context).setTitle("Select events")
                .setSingleChoiceItems(listEvents.map { it.name }.toTypedArray(), i) { dialog, which ->
                    val eventKey = listEvents[which]
                    sharedPreferences
                        .edit()
                        .putString(getString(R.string.pref_event_key), eventKey.key!!)
                        .putString(getString(R.string.pref_event_name), eventKey.name!!)
                        .apply()
                    dialog.dismiss()
                    val tba = createCachedTBAInstance(context!!)
                    thread {
                        // pre-fetch the event matches so it's cached
                        // we don't process anything here
                        tba.getEventMatchesSimple(eventKey.key)
                    }
                }.create().show()
        }
    }

    private fun showEventList() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val teamNumber = sharedPreferences.getString(getString(R.string.pref_team_key), "") ?: ""
        val tba = createCachedTBAInstance(context!!)

        thread {
            try {
                val events = tba.getTeamEventsByYearSimple("frc$teamNumber", 2019)
                updateEntries(events)
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    AlertDialog.Builder(context)
                        .setTitle("Error Retrieving Event")
                        .setMessage(e.localizedMessage)
                        .create().show()
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>(getString(R.string.pref_licenses_key))?.setOnPreferenceClickListener {
            val intent = Intent(context, LicensesActivity::class.java)
            it.context.startActivity(intent)
            true
        }

        findPreference<Preference>(getString(R.string.pref_team_key))?.setOnPreferenceClickListener {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val input = EditText(context)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            input.layoutParams = lp
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Enter team number")
                .setView(input)
                .setPositiveButton("OK") { _, _ -> }
                .setNegativeButton("CANCEL") { _, _ -> }

            val dialog = builder.create()
            input.setText(sharedPreferences.getString(getString(R.string.pref_team_key), ""))

            input.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (input.text.isNotEmpty() &&
                        input.text.toString().matches("-?\\d+(\\.\\d+)?".toRegex()) &&
                        input.text.toString().length <= 4
                    ) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        sharedPreferences.edit()
                            .putString(getString(R.string.pref_team_key), input.text.toString())
                            .apply()
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    }
                }
            })

            dialog.show()

            true
        }

        findPreference<Preference>(getString(R.string.pref_event_key))?.setOnPreferenceClickListener {
            showEventList()
            true
        }

        val aboutApp = findPreference<Preference>(getString(R.string.pref_about_key))
        if (aboutApp != null) {
            aboutApp.summary = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
        }
    }
}

