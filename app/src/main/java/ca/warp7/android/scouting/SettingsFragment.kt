package ca.warp7.android.scouting

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

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
            aboutApp.summary = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
        }

        findPreference<Preference>(getString(R.string.pref_event_selection))?.setOnPreferenceClickListener {
            startActivity(Intent(context, EventSelectionActivity::class.java))
            true
        }
    }

    override fun onResume() {
        super.onResume()
        val eventSelector = findPreference<Preference>(getString(R.string.pref_event_selection))
        if (eventSelector != null) {
            eventSelector.summary = "Current Event: " + PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(getString(R.string.pref_event_name), "No Event")
        }
    }
}

