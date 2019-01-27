package ca.warp7.android.scouting.v4

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import ca.warp7.android.scouting.AppResources
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.SettingsActivity
import ca.warp7.android.scouting.v4.constants.ID
import ca.warp7.android.scouting.v4.model.Specs
import ca.warp7.android.scouting.v4.model.SpecsIndex
import ca.warp7.android.scouting.v5.V5ScoutingActivity
import java.io.File

/**
 * @since v0.1.0
 */

class V4MainActivity : AppCompatActivity() {

    private lateinit var scoutNameField: EditText
    private lateinit var matchNumberField: EditText
    private lateinit var teamNumberField: EditText
    private lateinit var mismatchWarning: TextView
    private lateinit var verifier: CheckBox
    private lateinit var matchStartButton: View
    private lateinit var preferences: SharedPreferences
    private lateinit var specsIndex: SpecsIndex

    private var passedSpecsFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = this.getSharedPreferences(ID.ROOT, Context.MODE_PRIVATE)
        ensurePermissions()
        setupUI()
        setupListeners()

        findViewById<Button>(R.id.v5_starter).setOnClickListener {
            startActivity(Intent(this, V5ScoutingActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        setupSpecs()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.specs_selector_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_select_specs -> {
                askToSelectSpecs()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FILES -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupSpecs()
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLogoClicked(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun checkChanged(b: Boolean) {
        matchStartButton.visibility = if (b) View.VISIBLE else View.INVISIBLE
        val view = this.currentFocus
        if (view != null && verifier.isChecked) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun startScouting() {
        val name = scoutNameField.text.toString().replace("_".toRegex(), "")
        val editor = preferences.edit()
        editor.putString(ID.SAVE_SCOUT_NAME, name)
        editor.apply()
        if (!Specs.hasInstance()) return
        Intent(this, V4ScoutingActivity::class.java).apply {
            putExtra(ID.MSG_MATCH_NUMBER, matchNumberField.text.toString().toInt())
            putExtra(ID.MSG_TEAM_NUMBER, teamNumberField.text.toString().toInt())
            putExtra(ID.MSG_SCOUT_NAME, name)
            putExtra(ID.MSG_ALLIANCE, "")
            putExtra(ID.MSG_SPECS_FILE, passedSpecsFile!!.absolutePath)
        }.let { startActivity(it) }
    }

    private fun ensurePermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_FILES
            )
        }
    }

    private fun setupUI() {
        setContentView(R.layout.activity_v4_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        scoutNameField = findViewById(R.id.name_and_initial)
        matchNumberField = findViewById(R.id.match_number)
        teamNumberField = findViewById(R.id.team_number)
        mismatchWarning = findViewById(R.id.mismatch_warning)
        verifier = findViewById(R.id.verify_check)
        matchStartButton = findViewById(R.id.match_start_button)
        scoutNameField.setText(preferences.getString(ID.SAVE_SCOUT_NAME, ""))
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                updateTextFieldState()
            }
        }

        scoutNameField.addTextChangedListener(watcher)
        matchNumberField.addTextChangedListener(watcher)
        teamNumberField.addTextChangedListener(watcher)
        verifier.setOnCheckedChangeListener { _, b -> checkChanged(b) }
        matchStartButton.setOnClickListener { startScouting() }

        findViewById<View>(R.id.team_logo).setOnLongClickListener {
            onLogoClicked(it)
            true
        }
    }

    private fun setupSpecs() {
        val root = AppResources.specsRoot
        val indexFile = File(root, "index.json")
        if (!indexFile.exists()) {
            AppResources.copySpecsAssets(this)
        }
        loadIndex(indexFile)
    }

    private fun askToSelectSpecs() {
        val specs = specsIndex.names.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select your board")
            .setItems(specs) { _, which -> loadSpecsFromName(specs[which]) }
            .show()
    }

    private fun loadIndex(indexFile: File) {
        specsIndex = SpecsIndex(indexFile)
        val names = specsIndex.names
        if (!names.isEmpty()) {
            val savedName = preferences.getString(ID.SAVE_SPECS, "")
            loadSpecsFromName(if (names.contains(savedName)) savedName else names[0])
        } else {
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.title = "Index File Not Found"
            }
        }
    }

    private fun loadSpecsFromName(name: String?) {
        if (specsIndex.names.contains(name)) {
            passedSpecsFile = File(AppResources.specsRoot, specsIndex.getFileByName(name))
            val specs = Specs.setInstance(passedSpecsFile)
            val ab = supportActionBar
            if (ab != null) {
                ab.title = "Board: " + specs!!.boardName
            }
            updateTextFieldState()
            val editor = preferences.edit()
            editor.putString(ID.SAVE_SPECS, name)
            editor.apply()
        }
    }

    private fun updateTextFieldState() {
        val name = scoutNameField.text.toString()
        val match = matchNumberField.text.toString()
        val team = teamNumberField.text.toString()
        if (name.isNotEmpty() && match.isNotEmpty() && team.isNotEmpty()) {
            verifier.isEnabled = true
            if (Specs.getInstance().hasSchedule()) {
                if (matchDoesExist(match, team)) {
                    mismatchWarning.visibility = View.INVISIBLE
                    verifier.setText(R.string.verify_match_info)
                    verifier.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorAlmostBlack
                        )
                    )
                } else {
                    mismatchWarning.setText(R.string.schedule_mismatch)
                    mismatchWarning.visibility = View.VISIBLE
                    verifier.setText(R.string.verify_match_proceed)
                    verifier.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
                    verifier.isChecked = false
                }
            } else {
                mismatchWarning.setText(R.string.schedule_does_not_exist)
                mismatchWarning.visibility = View.VISIBLE
            }
        } else {
            mismatchWarning.visibility = View.INVISIBLE
            verifier.setText(R.string.verify_match_info)
            verifier.isEnabled = false
            verifier.setTextColor(ContextCompat.getColor(this, R.color.colorAlmostBlack))
            verifier.isChecked = false
        }
    }

    private fun matchDoesExist(m: String, t: String): Boolean {
        return Specs.getInstance().matchIsInSchedule(Integer.parseInt(m) - 1, Integer.parseInt(t))
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_FILES = 0
    }
}
