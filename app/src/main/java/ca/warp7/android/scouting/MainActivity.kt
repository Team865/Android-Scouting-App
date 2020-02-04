package ca.warp7.android.scouting

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.entry.Alliance
import ca.warp7.android.scouting.entry.Board.*
import ca.warp7.android.scouting.entry.toBoard
import ca.warp7.android.scouting.event.EventInfo
import ca.warp7.android.scouting.event.MatchSchedule
import ca.warp7.android.scouting.tba.getEventMatchesSimple
import ca.warp7.android.scouting.ui.EntryInMatch
import ca.warp7.android.scouting.ui.EntryListAdapter
import ca.warp7.android.scouting.ui.createQRBitmap
import com.google.zxing.WriterException
import java.io.File
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
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
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val entryListAdapter = EntryListAdapter(this, displayedEntries)
        entriesList.adapter = entryListAdapter

        boardTextView.setOnClickListener { onSelectBoard(preferences) }

        val boardString = preferences.getString(PreferenceKeys.kBoard, "R1")
        board = boardString?.toBoard() ?: R1
        updateBoard()

        scoutTextView.setOnClickListener { onEnterScout(preferences, null) }
        entriesList.setOnItemClickListener { _, _, position, _ ->
            onEntryClicked(entryListAdapter, position)
        }
        entriesList.setOnItemLongClickListener { _, _, position, _ ->
            onEntryLongClicked(entryListAdapter,position)
        }

        val eventCheck = {
            val key = preferences.getString(PreferenceKeys.kEventKey, null)
            val event = preferences.getString(PreferenceKeys.kEventName, null)

            if (key == null || event == null || key == eventInfo.eventKey) {
                startActivity(Intent(this, EventSelectionActivity::class.java))
            }
        }

        val scoutPref = preferences.getString(PreferenceKeys.kScout, null)
        if (scoutPref != null) {
            scoutTextView.text = scoutPref
            eventCheck()
        } else {
            val scoutName = "Unknown Scout"
            scoutTextView.text = scoutName
            onEnterScout(preferences, eventCheck)
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
            R.id.menu_new_entry -> onNewEntry()
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
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
     * The match schedule stuff is in onStart because the activity must be updated
     * when the user returns from the settings screen
     */
    override fun onStart() {
        super.onStart()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val key = preferences.getString(PreferenceKeys.kEventKey, null)
        val event = preferences.getString(PreferenceKeys.kEventName, null)

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
     * Save the scouted entries before pausing the activity
     */
    override fun onStop() {
        super.onStop()
        if (scoutedEntries.isNotEmpty()) {
            val entriesFile = File(filesDir, eventInfo.eventKey + ".csv")
            entriesFile.writeText(scoutedEntries.joinToString("\n") { it.toCSV() })
        }
    }

    /**
     * Computes the match schedule in a thread, then update the UI
     */
    private fun updateMatchScheduleInThread(event: String, key: String) {
        try {
            // we get the tba matches, then sort it
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

            // reload the scouted entries from disk
            scoutedEntries.clear()
            val entriesFile = File(filesDir, "$key.csv")
            if (entriesFile.exists()) {
                val lines = entriesFile.readLines()
                val data = lines.map { EntryInMatch.fromCSV(it) }
                scoutedEntries.addAll(data)
            }

            runOnUiThread {
                supportActionBar?.title = eventInfo.eventName
                updateExpectedItems()
                updateDisplayedItems()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error_retrieving_data))
                    .setMessage(getString(R.string.check_connection))
                    .create().show()
            }
        }
    }

    /**
     * Called when an entry is clicked in the list
     */
    private fun onEntryClicked(adapter: EntryListAdapter, position: Int) {
        val entryInMatch = adapter.getItem(position) ?: return
        if (entryInMatch.isComplete && entryInMatch.data.isNotEmpty()) {
            // we show the data in a qr code dialog
            val qrImage = ImageView(this)
            qrImage.setPadding(16, 0, 16, 0)

            // create the dialog
            val dialog = AlertDialog.Builder(this)
                .setTitle(entryInMatch.match)
                .setView(qrImage)
                .setNeutralButton(getString(R.string.send_with)) { _, _ ->

                    // Send with an intent
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, entryInMatch.data)
                    intent.type = "text/plain"
                    startActivity(Intent.createChooser(intent, entryInMatch.data))
                }
                .setPositiveButton(getString(R.string.button_ok)) { dialog, _ -> dialog.dismiss() }
                .create()

            // add the listener so we can figure out the width of the QR code to make
            dialog.setOnShowListener {
                val dim = dialog.window?.decorView?.width ?: 0
                try {
                    qrImage.setImageBitmap(createQRBitmap(entryInMatch.data, dim))
                } catch (e: WriterException) {
                    qrImage.setImageDrawable(getDrawable(R.drawable.ic_launcher_background))
                    e.printStackTrace()
                }
            }

            dialog.show()
        } else {
            if (entryInMatch.teams.size > 5) {
                // actually start scouting the entry
                startScoutingActivity(entryInMatch)
            }
        }
    }

    /**
     * Delete a scouted entry
     */
    private fun onEntryLongClicked(adapter: EntryListAdapter, position: Int): Boolean {
        val entryInMatch = adapter.getItem(position) ?: return false
        if (!entryInMatch.isComplete) return false
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_this_entry))
            .setMessage(entryInMatch.data)
            .setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.dismiss()}
            .setPositiveButton(getString(R.string.button_ok)) { dialog, _ ->
                scoutedEntries.remove(entryInMatch)
                updateDisplayedItems()
                dialog.dismiss()
            }
            .create()
        dialog.show()
        return true
    }

    private fun onSelectBoard(preferences: SharedPreferences) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_board_title))
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
    private fun onEnterScout(preferences: SharedPreferences, nextStep: (() -> Unit)?) {
        // create the edit text component
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            hint = getString(R.string.scout_input_hint)
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
            .setTitle(getString(R.string.scout_input_title))
            .setView(layout)
            .setPositiveButton(getString(R.string.button_ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.cancel() }
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
            // execute the next step
            nextStep?.invoke()
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
                if (it.match.startsWith(p)) {
                    it.match.substring(p.length).toIntOrNull() ?: 0
                } else 0
            }
        }
        (entriesList.adapter as EntryListAdapter).notifyDataSetChanged()
    }

    private fun startScoutingActivity(entryInMatch: EntryInMatch) {
        startActivityForResult(Intent(this, ScoutingActivity::class.java).apply {
            putExtra(kEntryInMatchIntent, entryInMatch.toCSV())
            putExtra(kScoutIntent, scoutTextView.text.toString())
        }, MY_INTENT_REQUEST_SCOUTING)
    }

    private fun onNewEntry() {
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
            .setTitle(getString(R.string.add_new_entry))
            .setView(layout)
            .setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                val matchText = matchEdit.text.toString()
                val teamText = teamEdit.text.toString()
                startUnscheduledEntry(matchText, teamText)
            }
            .setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.dismiss() }
            .create()

        // make sure the keyboard is up
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()

        // get the ok button
        val ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        ok.isEnabled = false

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ok.isEnabled = matchEdit.text.isNotEmpty() && teamEdit.text.isNotEmpty()
            }
        }

        matchEdit.addTextChangedListener(watcher)
        teamEdit.addTextChangedListener(watcher)
    }

    private fun startUnscheduledEntry(match: String, team: String) {

        if (match.isEmpty() || team.isEmpty()) {
            return
        }

        val matchKey = "${eventInfo.eventKey}_$match"

        val teamNumber = team.toInt()
        val mutableTeams = mutableListOf(0, 0, 0, 0, 0, 0)

        if (board != RX && board != BX) {
            mutableTeams[values().indexOf(board)] = teamNumber
        }

        val entryInMatch = EntryInMatch(
            matchKey,
            mutableTeams,
            board,
            isComplete = false,
            isScheduled = false
        )
        startScoutingActivity(entryInMatch)
    }

    private fun processScoutingActivityResult(intent: Intent) {

        // get the extra data from the intent bundle
        val entryString = intent.getStringExtra(kEntryInMatchIntent)
        val entryInMatch = EntryInMatch.fromCSV(entryString)

        scoutedEntries.add(entryInMatch)
        updateDisplayedItems()
    }
}
