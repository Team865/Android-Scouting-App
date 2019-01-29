package ca.warp7.android.scouting.v5

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.SettingsActivity
import ca.warp7.android.scouting.v5.entry.Board

class V5MainActivity : AppCompatActivity() {

    private lateinit var boardTextView: TextView
    private lateinit var scoutTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v5_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Humber College Event"
        boardTextView = findViewById(R.id.board)
        scoutTextView = findViewById(R.id.scout_name)

        boardTextView.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Select board")
                .setItems(R.array.board_choices_v5) { _, which -> boardTextView.text = Board.values()[which].name }
                .show()
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
                    startActivity(Intent(this, V5ScoutingActivity::class.java))
                    true
                }
                else -> false
            }
        } ?: false
    }
}
