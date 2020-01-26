package ca.warp7.android.scouting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.json.JSONArray
import java.io.InputStreamReader
import java.net.URL

/**
 * @since v0.4.1
 */

class SettingsFragment : PreferenceFragmentCompat() {

    private fun updateEntries(listEvents: List<String>) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val handler = Handler(Looper.getMainLooper())
        val currentEvent = sharedPreferences.getString("eventName", "") ?: ""
        var i = listEvents.indexOf(currentEvent)
        if (i == -1) i = 0
        handler.post {
            AlertDialog.Builder(context).setTitle("Select events")
                .setSingleChoiceItems(listEvents.toTypedArray(), i) { dialog, which ->
                    listEvents[which].also {
                        sharedPreferences.edit().putString("eventName", it).apply()
                    }
                    dialog.dismiss()
                }.create().show()
        }
    }

    private fun handleData(eventData: String) {
        val listEvents = mutableListOf<String>()
        val events = JSONArray(eventData)
        for (i in 0 until events.length()) {
            val event = events.getJSONObject(i)
            listEvents.add(event.getString("name"))
        }
        updateEntries(listEvents)
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
            input.setText(sharedPreferences.getString("teamNumber", ""))
            input.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (input.text.isNotEmpty() &&
                        input.text.toString().matches("-?\\d+(\\.\\d+)?".toRegex()) &&
                        input.text.toString().length <= 4) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        sharedPreferences.edit().putString("teamNumber", input.text.toString())
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

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val teamNumber = sharedPreferences.getString(getString(R.string.pref_team_key), "") ?: ""

            val thread = Thread {
                val events: String
                try {
                    val url =
                        URL("https://www.thebluealliance.com/api/v3/team/frc$teamNumber/events/2019/simple")
                    val connection = url.openConnection()
                    connection.addRequestProperty("User-Agent", "User-agent")
                    connection.setRequestProperty("X-TBA-Auth-Key", BuildConfig.TBA_KEY)

                    events = InputStreamReader(connection.getInputStream()).readText()

                    handleData(events)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
            true
        }

        val aboutApp = findPreference<Preference>(getString(R.string.pref_about_key))
        if (aboutApp != null) {
            aboutApp.summary = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
        }
    }
}

