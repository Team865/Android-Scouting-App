package ca.warp7.android.scouting.wrappers;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.resources.ManagedPreferences;

abstract public class ScoutingActivityWrapper extends AppCompatActivity
        implements ScoutingActivityListener {

    // System Services

    protected Handler mTimeHandler;
    protected ManagedPreferences mPreferences;

    // Animation Objects

    protected final Animation animate_in = new AlphaAnimation(0.0f, 1.0f);
    protected final Animation animate_out = new AlphaAnimation(1.0f, 0.0f);

    /**
     * @return The current time in seconds
     */
    protected int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimeHandler = new Handler();
        mPreferences = new ManagedPreferences(this);
    }
}
