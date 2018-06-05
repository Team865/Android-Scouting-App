package ca.warp7.android.scouting;

import org.json.JSONObject;

/**
 * Representative of a data point inside the template
 */

@SuppressWarnings({"WeakerAccess", "unused"})
class DataPoint {

    protected JSONObject mJSON;

    private int mIndex;
    private String mId;
    private String logTitle;
    private String label;


    public DataPoint(int index, JSONObject json) {
        mIndex = index;
        mJSON = json;
        mId = mJSON.optString("id", String.valueOf(mIndex));
        logTitle = mJSON.optString("log", "$" + mId);
        label = mJSON.optString("label", "$" + mId);
    }

    public int getIndex() {
        return mIndex;
    }

    public String getIdentifier() {
        return mId;
    }

    public String getLogTitle() {
        return logTitle;
    }

    public String getLabel() {
        return label;
    }
}
