package ca.warp7.android.scouting

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * @since v0.4.2
 */

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(
            android.R.id.content,
            SettingsFragment()
        ).commit()
        setTheme(R.style.SettingsTheme)
        title = "Options"
    }
}
