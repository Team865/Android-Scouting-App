package ca.warp7.android.scouting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.warp7.android.scouting.model.RobotPosition;
import ca.warp7.android.scouting.model.ScoutingScheduleItem;

class ManagedData {

    static class MatchWithAllianceItem implements ScoutingScheduleItem {
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

        RobotPosition getFocusPosition() {
            return mFocusPosition;
        }

        boolean shouldFocus() {
            return mShouldFocus;
        }

        int getTeamAt(int i) {
            return mTeams[i];
        }

        int[] getTeams() {
            return mTeams;
        }

        int getMatchNumber() {
            return mMatchNumber;
        }
    }

    static class ScoutingSchedule {
        private List<ScoutingScheduleItem> mCurrentlyScheduled;
        private List<MatchWithAllianceItem> mFullSchedule;


        ScoutingSchedule() {
            mCurrentlyScheduled = new ArrayList<>();
            mFullSchedule = new ArrayList<>();
        }

        void loadFullScheduleFromCSV(File matchTableFile) throws IOException {

            mFullSchedule.clear();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(matchTableFile));

            bufferedReader.readLine(); // Removes the headers line
            String line = bufferedReader.readLine();

            while (line != null) {
                mFullSchedule.add(new MatchWithAllianceItem(line));
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        }

        void scheduleForDisplayOnly() {
            mCurrentlyScheduled.clear();
            for (MatchWithAllianceItem item : mFullSchedule) {
                mCurrentlyScheduled.add(new MatchWithAllianceItem(item));
            }
        }

        void scheduleAllAtRobotPosition(RobotPosition position) {
            mCurrentlyScheduled.clear();
            for (MatchWithAllianceItem item : mFullSchedule) {
                mCurrentlyScheduled.add(new MatchWithAllianceItem(item, position));
            }
        }

        public List<ScoutingScheduleItem> getCurrentlyScheduled() {
            return mCurrentlyScheduled;
        }
    }

}
