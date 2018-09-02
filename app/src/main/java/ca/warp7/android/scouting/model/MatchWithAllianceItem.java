package ca.warp7.android.scouting.model;


import ca.warp7.android.scouting.constants.RobotPosition;
import ca.warp7.android.scouting.interfaces.ScoutingScheduleItem;

/**
 * @since v0.4.2
 */

public class MatchWithAllianceItem implements ScoutingScheduleItem {
    private int[] mTeams;
    private int mMatchNumber;
    private boolean mShouldFocus;
    private int mFocusPosition;

    MatchWithAllianceItem(String matchCSV) {
        String[] split = matchCSV.split(",");
        mMatchNumber = Integer.valueOf(split[0].trim());
        mTeams = new int[6];
        for (int i = 1; i < 7; i++) {
            mTeams[i - 1] = Integer.valueOf(split[i].trim());
        }
        mShouldFocus = false;
    }

    MatchWithAllianceItem(MatchWithAllianceItem other) {
        this(other, other.getFocusPosition());
        mShouldFocus = other.shouldFocus();
    }

    MatchWithAllianceItem(MatchWithAllianceItem other, int focusPosition) {
        mMatchNumber = other.getMatchNumber();
        mTeams = other.getTeams();
        mShouldFocus = true;
        mFocusPosition = focusPosition;
    }

    public int getFocusPosition() {
        return mFocusPosition;
    }

    public boolean shouldFocus() {
        return mShouldFocus;
    }

    public int getTeamAt(int i) {
        return mTeams[i];
    }

    public int getTeamAtPosition(int position) {
        switch (position) {
            case RobotPosition.RED1:
                return mTeams[0];
            case RobotPosition.RED2:
                return mTeams[1];
            case RobotPosition.RED3:
                return mTeams[2];
            case RobotPosition.BLUE1:
                return mTeams[3];
            case RobotPosition.BLUE2:
                return mTeams[4];
            case RobotPosition.BLUE3:
                return mTeams[5];
        }
        return 0;
    }

    private int[] getTeams() {
        return mTeams;
    }

    public int getMatchNumber() {
        return mMatchNumber;
    }
}
