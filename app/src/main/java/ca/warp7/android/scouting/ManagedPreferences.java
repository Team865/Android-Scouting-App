package ca.warp7.android.scouting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

class ManagedPreferences {

    public static class Fragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    public static class Activity extends AppCompatActivity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new Fragment()).commit();
            setTheme(R.style.SettingsTheme);
            setTitle("Settings");
        }
    }

    public static class ActionVibrator {
        private Vibrator mActualVibrator;
        private boolean mVibrationOn;

        ActionVibrator(Context context, boolean vibrationOn) {
            mActualVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrationOn = vibrationOn;
        }

        void vibrateStart() {
            if (mVibrationOn) {
                mActualVibrator.vibrate(kStartVibration, -1);
            }
        }

        void vibrateAction() {
            if (mVibrationOn) {
                mActualVibrator.vibrate(kActionEffectVibration);
            }
        }

        static final long[] kStartVibration = new long[]{0, 20, 30, 20};
        static final int kActionEffectVibration = 30;
    }

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private ActionVibrator mActionVibrator;

    ManagedPreferences(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    ActionVibrator getVibrator() {
        if (mActionVibrator == null) {
            mActionVibrator = new ActionVibrator(mContext,
                    mSharedPreferences.getBoolean(kVibratorPreferenceName, true));
        }
        return mActionVibrator;
    }

    boolean shouldShowPause() {
        return mSharedPreferences.getBoolean(kShowPausePreferenceName, false);
    }

    private static final String kShowPausePreferenceName = "pref_show_pause";
    private static final String kVibratorPreferenceName = "pref_use_vibration";
}
