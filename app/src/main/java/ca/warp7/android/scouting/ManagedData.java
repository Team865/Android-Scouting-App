package ca.warp7.android.scouting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ManagedData {

    enum RobotPosition {
        RED1, RED2, RED3, BLUE1, BLUE2, BLUE3
    }

    static class MatchInfo {
        private int[] mTeams;
        private int mMatchNumber;

        MatchInfo(String matchCSV) {
            String[] split = matchCSV.split(",");
            mMatchNumber = Integer.valueOf(split[0]);
            mTeams = new int[6];
            for (int i = 1; i < 7; i++) {
                mTeams[i - 1] = Integer.valueOf(split[i]);
            }
        }

        public int getTeamAt(int i) {
            return mTeams[i];
        }

        public int[] getTeams() {
            return mTeams;
        }

        public int getMatchNumber() {
            return mMatchNumber;
        }
    }

    static class MatchTable {
        List<MatchInfo> mMatches = new ArrayList<>();


        MatchTable() throws IOException {

            File mtf = new File(Specs.getSpecsRoot(), "match-table.csv");
            BufferedReader br = new BufferedReader(new FileReader(mtf));

            br.readLine(); // Removes the headers line
            String line = br.readLine();

            while (line != null) {
                mMatches.add(new MatchInfo(line));
                line = br.readLine();
            }

            br.close();
        }

        String[] getTeamsArrayForBoard(int board) {
            String[] result = new String[mMatches.size()];
            for (int i = 0; i < mMatches.size(); i++) {
                result[i] = String.valueOf(mMatches.get(i).getTeamAt(board));
            }
            return result;
        }
    }


}
