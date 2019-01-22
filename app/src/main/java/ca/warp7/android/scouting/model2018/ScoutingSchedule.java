package ca.warp7.android.scouting.model2018;

import ca.warp7.android.scouting.abstraction.ScoutingScheduleItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since v0.4.2
 */

public class ScoutingSchedule {
    private final List<ScoutingScheduleItem> mCurrentlyScheduled;
    private final List<MatchWithAllianceItem> mFullSchedule;


    public ScoutingSchedule() {
        mCurrentlyScheduled = new ArrayList<>();
        mFullSchedule = new ArrayList<>();
    }

    public void loadFullScheduleFromCSV(File matchTableFile) throws IOException {

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

    public void scheduleForDisplayOnly() {
        mCurrentlyScheduled.clear();
        mCurrentlyScheduled.add(new ButtonItem("hi"));
        for (MatchWithAllianceItem item : mFullSchedule) {
            mCurrentlyScheduled.add(new MatchWithAllianceItem(item));
        }
    }

    public void scheduleAllAtRobotPosition(int position) {
        mCurrentlyScheduled.clear();
        for (MatchWithAllianceItem item : mFullSchedule) {
            mCurrentlyScheduled.add(new MatchWithAllianceItem(item, position));
        }
    }

    public List<ScoutingScheduleItem> getCurrentlyScheduled() {
        return mCurrentlyScheduled;
    }
}
