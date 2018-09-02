package ca.warp7.android.scouting.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.warp7.android.scouting.components.ActionVibrator;
import ca.warp7.android.scouting.constants.PreferenceKeys;

public class ManagedPreferences {

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private ActionVibrator mActionVibrator;

    public ManagedPreferences(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public ActionVibrator getVibrator() {
        if (mActionVibrator == null) {
            mActionVibrator = new ActionVibrator(mContext,
                    mSharedPreferences.getBoolean(PreferenceKeys.kVibratorPreferenceKey, true));
        }
        return mActionVibrator;
    }

    public boolean shouldShowPause() {
        return mSharedPreferences.getBoolean(PreferenceKeys.kShowPausePreferenceKey, false);
    }
}
