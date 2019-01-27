package ca.warp7.android.scouting.v5

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.SettingsActivity

class V5MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v5_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Humber College Event"

        findViewById<Button>(R.id.v5_starter).setOnClickListener {
            startActivity(Intent(this, V5ScoutingActivity::class.java))
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
                else -> false
            }
        } ?: false
    }
}
