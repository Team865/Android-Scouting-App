package ca.warp7.android.scouting;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

public class ManagedPreferences {

    public static class Activity extends PreferenceActivity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
