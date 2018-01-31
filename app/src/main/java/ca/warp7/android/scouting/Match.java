package ca.warp7.android.scouting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Match {

    class MatchData {
        private int typeIndex;
        private int value;
        private int undoFlag;

        MatchData(int typeIndex, int value) {
            this.typeIndex = typeIndex;
            this.value = value;
            this.undoFlag = 0;
        }

        void undo() {undoFlag = 1;}

        void redo() {undoFlag = 0;}

        void setValue(int value) {this.value = value;}

        int getIndex() {return typeIndex;}

        int getValue() {return value;}


        boolean getUndoFlag(){return undoFlag != 0;}

        // Convert into max 16 bit long int

        int toIntValue() {return typeIndex << 9 | undoFlag << 8 | value;}
    }


    // Basic variables for each match

    private int boardId;
    private int matchNumber;
    private int teamNumber;
    private String scoutName;

    private String boardName;


    // The time when match started

    private int timestamp = 0;


    // Comments by the scout

    private String comments = "";


    // Data values

    private ArrayList<MatchData> data = new ArrayList<MatchData>();


    public Match(int boardId,
                 String boardName,
                 int matchNumber,
                 int teamNumber,
                 String scoutName) {

        this.boardId = boardId;
        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.scoutName = scoutName;
        this.boardName = boardName;

    }

    // Methods for setting data

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void start() {
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }

    public void pushState(int index, int value) {
        for (MatchData md : data){
            if(md.getIndex() == index){
                md.setValue(value);
                return;
            }
        }
        data.add(new MatchData(index, value));
    }

    public void pushElapsed(int index){
        int elapsed = (int) (System.currentTimeMillis() / 1000) - timestamp;
        data.add(new MatchData(index, elapsed));
    }

    // Methods for generating strings

    private String getHeader() {
        return matchNumber + "_" + teamNumber + "_" + scoutName;
    }

    private String getDataCode() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(Shared.formatHex(timestamp, 8)).append(":")
                .append(Shared.formatHex(boardId,8)).append(":");

        for (MatchData md : data)
            sb.append(Shared.formatHex(md.toIntValue(), 4));

        sb.append(":");

        for (char ch : comments.toCharArray())
            sb.append(Integer.toHexString((int) ch));

        return sb.toString();
    }

    public String encode() {
        return getHeader() + "_" + getDataCode();
    }

    public String format() {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        StringBuilder sb = new StringBuilder();
        sb
                .append('\n')
                .append(Shared.formatLeftByLength("Match:", 10, " "))
                .append(matchNumber)
                .append('\n')
                .append(Shared.formatLeftByLength("Team:", 10, " "))
                .append(teamNumber)
                .append('\n')
                .append(Shared.formatLeftByLength("Scouter:", 10, " "))
                .append(scoutName)
                .append('\n')
                .append(Shared.formatLeftByLength("Board:", 10, " "))
                .append(boardName)
                .append('\n')
                .append(Shared.formatLeftByLength("Board #:", 10, " "))
                .append("0x")
                .append(Shared.formatHex(boardId, 8))
                .append('\n')
                .append(Shared.formatLeftByLength("Start:", 10, " "))
                .append(sdf.format(new Date(timestamp * 1000L)))
                .append("\ndata:\n\n");

        for (MatchData md : data){
            sb.append("(").append(md.getIndex()).append(", ").append(md.getValue()).append(") ");
            if(md.getUndoFlag())
                sb.append("[Undo]");
            sb.append('\n');
        }

        sb.append("\ncomments:\n").append(comments);

        return sb.toString();
    }

    // TODO Comment Encoder

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

}
