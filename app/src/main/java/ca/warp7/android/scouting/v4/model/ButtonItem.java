package ca.warp7.android.scouting.v4.model;

import ca.warp7.android.scouting.v4.abstraction.ScoutingScheduleItem;

public class ButtonItem implements ScoutingScheduleItem {
    private String mText;

    public ButtonItem(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }
}
