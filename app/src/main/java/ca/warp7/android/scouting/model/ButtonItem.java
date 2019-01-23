package ca.warp7.android.scouting.model;

import ca.warp7.android.scouting.abstraction.ScoutingScheduleItem;

public class ButtonItem implements ScoutingScheduleItem {
    private String mText;
    public ButtonItem(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }
}
