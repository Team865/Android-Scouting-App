package ca.warp7.android.scouting

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.ListPreference
import android.preference.PreferenceManager
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import org.json.JSONArray
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

/**
 * @since v0.4.1
 */

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        this.context
        findPreference(getString(R.string.pref_licenses_key)).setOnPreferenceClickListener {
            val intent = Intent(context, LicensesActivity::class.java)
            it.context.startActivity(intent)
            true
        }



        findPreference(getString(R.string.event_name)).setOnPreferenceClickListener {
            val handler = Handler(Looper.getMainLooper())
            val listEvents = mutableListOf<String>()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val teamNumber = sharedPreferences.getString(getString(R.string.team_number), "").toString()

            fun updateEntries(){
                handler.post {  val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listEvents)
                    AlertDialog.Builder(context).setTitle("Select events").setSingleChoiceItems(adapter, 0){
                            _, _ ->
                        println("abc")

                    }.create().show() }

            }

            fun handleData(data : String){
                println("In handling data")
                val JSONarr = JSONArray(data)
                for (i in 0 until JSONarr.length()) {
                    val JSONObj = JSONarr.getJSONObject(i)
                    listEvents.add(JSONObj.getString("name"))
                }
                println(listEvents)
                updateEntries()
            }

            val thread = Thread {
                val events: String

                println("In thread")
                try {
                    val url = URL("https://www.thebluealliance.com/api/v3/team/frc$teamNumber/events/2019/simple")
                    val connection = url.openConnection()
                    connection.addRequestProperty("User-Agent", "User-agent")
                    connection.setRequestProperty("X-TBA-Auth-Key", "NTFtIarABYtYkZ4u3VmlDsWUtv39Sp5kiowxP1CArw3fiHi3IQ0XcenrH5ONqGOx")

                    println("I'm trying")

                    events = InputStreamReader(connection.getInputStream()).readText()

                    println("Tried, getting data")

                    handleData(events)
                } catch (e: Exception) {
                    println("Failed")

                    /*handler.post {
                        val toast = Toast.makeText(context, "Invalid key", LENGTH_SHORT)
                        toast.setGravity(Gravity.BOTTOM, Gravity.CENTER, 0)
                        toast.show()
                    }*/

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