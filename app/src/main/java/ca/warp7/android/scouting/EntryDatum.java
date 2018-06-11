package ca.warp7.android.scouting;

/**
 * Stores and integer-encodes a single datum in a match scouting session
 */

@SuppressWarnings("SameParameterValue")
final class EntryDatum {

    private int type;
    private int value;
    private int undoFlag = 0;
    private int stateFlag = 0;

    EntryDatum(int type, int value) {
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
