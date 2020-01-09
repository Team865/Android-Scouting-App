package ca.warp7.android.scouting

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

/**
 * @since v0.4.1
 */

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference(getString(R.string.pref_licenses_key)).setOnPreferenceClickListener {
            val intent = Intent(context, LicensesActivity::class.java)
            it.context.startActivity(intent)
            true
        }

        val aboutApp = findPreference(getString(R.string.pref_about_key))
        aboutApp.summary = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
    }

    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        var number = sharedPreferences.getString(key, "")
        println(number)
    }
}
