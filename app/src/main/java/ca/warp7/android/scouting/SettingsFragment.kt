package ca.warp7.android.scouting

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

/**
 * @since v0.4.1
 */

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val listener = SettingsClickListener()
        addListener(R.string.pref_copy_assets_key, listener)
        addListener(R.string.pref_v4_key, listener)
        addListener(R.string.pref_licenses_key, listener)

        val aboutApp = findPreference(getString(R.string.pref_about_key))
        aboutApp.summary = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
    }

    private fun addListener(id: Int, listener: SettingsClickListener) {
        findPreference(getString(id)).onPreferenceClickListener = listener
    }
}
