package ca.warp7.android.scouting;

import android.content.Context;
import android.os.Vibrator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


class Match {

//TODO move the formatting of individual data into MatchData

    class MatchData {
        private int typeIndex;
        private int value;
        private int undoFlag = 0;

        MatchData(int typeIndex, int value) {
            this.typeIndex = typeIndex;
            this.value = value;
        }

        void undoFlagOn() {undoFlag = 1;}

        void undoFlagOff() {undoFlag = 0;}

        void setValue(int value) {this.value = value;}

        int getIndex() {return typeIndex;}

        int getValue() {return value;}


        boolean isUndone(){return undoFlag != 0;}

        // Convert into max 16 bit int

        int toIntValue() {return undoFlag << 15 | typeIndex << 8 | value;}
    }


    // Basic variables for each match

    private int matchNumber;
    private int teamNumber;
    private String scoutName;

    // Board

    private Board board;


    // The time when match started

    private int timestamp = 0;


    // Comments by the scout

    //private String comments = "";

    // Time store

    private int lastRecordedTime = -1;


    // Data values

    private ArrayList<MatchData> data = new ArrayList<MatchData>();


    Match(int matchNumber, int teamNumber, String scoutName) {

        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.scoutName = scoutName;

        board = new Board();
        timestamp = (int) (System.currentTimeMillis() / 1000);
    }

    // Methods for setting data

    /*void comment(String comments) {
        this.comments = comments;
    }*/


    void pushState(int index, int value) {
        if (index < 0 || index > 127)
            return;
        for (MatchData md : data){
            if(md.getIndex() == index){
                md.setValue(value);
                return;
            }
        }
        data.add(new MatchData(index, value));
    }


    void pushElapsed(int index, TimedScoutingActivity activity){
        int elapsed = (int) (System.currentTimeMillis() / 1000) - timestamp;
        if (elapsed != lastRecordedTime && elapsed < Static.MATCH_LENGTH) {
            MatchData md = new MatchData(index, elapsed);
            data.add(md);
            lastRecordedTime = elapsed;

            ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE))
                    .vibrate(50);
        }
    }


    // Methods for generating strings

    private String getHeader() {
        return matchNumber + "_" + teamNumber + "_" + scoutName;
    }

    private String getDataCode() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(Static.formatHex(timestamp, 8)).append("_")
                .append(Static.formatHex(board.getBoardId(),8)).append("_");

        for (MatchData md : data)
            sb.append(Static.formatHex(md.toIntValue(), 4));

        sb.append("_");

        /*for (char ch : comments.toCharArray())
            sb.append(Integer.toHexString((int) ch));*/

        return sb.toString();
    }

    String encode() {
        return getHeader() + "_" + getDataCode();
    }

    String format() {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        StringBuilder sb = new StringBuilder();
        sb
                .append(Static.formatLeftByLength("Starting", 10, " "))
                .append(sdf.format(new Date(timestamp * 1000L)))
                .append('\n')
                .append(Static.formatLeftByLength("Match", 10, " "))
                .append(matchNumber)
                .append('\n')
                .append(Static.formatLeftByLength("Team", 10, " "))
                .append(teamNumber)
                .append('\n')
                .append(Static.formatLeftByLength("Scouter", 10, " "))
                .append(scoutName)
                .append('\n')
                .append(Static.formatLeftByLength("Board", 10, " "))
                .append(board.getBoardName())
                .append('\n')
                .append(Static.formatLeftByLength("Board #", 10, " "))
                .append("0x-")
                .append(Static.formatHex(board.getBoardId(), 8))
                .append("\nData:\n\n");

        for (MatchData md : data)
            sb
                    .append("\t(")
                    .append(Static.formatRightByLength(String.valueOf(md.getIndex()),
                            3, " "))
                    .append(", ")
                    .append(Static.formatRightByLength(String.valueOf(md.getValue()),
                            3, " "))
                    .append(")")
                    .append(md.isUndone()?"[Undo]" : "")
                    .append('\n');


        //sb.append("\nComments:").append(comments);

        //sb.append("\n\n\nEncoded\n\n\"").append(encode()).append("\"");

        return sb.toString();
    }

}
