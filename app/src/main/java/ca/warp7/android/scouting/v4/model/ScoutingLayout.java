package ca.warp7.android.scouting.v4.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @since v0.2.0
 */

public class ScoutingLayout {

    private final ArrayList<String[]> fields = new ArrayList<>();
    private String title;


    ScoutingLayout(JSONObject data) throws JSONException {
        title = data.getString("title");

        JSONArray fieldRows = data.getJSONArray("fields");

        for (int i = 0; i < fieldRows.length(); i++) {
            JSONArray fieldRow = fieldRows.getJSONArray(i);

            if (fieldRow.length() != 0) {
                String[] fieldsArray = new String[fieldRow.length()];

                for (int j = 0; j < fieldRow.length(); j++) {
                    fieldsArray[j] = fieldRow.getString(j);
                }

                fields.add(fieldsArray);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String[]> getFields() {
        return fields;
    }
}
