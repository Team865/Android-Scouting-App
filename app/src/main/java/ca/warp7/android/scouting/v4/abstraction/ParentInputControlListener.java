package ca.warp7.android.scouting.v4.abstraction;

import android.view.View;

/**
 * Interface for containers to implement, which the child
 * view can call to get the supporting view to act on it
 *
 * @since v0.3.0
 */

public interface ParentInputControlListener {
    View getSupportView();
}
