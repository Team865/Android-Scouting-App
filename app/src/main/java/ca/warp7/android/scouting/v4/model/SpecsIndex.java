package ca.warp7.android.scouting.v4.model;

import ca.warp7.android.scouting.AppResources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @since v0.2.0
 */

public class SpecsIndex {

    private final ArrayList<String> files = new ArrayList<>();

    private final ArrayList<String> names = new ArrayList<>();


    public SpecsIndex(File file) {
        try {
            JSONObject index = new JSONObject(AppResources.INSTANCE.readFile(file));

            JSONArray files = index.getJSONArray("files");
            JSONArray names = index.getJSONArray("names");
            JSONArray ids = index.getJSONArray("identifiers");

            for (int i = 0; i < ids.length(); i++) {
                this.files.add(files.getString(i));
                this.names.add(names.getString(i));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public String getFileByName(String name) {
        return files.get(names.indexOf(name));
    }
}
