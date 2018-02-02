package ca.warp7.android.scouting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


class Match {



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

    private String comments = "";


    // Data values

    private ArrayList<MatchData> data = new ArrayList<MatchData>();


    Match(int matchNumber, int teamNumber, String scoutName) {

        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.scoutName = scoutName;

        board = new Board();
    }


    /*public Match(String d){
        String[] sections = d.split("_");
        this.match = Integer.parseInt(sections[1]);
        this.team = Integer.parseInt(sections[2]);
        this.scoutName = sections[4];

        //TODO change index
        this.boardId = Shared.parseHex32(sections[5].substring(8, 16));
        this.timestamp = Shared.parseHex32(sections[5].substring(16, 24));

        for (int i = 0; i < sections[6].length() / 4; i++) {
            String ivs = sections[6].substring(i * 4, i * 4 + 4);
            addData(Integer.parseInt(ivs.substring(0,2), 16), Integer.parseInt(ivs.substring(2,4), 16));
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < sections[7].length(); j += 2) {
            String str = sections[7].substring(j, j + 2);
            sb.append((char) Integer.parseInt(str, 16));
        }
        comments = sb.toString();
    }*/


    // Methods for setting data

    void comment(String comments) {
        this.comments = comments;
    }

    void start() {
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }

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

    void pushElapsed(int index){
        int elapsed = (int) (System.currentTimeMillis() / 1000) - timestamp;
        MatchData md = new MatchData(index, elapsed);
        data.add(md);
    }

    // Methods for generating strings

    private String getHeader() {
        return matchNumber + "_" + teamNumber + "_" + scoutName;
    }

    private String getDataCode() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(Shared.formatHex(timestamp, 8)).append("_")
                .append(Shared.formatHex(board.getBoardId(),8)).append("_");

        for (char ch : comments.toCharArray())
            sb.append(Integer.toHexString((int) ch));

        sb.append("_");

        for (MatchData md : data)
            sb.append(Shared.formatHex(md.toIntValue(), 4));

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
                .append(Shared.formatLeftByLength("Starting", 10, " "))
                .append(sdf.format(new Date(timestamp * 1000L)))
                .append('\n')
                .append(Shared.formatLeftByLength("Match", 10, " "))
                .append(matchNumber)
                .append('\n')
                .append(Shared.formatLeftByLength("Team", 10, " "))
                .append(teamNumber)
                .append('\n')
                .append(Shared.formatLeftByLength("Scouter", 10, " "))
                .append(scoutName)
                .append('\n')
                .append(Shared.formatLeftByLength("Board", 10, " "))
                .append(board.getBoardName())
                .append('\n')
                .append(Shared.formatLeftByLength("Board #", 10, " "))
                .append("0x")
                .append(Shared.formatHex(board.getBoardId(), 8))
                .append("\nData:\n\n");

        for (MatchData md : data)
            sb
                    .append("(")
                    .append(md.getIndex())
                    .append(", ")
                    .append(md.getValue())
                    .append(") ")
                    .append(md.isUndone()?"[Undo]" : "")
                    .append('\n');


        sb.append("\nComments:").append(comments);

        //sb.append("\n\n\nEncoded\n\n\"").append(encode()).append("\"");

        return sb.toString();
    }

}
