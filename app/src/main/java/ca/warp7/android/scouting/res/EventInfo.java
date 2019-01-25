package ca.warp7.android.scouting.res;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class EventInfo {

    static class NotProperEventFormat extends Exception {
    }

    private final File mEventRoot;
    private String mEventName;

    EventInfo(File eventFilePath) throws NotProperEventFormat {
        mEventRoot = eventFilePath;
        File eventJSONPath = new File(mEventRoot, "event.json");
        try {
            String eventJSONString = AppResources.INSTANCE.readFile(eventJSONPath);
            JSONObject eventJSON = new JSONObject(eventJSONString);
            mEventName = eventJSON.getString("event_name");
        } catch (IOException | JSONException e) {
            throw new NotProperEventFormat();
        }
    }

    public String getEventName() {
        return mEventName;
    }

    public File getMatchTableRoot() {
        return new File(mEventRoot, "match-table.csv");
    }
}
