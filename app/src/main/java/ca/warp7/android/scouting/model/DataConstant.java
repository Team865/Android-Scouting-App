package ca.warp7.android.scouting.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataConstant {

    private static final String
            C_ID = "id",
            C_LOG = "log",
            C_LABEL = "label",
            C_LABEL_ON = "label_on",
            C_TYPE = "type",
            C_MAX = "max",
            C_CHOICES = "choices";

    private static final String
            T_TIMESTAMP = "timestamp",
            T_CHOICE = "choice",
            T_RATING = "rating",
            T_CHECKBOX = "checkbox",
            T_DURATION = "duration";


    public static final int
            TIMESTAMP = 0,
            CHOICE = 1,
            RATING = 2,
            CHECKBOX = 3,
            DURATION = 4;


    private static int toIntegerType(String type) {
        switch (type) {
            case DataConstant.T_TIMESTAMP:
                return DataConstant.TIMESTAMP;

            case DataConstant.T_CHOICE:
                return DataConstant.CHOICE;

            case DataConstant.T_RATING:
                return DataConstant.RATING;

            case DataConstant.T_CHECKBOX:
                return DataConstant.CHECKBOX;

            case DataConstant.T_DURATION:
                return DataConstant.DURATION;
            default:
                return -1;
        }
    }


    private final int index;

    private final String id;

    private final String logTitle;

    private final String label;

    private final String labelOn;

    private final int max;

    private final int type;

    private final String[] choices;


    DataConstant(int index, JSONObject data) throws JSONException {

        this.index = index;
        this.id = data.getString(C_ID);


        logTitle = data.optString(C_LOG, "$" + id);
        label = data.optString(C_LABEL, "$" + id);
        labelOn = data.optString(C_LABEL_ON, "$" + id);

        max = data.optInt(C_MAX, -1);

        type = data.has(C_TYPE) ? toIntegerType(data.getString(C_TYPE)) : -1;

        if (data.has(C_CHOICES)) {
            JSONArray ca = data.getJSONArray(C_CHOICES);
            choices = new String[ca.length()];

            for (int i = 0; i < choices.length; i++) {
                choices[i] = ca.getString(i);
            }

        } else {
            choices = new String[]{"None"};
        }
    }

    public int getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }

    public String getLogTitle() {
        return logTitle;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelOn() {
        return labelOn;
    }

    public int getType() {
        return type;
    }

    public int getMax() {
        return max;
    }

    public String[] getChoices() {
        return choices;
    }

    public String format(int v) {
        switch (type) {
            case TIMESTAMP:
            case DURATION:
                return v + " s";

            case CHOICE:
                return v >= 0 && v < choices.length ?
                        "<" + choices[v] + ">" : String.valueOf(v);

            case RATING:
                return v + "/" + max;

            case CHECKBOX:
                return v != 0 ? "Yes" : "No";

            default:
                return String.valueOf(v);
        }
    }

}
