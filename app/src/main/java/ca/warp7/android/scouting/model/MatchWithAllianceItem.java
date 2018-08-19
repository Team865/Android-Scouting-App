package ca.warp7.android.scouting.model;


public class MatchWithAllianceItem implements ScoutingScheduleItem {
    private int[] mTeams;
    private int mMatchNumber;
    private boolean mShouldFocus;
    private RobotPosition mFocusPosition;

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

    MatchWithAllianceItem(MatchWithAllianceItem other, RobotPosition focusPosition) {
        mMatchNumber = other.getMatchNumber();
        mTeams = other.getTeams();
        mShouldFocus = true;
        mFocusPosition = focusPosition;
    }

    public RobotPosition getFocusPosition() {
        return mFocusPosition;
    }

    public boolean shouldFocus() {
        return mShouldFocus;
    }

    public int getTeamAt(int i) {
        return mTeams[i];
    }

    private int[] getTeams() {
        return mTeams;
    }

    public int getMatchNumber() {
        return mMatchNumber;
    }
}
