package ca.warp7.android.scouting.abstraction;

/**
 * Child view interface to pass a ParentControlLister
 * @since v0.3.0
 */

public interface ChildInputControl {
    void setParentListener(ParentInputControlListener listener);
}
