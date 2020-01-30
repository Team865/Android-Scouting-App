package ca.warp7.android.scouting

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.entry.*
import ca.warp7.android.scouting.entry.Board.*
import ca.warp7.android.scouting.event.EventInfo
import ca.warp7.android.scouting.event.MatchSchedule
import ca.warp7.android.scouting.tba.getEventMatchesSimple
import ca.warp7.android.scouting.ui.EntryInMatch
import ca.warp7.android.scouting.ui.EntryListAdapter
import ca.warp7.android.scouting.ui.createQRBitmap
import com.google.zxing.WriterException
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
        // the id to use when requesting permissions
        private const val MY_PERMISSIONS_REQUEST_FILES = 0
        // the id to use when getting data back from ScoutingActivity
        private const val MY_INTENT_REQUEST_SCOUTING = 1
    }

    // the board and the scout
    private val boardTextView: TextView get() = findViewById(R.id.board)
    private val scoutTextView: TextView get() = findViewById(R.id.scout_name)

    // the list of matches
    private val entriesList: ListView get() = findViewById(R.id.entries_list)

    // the current board
    private var board = R1
    private var eventInfo = EventInfo(
        "No Event", "No Key",
        MatchSchedule(listOf())
    )

    // the list of items that are actually displayed on screen
    private val displayedEntries = ArrayList<EntryInMatch>()

    // the entries that are in the schedule
    private val expectedEntries = ArrayList<EntryInMatch>()

    // the entries that are not in the schedule
    private val scoutedEntries = ArrayList<EntryInMatch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        initActivityWithPermissions()
    }

    private fun initActivityWithPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_FILES
            )
        } else {
            initActivity()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FILES -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted - continue to setup
                    initActivity()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Handle the top-right menu clicks
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val it = item?.itemId ?: return false
        when (it) {
            R.id.menu_new_entry -> {
                onNewEntry()
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

    /**
     * Get the data from the scouting activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_INTENT_REQUEST_SCOUTING) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    processScoutingActivityResult(data)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * The match schedule stuff is in onResume because the activity must be updated
     * when the user returns from the settings screen
     */
    override fun onResume() {
        super.onResume()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val key = preferences.getString(PreferenceKeys.kEventKey, "No Key")
        val event = preferences.getString(PreferenceKeys.kEventName, "No Event")

        // only regenerate match schedule if the key is different
        if (key != null && event != null && key != eventInfo.eventKey) {
            val adapter = entriesList.adapter as? EntryListAdapter
            if (adapter != null) {
                adapter.highlightTeam = preferences
                    .getString(PreferenceKeys.kTeam, "0")!!.toInt()
            }
            // run match schedule getter on a new thread
            thread { updateMatchScheduleInThread(event, key) }
        }
    }

    /**
     * Set up the activity when permission is granted
     */
    private fun initActivity() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val entryListAdapter = EntryListAdapter(this, displayedEntries)
        entriesList.adapter = entryListAdapter

        boardTextView.setOnClickListener { onSelectBoard(preferences) }

        val boardString = preferences.getString(PreferenceKeys.kBoard, "R1")
        board = boardString?.toBoard() ?: R1
        updateBoard()

        scoutTextView.setOnClickListener { onEnterScout(preferences) }
        entriesList.setOnItemClickListener { _, _, position, _ ->
            onEntryClicked(entryListAdapter, position)
        }
        scoutTextView.text = preferences.getString(PreferenceKeys.kScout, "Unknown Scout")
    }

    /**
     * Computes the match schedule in a thread, then update the UI
     */
    private fun updateMatchScheduleInThread(event: String, key: String) {
        try {
            // we need to get
            val matches = createCachedTBAInstance(this).getEventMatchesSimple(key)
                .filter { it.comp_level == "qm" }
                .sortedBy { it.match_number }

            eventInfo = EventInfo(
                event,
                key,
                MatchSchedule(matches.flatMap { match ->
                    match.alliances!!.red!!.team_keys!!.map { it.substring(3).toInt() } +
                            match.alliances.blue!!.team_keys!!.map { it.substring(3).toInt() }
                })
            )
            runOnUiThread {
                supportActionBar?.title = eventInfo.eventName

                updateExpectedItems()
                updateDisplayedItems()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Error Retrieving Event")
                    .setMessage(e.toString())
                    .create().show()
            }
        }
    }

    /**
     * Called when an entry is clicked in the list
     */
    private fun onEntryClicked(adapter: EntryListAdapter, position: Int) {
        val item = adapter.getItem(position) ?: return
        if (item.isComplete && item.data.isNotEmpty()) {
            // we show the data in a qr code dialog
            val qrImage = ImageView(this)
            qrImage.setPadding(16, 0, 16, 0)

            // create the dialog
            val dialog = AlertDialog.Builder(this)
                .setTitle(item.match)
                .setView(qrImage)
                .setNeutralButton("Send With...") { _, _ ->

                    // Send with an intent
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, item.data)
                    intent.type = "text/plain"
                    startActivity(Intent.createChooser(intent, item.data))
                }
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()

            // add the listener so we can figure out the width of the QR code to make
            dialog.setOnShowListener {
                val dim = dialog.window?.decorView?.width ?: 0
                try {
                    qrImage.setImageBitmap(createQRBitmap(item.data, dim))
                } catch (e: WriterException) {
                    qrImage.setImageDrawable(getDrawable(R.drawable.ic_launcher_background))
                    e.printStackTrace()
                }
            }

            dialog.show()
        } else {
            if (item.teams.size > 5) {
                // actually start scouting the entry
                startScouting(
                    item.match, when (board) {
                        R1 -> item.teams[0].toString()
                        R2 -> item.teams[1].toString()
                        R3 -> item.teams[2].toString()
                        B1 -> item.teams[3].toString()
                        B2 -> item.teams[4].toString()
                        B3 -> item.teams[5].toString()
                        RX, BX -> "ALL"
                    }, scoutTextView.text.toString(), board)
            }
        }
    }

    private fun onSelectBoard(preferences: SharedPreferences) {
        AlertDialog.Builder(this)
            .setTitle("Select board")
            .setIcon(R.drawable.ic_book_ablack_small)
            .setSingleChoiceItems(
                values().map { it.displayName }.toTypedArray(),
                values().indexOf(board)
            ) { dialog, which ->
                values()[which].also {
                    board = it
                    updateBoard()
                    updateExpectedItems()
                    updateDisplayedItems()
                    preferences.edit().apply {
                        putString(PreferenceKeys.kBoard, it.name)
                        apply()
                    }
                }
                dialog.dismiss()
            }.create().show()
    }

    /**
     * Create a dialog to let the scout enter their name
     */
    private fun onEnterScout(preferences: SharedPreferences) {
        // create the edit text component
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            hint = "First L"
            // add an icon
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_account_box_ablack_small, 0, 0, 0)
            compoundDrawablePadding = 16
            setPadding(16, paddingTop, 16, paddingBottom)
        }

        // create a layout with this input
        val layout = LinearLayout(this)
        layout.addView(input)
        layout.setPadding(16, 8, 16, 0)

        // create the dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Name")
            .setView(layout)
            .setPositiveButton("OK") { _, _ -> }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()

        // get the ok button
        val ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        ok.setOnClickListener {
            val result = input.text.toString().trim()
            scoutTextView.text = result
            preferences.edit().apply {
                putString(PreferenceKeys.kScout, result)
                apply()
            }
            dialog.dismiss()
        }
        ok.isEnabled = validateName(input.text.toString())

        // add text validation to the
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ok.isEnabled = validateName(input.text.toString())
            }
        })
    }

    private fun updateBoard() {
        boardTextView.text = board.name
        boardTextView.setTextColor(
            ContextCompat.getColor(
                this, when (board.alliance) {
                    Alliance.Red -> R.color.colorRed
                    Alliance.Blue -> R.color.colorBlue
                }
            )
        )
    }

    private fun updateExpectedItems() {
        expectedEntries.clear()
        eventInfo.matchSchedule.forEach { matchNumber, teams ->
            val item = EntryInMatch(
                "${eventInfo.eventKey}_$matchNumber",
                teams, board, isComplete = false, isScheduled = true
            )
            expectedEntries.add(item)
        }
    }

    private fun updateDisplayedItems() {
        displayedEntries.clear()
        displayedEntries.addAll(expectedEntries)
        if (scoutedEntries.isNotEmpty()) {
            displayedEntries.addAll(scoutedEntries)
            val p = eventInfo.eventKey + "_"

            // sort matches in the correct order
            displayedEntries.sortBy {
                it.match.run {
                    if (startsWith(p)) {
                        substring(p.length).toIntOrNull() ?: 0
                    } else 0
                }
            }
        }
        (entriesList.adapter as EntryListAdapter).notifyDataSetChanged()
    }

    private fun startScouting(match: String, team: String, scout: String, board: Board) {
        startActivityForResult(Intent(this, ScoutingActivity::class.java).apply {
            putExtra(ScoutingIntentKey.kMatch, match)
            putExtra(ScoutingIntentKey.kBoard, board)
            putExtra(ScoutingIntentKey.kTeam, team)
            putExtra(ScoutingIntentKey.kScout, scout)
        }, MY_INTENT_REQUEST_SCOUTING)
    }

    private fun onNewEntry() {
        if (board == RX || board == BX) return
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 8, 16, 0)

        // create the EditText for match
        val matchEdit = EditText(this).apply {
            hint = getString(R.string.hint_match)
            inputType = InputType.TYPE_CLASS_NUMBER
            setPadding(16, paddingTop, 16, paddingBottom)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_layers_ablack_small, 0, 0, 0)
            compoundDrawablePadding = 16
        }

        // create the EditText for team
        val teamEdit = EditText(this).apply {
            hint = getString(R.string.hint_team)
            inputType = InputType.TYPE_CLASS_NUMBER
            setPadding(16, paddingTop, 16, paddingBottom)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_people_ablack_small, 0, 0, 0)
            compoundDrawablePadding = 16
        }

        layout.addView(matchEdit)
        layout.addView(teamEdit)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Entry")
            .setView(layout)
            .setPositiveButton("Ok") { _, _ ->
                val matchKey = "${eventInfo.eventKey}_${matchEdit.text}"
                // start scouting when ok
                startScouting(matchKey, teamEdit.text.toString(), scoutTextView.text.toString(), board)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        // make sure the keyboard is up
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun processScoutingActivityResult(intent: Intent) {

        // get the extra data from the intent bundle
        val result = intent.getStringExtra(ScoutingIntentKey.kResult)
        val match = intent.getStringExtra(ScoutingIntentKey.kMatch) ?: "- - -"
        val team = intent.getStringExtra(ScoutingIntentKey.kTeam).toIntOrNull() ?: 0
        val board = intent.getSerializableExtra(ScoutingIntentKey.kBoard) as Board

        var teams: List<Int> = listOf()
        var isScheduled = true
        var foundData = false

        // we need to find the right entry in the expected items
        // so we know what all the teams are
        for (item in expectedEntries) {
            if (item.match == match && item.board == board) {
                teams = item.teams
                foundData = true
                break
            }
        }

        // add some more conditions to make sure everything's right
        if (teams.size > 5) {
            val expectedTeam = when (board) {
                R1 -> teams[0]
                R2 -> teams[1]
                R3 -> teams[2]
                B1 -> teams[3]
                B2 -> teams[4]
                B3 -> teams[5]
                RX, BX -> 0
            }
            if (team != expectedTeam) {
                foundData = false
            }
        } else foundData = false

        // change the entry to Added state if data is really not found
        if (!foundData) {
            val mutableTeams = mutableListOf(0, 0, 0, 0, 0, 0)
            mutableTeams[values().indexOf(board)] = team
            teams = mutableTeams
            isScheduled = false
        }

        // add to the list of scouted items
        scoutedEntries.add(
            EntryInMatch(
                match = match,
                teams = teams,
                board = board,
                isComplete = true,
                isScheduled = isScheduled,
                data = result
            )
        )
        updateDisplayedItems()
    }
}
