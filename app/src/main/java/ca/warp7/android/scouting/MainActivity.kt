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
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.boardfile.exampleEventInfo
import ca.warp7.android.scouting.entry.*
import ca.warp7.android.scouting.entry.Board.*
import ca.warp7.android.scouting.ui.EntryListAdapter
import ca.warp7.android.scouting.ui.createQRBitmap
import com.google.zxing.WriterException


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
    private val eventInfo = exampleEventInfo

    private val displayedItems = mutableListOf<EntryItem>()
    private val scoutedItems = mutableListOf<EntryItem>()
    private val expectedItems = mutableListOf<EntryItem>()

    private var showScoutedEntries = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = eventInfo.eventName
        val entryListAdapter = EntryListAdapter(this, displayedItems)
        entriesList.adapter = entryListAdapter
        ensurePermissions()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        boardTextView.setOnClickListener { onSelectBoard(preferences) }

        val boardString = preferences.getString(MainSettingsKey.kBoard, "R1")
        board = boardString?.toBoard() ?: R1
        updateBoard()
        updateExpectedItems()
        updateDisplayedItems()
        scoutTextView.setOnClickListener { onEnterScout(preferences) }
        entriesList.setOnItemClickListener { _, _, position, _ ->
            onEntryClicked(entryListAdapter, position)
        }
        scoutTextView.text = preferences.getString(MainSettingsKey.kScout, "Unknown Scout")
    }

    private fun onEntryClicked(adapter: EntryListAdapter, position: Int) {
        val item = adapter.getItem(position) ?: return
        if (item.state != EntryItemState.Waiting && item.data.isNotEmpty()) {
            val qrImage = ImageView(this)
            qrImage.setPadding(16, 0, 16, 0)
            val dialog = AlertDialog.Builder(this)
                .setTitle(item.match)
                .setView(qrImage)
                .setNeutralButton("Send With...") { _, _ ->
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, item.data)
                    intent.type = "text/plain"
                    startActivity(Intent.createChooser(intent, item.data))
                }
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()
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
        } else if (item.teams.size > 5) {
            startScouting(
                item.match, when (board) {
                    R1 -> item.teams[0].toString()
                    R2 -> item.teams[1].toString()
                    R3 -> item.teams[2].toString()
                    B1 -> item.teams[3].toString()
                    B2 -> item.teams[4].toString()
                    B3 -> item.teams[5].toString()
                    RX, BX -> "ALL"
                }, scoutTextView.text.toString(), board
            )
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
                        putString(MainSettingsKey.kBoard, it.name)
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
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
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
                putString(MainSettingsKey.kScout, result)
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
        expectedItems.clear()
        eventInfo.matchSchedule.forEach { matchNumber, teams ->
            val item = EntryItem(
                "${eventInfo.eventKey}_$matchNumber",
                teams, board, EntryItemState.Waiting
            )
            expectedItems.add(item)
        }
    }

    private fun updateDisplayedItems() {
        displayedItems.clear()
        displayedItems.addAll(expectedItems)
        if (showScoutedEntries && scoutedItems.isNotEmpty()) {
            displayedItems.addAll(scoutedItems)
            val p = eventInfo.eventKey + "_"

            // sort matches in the correct order
            displayedItems.sortBy {
                it.match.run {
                    if (startsWith(p)) substring(p.length).toIntOrNull() ?: 0 else 0
                }
            }
        }
        (entriesList.adapter as EntryListAdapter).notifyDataSetChanged()
    }

    private fun validateName(str: String): Boolean {
        val name = str.trim()
        if (name.isEmpty()) return false
        val split = name.split(" ")
        return split.size == 2 && split[0][0].isUpperCase() && split[1].length == 1 && split[1][0].isUpperCase()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FILES -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //@SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem?) = item?.itemId?.let {
        when (it) {
            R.id.menu_new_entry -> {
                if (board == RX || board == BX) return@let true
                val layout = LinearLayout(this)
                layout.orientation = LinearLayout.VERTICAL
                layout.setPadding(16, 8, 16, 0)
                val matchEdit = EditText(this).apply {
                    hint = getString(R.string.hint_match)
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_layers_ablack_small, 0, 0, 0)
                    compoundDrawablePadding = 16
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                }
                val teamEdit = EditText(this).apply {
                    hint = getString(R.string.hint_team)
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_people_ablack_small, 0, 0, 0)
                    compoundDrawablePadding = 16
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                }
                layout.addView(matchEdit)
                layout.addView(teamEdit)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Add New Entry")
                    .setView(layout)
                    .setPositiveButton("Ok") { _, _ ->
                        val matchKey = "${eventInfo.eventKey}_${matchEdit.text}"
                        startScouting(matchKey, teamEdit.text.toString(), scoutTextView.text.toString(), board)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .create()
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dialog.show()
                true
            }
            R.id.menu_toggle_scouted -> {
                showScoutedEntries = !showScoutedEntries
                if (showScoutedEntries) item.setIcon(R.drawable.ic_visibility_off_ablack)
                else item.setIcon(R.drawable.ic_visibility_ablack)
                if (scoutedItems.isNotEmpty()) updateDisplayedItems()
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> false
        }
    } ?: false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_INTENT_REQUEST_SCOUTING) {
            if (resultCode == Activity.RESULT_OK) {
                data?.also {
                    val result = it.getStringExtra(ScoutingIntentKey.kResult)
                    val match = it.getStringExtra(ScoutingIntentKey.kMatch) ?: "- - -"
                    val team = it.getStringExtra(ScoutingIntentKey.kTeam).toIntOrNull() ?: 0
                    val board = it.getSerializableExtra(ScoutingIntentKey.kBoard) as Board
                    var teams: List<Int> = listOf()
                    var state = EntryItemState.Completed
                    var foundData = false
                    for (item in expectedItems) {
                        if (item.match == match && item.board == board) {
                            teams = item.teams
                            foundData = true
                            break
                        }
                    }
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
                    if (!foundData) {
                        val mutableTeams = mutableListOf(0, 0, 0, 0, 0, 0)
                        mutableTeams[values().indexOf(board)] = team
                        teams = mutableTeams
                        state = EntryItemState.Added
                    }
                    scoutedItems.add(
                        EntryItem(
                            match,
                            teams,
                            board,
                            state,
                            result
                        )
                    )
                    updateDisplayedItems()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun ensurePermissions() {
        val permission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_FILES)
        }
    }

    private fun startScouting(match: String, team: String, scout: String, board: Board) {
        startActivityForResult(Intent(this, ScoutingActivity::class.java).apply {
            putExtra(ScoutingIntentKey.kMatch, match)
            putExtra(ScoutingIntentKey.kBoard, board)
            putExtra(ScoutingIntentKey.kTeam, team)
            putExtra(ScoutingIntentKey.kScout, scout)
        }, MY_INTENT_REQUEST_SCOUTING)
    }

}
