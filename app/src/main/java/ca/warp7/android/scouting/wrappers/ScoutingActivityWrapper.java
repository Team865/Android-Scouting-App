package ca.warp7.android.scouting.wrappers;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import ca.warp7.android.scouting.abstraction.AbstractActionVibrator;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.resources.ManagedPreferences;

abstract public class ScoutingActivityWrapper extends AppCompatActivity
        implements ScoutingActivityListener {

    // System Services

    private Handler mTimeHandler;
    private ManagedPreferences mPreferences;

    // Animation Objects

    protected final Animation mAlphaAnimationIn = new AlphaAnimation(0.0f, 1.0f);
    protected final Animation mAlphaAnimationOut = new AlphaAnimation(1.0f, 0.0f);

    /**
     * @return The current time in seconds
     */
    protected static int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    protected static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    protected static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimeHandler = new Handler();
        mPreferences = new ManagedPreferences(this);
    }

    @Override
    public Handler getHandler() {
        return mTimeHandler;
    }

    @Override
    public AbstractActionVibrator getManagedVibrator() {
        return mPreferences.getVibrator();
    }

    protected ManagedPreferences getManagedPreferences() {
        return mPreferences;
    }
}
