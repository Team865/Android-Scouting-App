package ca.warp7.android.scouting.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;

import ca.warp7.android.scouting.ScheduleActivity;
import ca.warp7.android.scouting.constants.PreferenceKeys;
import ca.warp7.android.scouting.data.AppAssets;
import ca.warp7.android.scouting.model.Specs;

public class SettingsClickListener implements Preference.OnPreferenceClickListener {
    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PreferenceKeys.kCopyAssetsKey:
                onCopyAssets(preference.getContext());
                return true;
            case PreferenceKeys.kScheduleKey:
                onScheduleActivityIntent(preference.getContext());
                return true;
        }
        return false;
    }

    private void onScheduleActivityIntent(Context context) {
        Intent intent;
        intent = new Intent(context, ScheduleActivity.class);
        context.startActivity(intent);
    }

    private void onCopyAssets(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Are you sure?")
                .setMessage("Any files stored at \""
                        + Specs.getSpecsRoot().getAbsolutePath()
                        + "\" will be overwritten.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppAssets.copyAssets(context);
                    }
                })
                .create()
                .show();
    }
}
