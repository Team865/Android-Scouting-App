package ca.warp7.android.scouting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ca.warp7.android.scouting.ui.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.apply {
            title = getString(R.string.settings_title)
            setDisplayHomeAsUpEnabled(true)
        }
        supportFragmentManager.beginTransaction().add(
                R.id.settings_frame,
                SettingsFragment()
        ).commit()
        setTheme(R.style.AppTheme)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
