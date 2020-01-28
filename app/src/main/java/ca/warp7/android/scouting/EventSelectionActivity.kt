package ca.warp7.android.scouting

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ca.warp7.android.scouting.tba.EventSimple
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
            title = "Select FRC Event"
            setDisplayHomeAsUpEnabled(true)
        }
        val teamSearch = findViewById<EditText>(R.id.team_search)
        val yearEdit = findViewById<EditText>(R.id.year)
        teamSearch.setOnEditorActionListener { textView, _, _ ->
            onSearch(textView, yearEdit)
            true
        }
        val eventListView = findViewById<ListView>(R.id.event_list)
        val adapter = EventListAdapter(this, eventList)
        eventListView.adapter = adapter
        eventListView.setOnItemClickListener { _, _, position, _ ->
            onEventListItemClicked(adapter, position, teamSearch)
        }
        yearEdit.setText(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("year", 2020).toString(), TextView.BufferType.EDITABLE
        )

        val teamNumber = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.pref_team_key), "")!!
        teamSearch.setText(teamNumber, TextView.BufferType.EDITABLE)
    }

    private fun onEventListItemClicked(adapter: EventListAdapter, position: Int, teamSearch: EditText) {
        val event = adapter.getItem(position) ?: return
        val teamNumber = teamSearch.text.toString().toInt()
        AlertDialog.Builder(this)
            .setTitle("Team $teamNumber")
            .setMessage("Select \"${event.year} ${event.name}\" as the event? This will load the match schedule and delete entries from other events")
            .setPositiveButton("Continue") {dialog, _ ->
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                preferences
                    .edit()
                    .putString(getString(R.string.pref_team_key), teamNumber.toString())
                    .putString(getString(R.string.pref_event_key), event.key!!)
                    .putString(getString(R.string.pref_event_name), event.name)
                    .apply()
                thread {
                    // pre-fetch the event matches so it's cached
                    // we don't process anything here
                    createCachedTBAInstance(this).getEventMatchesSimple(event.key)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") {dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun onSearch(textView: TextView, yearEdit: EditText) {
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
                val result = createCachedTBAInstance(this)
                    .getTeamEventsByYearSimple("frc$teamNumber", year)
                    .sortedBy { it.start_date }
                // update the events on the UI thread
                runOnUiThread {
                    updateListView(result)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(e.toString())
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.event_selection_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_unlisted_event -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
