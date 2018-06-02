package ca.warp7.android.scouting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Data model for a specific match
 */

@SuppressWarnings("SameParameterValue")
final class Encoder {

    private int matchNumber;
    private int teamNumber;
    private String scoutName;

    private int timestamp;

    private Specs specs;

    private ArrayList<Datum> dataStack;

    Encoder(int matchNumber, int teamNumber, String scoutName) {
        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.scoutName = scoutName;

        timestamp = (int) (System.currentTimeMillis() / 1000);

        specs = Specs.getInstance();

        dataStack = new ArrayList<>();
    }

    void push(int t, int v, int s) {
        if (t < 0 || t > 63) {
            return;
        }
        Datum d = new Datum(t, v);
        d.setStateFlag(s);
        dataStack.add(d);
    }

    Specs.DataConstant undo() {
        for (int i = dataStack.size() - 1; i >= 0; i--) {

            Datum datum = dataStack.get(i);

            if (datum.getUndoFlag() == 0) {
                datum.setUndoFlag(1);
                return specs.getDataConstantByIndex(datum.getType());
            }
        }
        return null;
    }

    int getCount(int t){
        int total = 0;
        for (Datum d : dataStack){
            if (d.getType() == t && d.getUndoFlag() == 0){
                total++;
            }
        }
        return total;
    }

    int getLastValue(int t, int defaultValue){
        for (int i = dataStack.size() - 1; i >= 0; i--){
            Datum d = dataStack.get(i);
            if (d.getType() == t && d.getUndoFlag() == 0){
                return d.getValue();
            }
        }
        return defaultValue;
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

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        StringBuilder sb = new StringBuilder();

        sb
                .append('\n')

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

                .append(formatLeft("Data", 21, " "))
                .append("Value")
                .append("\n")
                .append(new String(new char[31]).replace("\0", "-"));

        for (Datum d : dataStack) {
            sb.append("\n");

            int t = d.getType();

            if(specs.hasIndexInConstants(t)){
                Specs.DataConstant dc = specs.getDataConstantByIndex(t);
                sb
                        .append(formatLeft(dc.getLogTitle() +
                                (d.getStateFlag() == 0 ? "<Off>" : "") + " ", 20, " "))
                        .append(dc.format(d.getValue()))
                        .append(d.getUndoFlag() != 0 ? " Ⓤ" : "");
            } else {
                sb
                        .append(formatLeft(String.valueOf(t), 21, " "))
                        .append(String.valueOf(d.getValue()));
            }
        }

        sb
                .append('\n')
                .append(new String(new char[31]).replace("\0", "-"));

        return sb.toString();
    }


    private static String formatRight(String s, int d, String r) {
        String n = s;
        if (d > n.length())
            n = new String(new char[d - n.length()])
                    .replace("\0", r) + n;
        return n;
    }

    private static String formatLeft(String s, int d, String r) {
        String n = s;
        if (d > n.length())
            n += new String(new char[d - n.length()])
                    .replace("\0", r);
        return n;
    }

    private static String fillHex(int n, int digits) {
        return Encoder.formatRight(Integer.toHexString(n), digits, "0");
    }


    /**
     * Stores and integer-encodes a single datum in a match scouting session
     */
    static final class Datum {
        private int
                type,
                value,
                undoFlag = 0,
                stateFlag = 0;

        Datum(int type, int value) {
            this.type = type;
            this.value = value;
        }

        int getType() {
            return type;
        }

        int getValue() {
            return value;
        }

        int getUndoFlag() {
            return undoFlag;
        }

        int getStateFlag() {
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

}