package ca.warp7.android.scouting.interfaces;

import android.view.View;

/**
 * Interface for containers to implement, which the child
 * view can call to get the supporting view to act on it
 */

public interface ParentInputControlListener {
    View getSupportView();
}
