package ca.warp7.android.scouting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import ca.warp7.android.scouting.tba.EventSimple
import ca.warp7.android.scouting.tba.createCachedTBAInstance
import ca.warp7.android.scouting.tba.getEventMatchesSimple
import ca.warp7.android.scouting.tba.getTeamEventsByYearSimple
import ca.warp7.android.scouting.ui.EventListAdapter
import kotlin.concurrent.thread


class EventSelectionActivity : AppCompatActivity() {

    private val eventList = ArrayList<EventSimple>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_selection)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.apply {
            title = getString(R.string.select_frc_event)
            setDisplayHomeAsUpEnabled(true)
        }

        val teamSearch = findViewById<EditText>(R.id.team_search)
        val yearEdit = findViewById<EditText>(R.id.year)
        teamSearch.setOnEditorActionListener { textView, _, _ ->
            onSearch(textView, yearEdit, cacheFirst = false)
            true
        }

        val eventListView = findViewById<ListView>(R.id.event_list)
        val adapter = EventListAdapter(this, eventList)
        eventListView.adapter = adapter
        eventListView.setOnItemClickListener { _, _, position, _ ->
            onEventListItemClicked(adapter, position,
                    teamSearch.text.toString().toInt(),
                    yearEdit.text.toString().toInt()
            )
        }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        val year = sharedPref.getString(PreferenceKeys.kYear, "2020")
        yearEdit.setText(year, TextView.BufferType.EDITABLE)

        val teamNumber = sharedPref.getString(PreferenceKeys.kTeam, "")!!
        teamSearch.setText(teamNumber, TextView.BufferType.EDITABLE)

        if (teamNumber.isNotEmpty()) {
            // show previous results
            onSearch(teamSearch, yearEdit, cacheFirst = true)
        }
    }

    private fun onEventListItemClicked(
            adapter: EventListAdapter,
            position: Int,
            teamNumber: Int,
            year: Int
    ) {
        val event = adapter.getItem(position) ?: return
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.team_template, teamNumber))
                .setMessage(getString(R.string.override_message_template, event.year, event.name))
                .setPositiveButton(getString(R.string.button_continue)) { dialog, _ ->
                    dialog.dismiss()
                    onContinueSetEvent(teamNumber, event, year)
                }
                .setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.dismiss() }
                .create().show()
    }

    private fun onContinueSetEvent(teamNumber: Int, event: EventSimple, year: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences
                .edit()
                .putString(PreferenceKeys.kTeam, teamNumber.toString())
                .putString(PreferenceKeys.kEventKey, event.key!!)
                .putString(PreferenceKeys.kEventName, event.name)
                .putString(PreferenceKeys.kYear, year.toString())
                .apply()
        thread {
            // pre-fetch the event matches so it's cached
            // we don't process anything here. Make sure to catch errors
            try {
                createCachedTBAInstance(this, cacheFirst = false)
                        .getEventMatchesSimple(event.key)
            } catch (ignored: Exception) {
            }
        }

        val intent = Intent(this, MainActivity::class.java)

        // this make so that we go back instead of restarting the activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun onSearch(textView: TextView, yearEdit: EditText, cacheFirst: Boolean) {
        val teamNumber = textView.text.toString()

        // https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard
        val inputManager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = currentFocus
        if (focus != null) {
            inputManager.hideSoftInputFromWindow(
                    focus.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        textView.clearFocus()
        val year = yearEdit.text.toString().toInt()

        thread {
            try {
                val result = createCachedTBAInstance(this, cacheFirst)
                        .getTeamEventsByYearSimple("frc$teamNumber", year)
                        .sortedBy { it.start_date }
                // update the events on the UI thread
                runOnUiThread {
                    updateListView(result)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    AlertDialog.Builder(this)
                            .setTitle(getString(R.string.error_retrieving_data))
                            .setMessage(getString(R.string.check_connection))
                            .create().show()
                }
            }
        }
    }

    private fun updateListView(result: List<EventSimple>) {
        val lv = findViewById<ListView>(R.id.event_list)
        eventList.clear()
        eventList.addAll(result)
        (lv.adapter as EventListAdapter).notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
