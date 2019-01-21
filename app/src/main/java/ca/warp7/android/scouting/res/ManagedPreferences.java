package ca.warp7.android.scouting.res;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.AbstractActionVibrator;

/**
 * @since v0.4.1
 */

public class ManagedPreferences {

    private final SharedPreferences mSharedPreferences;
    private final Context mContext;
    private AbstractActionVibrator mActionVibrator;

    public ManagedPreferences(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public AbstractActionVibrator getVibrator() {
        if (mActionVibrator == null) {
            mActionVibrator = new ActionVibrator(mContext, mSharedPreferences
                    .getBoolean(getString(R.string.pref_use_vibration_key), true));
        }
        return mActionVibrator;
    }

    public boolean shouldShowPause() {
        return mSharedPreferences.getBoolean(getString(R.string.pref_show_pause_key), false);
    }

    private String getString(int id) {
        return mContext.getString(id);
    }
}
