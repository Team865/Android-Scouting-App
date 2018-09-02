package ca.warp7.android.scouting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import ca.warp7.android.scouting.constants.PreferenceKeys;
import ca.warp7.android.scouting.interfaces.AbstractActionVibrator;

public class ManagedPreferences {

    public static class ActionVibrator implements AbstractActionVibrator {
        private Vibrator mActualVibrator;
        private boolean mVibrationOn;

        ActionVibrator(Context context, boolean vibrationOn) {
            mActualVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrationOn = vibrationOn;
        }

        @Override
        public void vibrateStart() {
            if (mVibrationOn) {
                mActualVibrator.vibrate(kStartVibration, -1);
            }
        }

        @Override
        public void vibrateAction() {
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
                    mSharedPreferences.getBoolean(PreferenceKeys.kVibratorPreferenceKey, true));
        }
        return mActionVibrator;
    }

    boolean shouldShowPause() {
        return mSharedPreferences.getBoolean(PreferenceKeys.kShowPausePreferenceKey, false);
    }
}
