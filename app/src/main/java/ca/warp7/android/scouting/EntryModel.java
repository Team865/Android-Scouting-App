package ca.warp7.android.scouting;

import java.util.ArrayList;

/**
 * Data model for a specific match
 */

@SuppressWarnings({"SameParameterValue", "unused"})
class EntryModel {

    private int matchNumber;
    private int teamNumber;
    private String scoutName;

    private int timestamp;

    private Specs specs;

    private ArrayList<EntryDatum> dataStack;

    EntryModel(int matchNumber, int teamNumber, String scoutName) {
        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.scoutName = scoutName;

        timestamp = (int) (System.currentTimeMillis() / 1000);

        specs = Specs.getInstance();

        dataStack = new ArrayList<>();
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public String getScoutName() {
        return scoutName;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Specs getSpecs() {
        return specs;
    }

    public ArrayList<EntryDatum> getDataStack() {
        return dataStack;
    }

    void push(int t, int v, int s) {
        if (t < 0 || t > 63) {
            return;
        }
        EntryDatum d = new EntryDatum(t, v);
        d.setStateFlag(s);
        dataStack.add(d);
    }

    Specs.DataConstant undo() {
        for (int i = dataStack.size() - 1; i >= 0; i--) {

            EntryDatum datum = dataStack.get(i);

            if (datum.getUndoFlag() == 0) {
                datum.setUndoFlag(1);
                return specs.getDataConstantByIndex(datum.getType());
            }
        }
        return null;
    }

    int getCount(int t) {
        int total = 0;
        for (EntryDatum d : dataStack) {
            if (d.getType() == t && d.getUndoFlag() == 0) {
                total++;
            }
        }
        return total;
    }

    int getLastValue(int t) {
        for (int i = dataStack.size() - 1; i >= 0; i--) {
            EntryDatum d = dataStack.get(i);
            if (d.getType() == t && d.getUndoFlag() == 0) {
                return d.getValue();
            }
        }
        return 0;
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

        for (EntryDatum d : dataStack)
            sb.append(fillHex(d.encode(), 4));

        sb.append("_");

        return sb.toString();
    }

    String encode() {
        return head() + "_" + dataCode();
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
        return EntryModel.formatRight(Integer.toHexString(n), digits, "0");
    }


}