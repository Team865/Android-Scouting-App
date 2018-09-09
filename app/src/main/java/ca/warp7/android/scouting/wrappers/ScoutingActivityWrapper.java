package ca.warp7.android.scouting.wrappers;

import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import ca.warp7.android.scouting.abstraction.EntryTimekeeper;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;

abstract public class ScoutingActivityWrapper extends AppCompatActivity
        implements ScoutingActivityListener, EntryTimekeeper {


    // Animation Objects
    protected final Animation animate_in = new AlphaAnimation(0.0f, 1.0f);
    protected final Animation animate_out = new AlphaAnimation(1.0f, 0.0f);

    /**
     * @return The current time in seconds
     */
    protected int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
