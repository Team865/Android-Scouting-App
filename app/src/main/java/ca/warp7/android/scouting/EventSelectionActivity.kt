package ca.warp7.android.scouting

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import ca.warp7.android.scouting.tba.EventSimple
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
        teamSearch.setOnEditorActionListener { v, _, _ ->
            val teamNumber = v.text.toString()

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
            v.clearFocus()

            thread {
                val result = createCachedTBAInstance(this)
                    .getTeamEventsByYearSimple("frc$teamNumber", 2019)
                    .sortedBy { it.start_date }
                // update the events on the UI thread
                runOnUiThread {
                    updateListView(result)
                }
            }
            true
        }

        findViewById<ListView>(R.id.event_list).adapter = EventListAdapter(this, eventList)
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
