package ca.warp7.android.scouting.v5

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.SettingsActivity
import ca.warp7.android.scouting.v5.entry.*
import ca.warp7.android.scouting.v5.entry.Board.*
import ca.warp7.android.scouting.v5.ui.EntriesListAdapter


class V5MainActivity : AppCompatActivity() {

    private lateinit var boardTextView: TextView
    private lateinit var scoutTextView: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var entriesList: ListView

    private var board = R1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v5_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Humber College"
        boardTextView = findViewById(R.id.board)
        scoutTextView = findViewById(R.id.scout_name)
        entriesList = findViewById(R.id.entries_list)
        ensurePermissions()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        boardTextView.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select board")
                .setSingleChoiceItems(R.array.board_choices_v5, values().indexOf(board)) { dialog, which ->
                    values()[which].also {
                        board = it
                        updateBoardText()
                        preferences.edit().apply {
                            putString(MainSettingsKey.kBoard, it.name)
                            apply()
                        }
                    }
                    dialog.dismiss()
                }.create().show()
        }
        val boardString = preferences.getString(MainSettingsKey.kBoard, "R1")
        board = boardString?.toBoard() ?: R1
        updateBoardText()
        scoutTextView.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val layout = LinearLayout(this)
            layout.addView(input)
            layout.setPadding(10, 0, 10, 0)
            val dialog = AlertDialog.Builder(this)
                .setTitle("Enter Name")
                .setMessage("Format: First Name + Space + One Letter Last Initial, Both Capitalized")
                .setView(layout)
                .setPositiveButton("OK") { _, _ -> }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .create()
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
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
            input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ok.isEnabled = validateName(input.text.toString())
                }
            })
        }
        val adapter = EntriesListAdapter(
            this, listOf(
                EntryItem("2019onto3_qm1", listOf(1, 2, 3, 4, 5, 6), B1),
                EntryItem("2019onto3_qm2", listOf(), R1, state = EntryItemState.Completed),
                EntryItem("2019onto3_qm3", listOf(), R1, state = EntryItemState.Added),
                EntryItem("2019onto3_qm123", listOf(), BX)
            )
        )
        entriesList.adapter = adapter
        entriesList.setOnItemClickListener { _, _, position, _ ->
            adapter.getItem(position)?.apply {
                if (teams.size > 5) {
                    startScouting(
                        match, when (board) {
                            R1 -> teams[0].toString()
                            R2 -> teams[1].toString()
                            R3 -> teams[2].toString()
                            B1 -> teams[3].toString()
                            B2 -> teams[4].toString()
                            B3 -> teams[5].toString()
                            RX, BX -> "ALL"
                        }, scoutTextView.text.toString(), board
                    )
                }
            }
        }
        entriesList.setOnItemLongClickListener { _, _, position, _ ->
            val match = adapter.getItem(position)?.match ?: ""
            AlertDialog.Builder(this)
                .setTitle("Delete Entry $match?")
                .setMessage("Deleted entry cannot be recovered")
                .setPositiveButton("Delete") { dialog, which -> }
                .setNegativeButton("Keep") { dialog, which -> }
                .create()
                .show()
            true
        }

        scoutTextView.text = preferences.getString(MainSettingsKey.kScout, "Unknown Scout")
    }

    private fun updateBoardText() {
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
        menuInflater.inflate(R.menu.v5_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.itemId?.let {
            when (it) {
                R.id.menu_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.menu_new_entry -> {
                    true
                }
                else -> false
            }
        } ?: false
    }

    private fun ensurePermissions() {
        val permission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_FILES)
        }
    }

    fun startScouting(match: String, team: String, scout: String, board: Board) {
        startActivity(Intent(this, V5ScoutingActivity::class.java).apply {
            putExtra(ScoutingIntentKey.kMatch, match)
            putExtra(ScoutingIntentKey.kBoard, board)
            putExtra(ScoutingIntentKey.kTeam, team)
            putExtra(ScoutingIntentKey.kScout, scout)
        })
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_FILES = 0
    }
}
