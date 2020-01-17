package ca.warp7.android.scouting

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import org.json.JSONArray
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

/**
 * @since v0.4.1
 */

@Suppress(
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class SettingsFragment : PreferenceFragmentCompat() {

    private fun updateEntries(listEvents: List<String>) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val handler = Handler(Looper.getMainLooper())
        val currentEvent = sharedPreferences.getString("eventName", "")
        var i = listEvents.indexOf(currentEvent)
        if (i == -1) i = 0
        handler.post {
            AlertDialog.Builder(context).setTitle("Select events")
                .setSingleChoiceItems(listEvents.toTypedArray(), i) { dialog, which ->
                    listEvents[which].also {
                        //println(it)
                        sharedPreferences.edit().putString("eventName", it).apply()
                    }
                    dialog.dismiss()
                }.create().show()
        }
    }

    private fun handleData(eventData: String) {

        val listEvents = mutableListOf<String>()
        println("In handling data")
        val events = JSONArray(eventData)
        for (i in 0 until events.length()) {
            val event = events.getJSONObject(i)
            listEvents.add(event.getString("name"))
        }
//        println(listEvents)
        updateEntries(listEvents)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference(getString(R.string.pref_licenses_key)).setOnPreferenceClickListener {
            val intent = Intent(context, LicensesActivity::class.java)
            it.context.startActivity(intent)
            true

        }

        findPreference(getString(R.string.pref_team_number)).setOnPreferenceClickListener {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val input = EditText(context)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            input.layoutParams = lp
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Enter team number")
                    .setView(input)
                    .setPositiveButton("OK", null)
                    .setNegativeButton("CANCEL", null)
                val dialog = builder.create()
                input.setText(sharedPreferences.getString("teamNumber", ""))
                dialog.setOnShowListener {
                    val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    button.setOnClickListener {
                        if (input.text.toString().replace("\\D+", "").length > 4) {
                            val toast = Toast.makeText(
                                context,
                                "Team number must not contain letters",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                            input.setText("")
                        } else {
                            sharedPreferences.edit().putString("teamNumber", input.text.toString())
                                .apply()
                            dialog.dismiss()
                        }
                    }

                }
                dialog.show()
            }
            true
        }
        findPreference(getString(R.string.pref_event_key)).setOnPreferenceClickListener {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val teamNumber = sharedPreferences.getString("teamNumber", "").toString()

            val thread = Thread {
                val events: String
//                println("In thread")
                try {
                    val url =
                        URL("https://www.thebluealliance.com/api/v3/team/frc$teamNumber/events/2019/simple")
                    val connection = url.openConnection()
                    connection.addRequestProperty("User-Agent", "User-agent")
                    connection.setRequestProperty(
                        "X-TBA-Auth-Key",
                        "NTFtIarABYtYkZ4u3VmlDsWUtv39Sp5kiowxP1CArw3fiHi3IQ0XcenrH5ONqGOx"
                    )

                    println("I'm trying")

                    events = InputStreamReader(connection.getInputStream()).readText()

                    println("Tried, getting data")
                    handleData(events)

                } catch (e: Exception) {
                    println("Failed")
                    e.printStackTrace()
                }
            }
            thread.start()
            true
        }

        val aboutApp = findPreference(getString(R.string.pref_about_key))
        aboutApp.summary = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
    }
}

