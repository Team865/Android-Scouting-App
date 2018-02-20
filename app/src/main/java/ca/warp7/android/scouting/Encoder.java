package ca.warp7.android.scouting;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Data model for a specific match
 */

final class Encoder {

    /**
     * Stores and integer-encode a single datum
     */
    static final class Datum{
        private int
                type,
                value,
                undoFlag = 0,
                stateFlag = 0;

        Datum (int type, int value) {
            this.type = type;
            this.value = value;
        }

        int getType() {
            return type;
        }

        public int getValue() {
            return value;
        }

        public int getUndoFlag() {
            return undoFlag;
        }

        public int getStateFlag() {
            return stateFlag;
        }

        void setValue(int value) {

            this.value = value;
        }

        void setUndoFlag(int undoFlag) {
            this.undoFlag = undoFlag;
        }

        void setStateFlag(int stateFlag) {
            this.stateFlag = stateFlag;
        }

        int encode() {
            return undoFlag << 15 | stateFlag << 14 | type << 8 | value;
        }

    }

    private static String formatRight(String s, int d, String r){
        String n = s;
        if (d > n.length())
            n = new String(new char[d - n.length()])
                    .replace("\0", r) + n;
        return n;
    }

    private static String formatLeft(String s, int d, String r){
        String n = s;
        if (d > n.length())
            n += new String(new char[d - n.length()])
                    .replace("\0", r);
        return n;
    }

    private static String fillHex(int n, int digits){
        return Encoder.formatRight(Integer.toHexString(n), digits, "0");
    }

    private int matchNumber;
    private int teamNumber;
    private String scoutName;

    private int timestamp;

    private Specs specs;

    ArrayList<Datum> dataStack;

    Encoder(int matchNumber, int teamNumber, String scoutName) {
        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.scoutName = scoutName;

        timestamp = (int) (System.currentTimeMillis() / 1000);

        specs = Specs.getInstance();

        dataStack = new ArrayList<>();
    }

    void push(int t, int v){
        dataStack.add(new Datum(t, v));
        Log.i("push", t + " " + v);
    }

    void pushOnce(int t, int v){
        if(t < 0 || t > 63){
            return;
        }
        for (Datum d : dataStack){
            if (t == d.getType()){
                d.setValue(v);
                return;
            }
        }
        push(t, v);
    }

    private String head() {
        return matchNumber + "_" + teamNumber + "_" + scoutName;
    }

    private String dataCode() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(fillHex(timestamp, 8))
                .append("_")
                .append(specs.getSpecsId())
                .append("_");

        for (Datum d : dataStack)
            sb.append(fillHex(d.encode(), 4));

        sb.append("_");

        return sb.toString();
    }

    String encode(){
        return head() + "_" + dataCode();
    }

    String format() {

        SimpleDateFormat sdf = new SimpleDateFormat("YY/MM/dd HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        StringBuilder sb = new StringBuilder();

        sb
                .append(formatLeft("Match Number:", 16, " "))
                .append(matchNumber)
                .append('\n')

                .append(formatLeft("Team Number:", 16, " "))
                .append(teamNumber)
                .append('\n')

                .append(formatLeft("Start Time:", 16, " "))
                .append(sdf.format(new Date(timestamp * 1000L)))
                .append('\n')

                .append(formatLeft("Scouter:", 16, " "))
                .append(scoutName)
                .append('\n')

                .append(formatLeft("Board:", 16, " "))
                .append(specs.getBoardName())
                .append('\n')

                .append(formatLeft("Alliance:", 16, " "))
                .append(specs.getAlliance())
                .append("\n\n")

                .append(formatLeft("Data", 23, " "))
                .append(formatLeft("Value", 14, " "))
                .append("Undo\n")
                .append(new String(new char[41]).replace("\0", "-"));

        for (Datum d : dataStack) {
            sb.append("\n");

            int t = d.getType();

            if(specs.hasIndexInConstants(t)){
                Specs.DataConstant dc = specs.getDataConstantByIndex(t);
                sb
                        .append(formatLeft(dc.getLogTitle(), 23, " "))
                        .append(formatLeft(dc.formatValue(d.getValue()), 14, " "));
            } else {
                sb
                        .append(formatLeft(String.valueOf(t), 23, " "))
                        .append(formatLeft(String.valueOf(d.getValue()), 14, " "));
            }
            sb.append(d.getUndoFlag() != 0 ? "Yes" : "No");
        }

        sb
                .append('\n')
                .append(new String(new char[41]).replace("\0", "-"));

        return sb.toString();
    }

}