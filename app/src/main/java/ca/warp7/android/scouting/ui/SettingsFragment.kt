package ca.warp7.android.scouting.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ca.warp7.android.scouting.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>(getString(R.string.pref_licenses_key))?.setOnPreferenceClickListener {
            val intent = Intent(context, LicensesActivity::class.java)
            it.context.startActivity(intent)
            true
        }

        val aboutApp = findPreference<Preference>(getString(R.string.pref_about_key))
        if (aboutApp != null) {
            aboutApp.summary = getString(R.string.version_name) +
                    BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
        }

        val eventSelect = findPreference<Preference>(getString(R.string.pref_event_selection))
        eventSelect?.setOnPreferenceClickListener {
            startActivity(Intent(context, EventSelectionActivity::class.java))
            true
        }

        val darkTheme = findPreference<ListPreference>(getString(R.string.pref_dark_theme_key))
        darkTheme?.setOnPreferenceChangeListener { _, newValue ->
            val newState = when (newValue) {
                "Light" -> AppCompatDelegate.MODE_NIGHT_NO
                "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            AppCompatDelegate.setDefaultNightMode(newState)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        val eventSelector = findPreference<Preference>(getString(R.string.pref_event_selection))
        val preferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val event = preferences.getString(PreferenceKeys.kEventName, "No Event")
        val year = preferences.getString(PreferenceKeys.kYear, "")
        if (eventSelector != null) {
            eventSelector.summary = getString(R.string.current_event, year, event)
        }
    }
}

