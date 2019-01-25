package ca.warp7.android.scouting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import ca.warp7.android.scouting.components.ScoutingScheduleAdapter
import ca.warp7.android.scouting.constants.ID
import ca.warp7.android.scouting.constants.RobotPosition
import ca.warp7.android.scouting.model.MatchWithAllianceItem
import ca.warp7.android.scouting.model.ScoutingSchedule
import ca.warp7.android.scouting.res.AppResources
import ca.warp7.android.scouting.res.EventInfo
import java.io.IOException
import java.util.*

/**
 * @since v0.4.2
 */

class ScheduleActivity : AppCompatActivity() {

    private var mScoutingSchedule: ScoutingSchedule? = null
    private var mScheduleAdapter: ScoutingScheduleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        title = "Match Schedule"

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_FILES
            )
        }

        val events = AppResources.events

        val names = ArrayList<String>()
        for (event in events) {
            names.add(event.eventName)
        }

        AlertDialog.Builder(this).setTitle("Select Event")
            .setItems(names.toTypedArray()) { _, which -> createScreen(events[which]) }
            .create().show()
    }

    private fun createScreen(selectedEvent: EventInfo) {
        title = selectedEvent.eventName

        val spinner = findViewById<Spinner>(R.id.board_spinner)
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.board_choices, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        val scheduleListView = findViewById<ListView>(R.id.entry_list)
        mScoutingSchedule = ScoutingSchedule()

        try {
            mScoutingSchedule!!.loadFullScheduleFromCSV(selectedEvent.matchTableRoot)
        } catch (exception: IOException) {
            onErrorDialog(exception)
        }

        mScoutingSchedule!!.scheduleForDisplayOnly()

        mScheduleAdapter = ScoutingScheduleAdapter(
            this,
            mScoutingSchedule!!.currentlyScheduled
        )

        scheduleListView.adapter = mScheduleAdapter

        scheduleListView.setOnItemClickListener { _, _, position, _ ->
            val item = mScoutingSchedule!!.currentlyScheduled[position]
            if (item is MatchWithAllianceItem) {
                if (item.shouldFocus()) {
                    val team = item.getTeamAtPosition(item.focusPosition)
                    val match = item.matchNumber
                    val intent = Intent(this, ScoutingActivity::class.java)

                    intent.putExtra(ID.MSG_MATCH_NUMBER, match)
                    intent.putExtra(ID.MSG_TEAM_NUMBER, team)
                    intent.putExtra(ID.MSG_ALLIANCE, "~")
                    intent.putExtra(ID.MSG_SCOUT_NAME, "hi")
                    intent.putExtra(ID.MSG_SPECS_FILE, "")

                    startActivity(intent)
                }
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> mScoutingSchedule!!.scheduleForDisplayOnly()
                    1 -> mScoutingSchedule!!.scheduleAllAtRobotPosition(RobotPosition.RED1)
                    2 -> mScoutingSchedule!!.scheduleAllAtRobotPosition(RobotPosition.RED2)
                    3 -> mScoutingSchedule!!.scheduleAllAtRobotPosition(RobotPosition.RED3)
                    4 -> mScoutingSchedule!!.scheduleAllAtRobotPosition(RobotPosition.BLUE1)
                    5 -> mScoutingSchedule!!.scheduleAllAtRobotPosition(RobotPosition.BLUE2)
                    6 -> mScoutingSchedule!!.scheduleAllAtRobotPosition(RobotPosition.BLUE3)
                }
                mScheduleAdapter!!.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun onErrorDialog(exception: Exception) {
        exception.printStackTrace()
        AlertDialog.Builder(this)
            .setTitle("An error occurred")
            .setMessage(exception.toString())
            .setPositiveButton("OK") { _, _ -> onBackPressed() }
            .create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FILES -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permission", "granted")
                }
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_FILES = 0
    }
}
