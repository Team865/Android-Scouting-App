package ca.warp7.android.scouting.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ca.warp7.android.scouting.res.AppResources;

/**
 * Data Model for reading metrics, settings,
 * match schedules, and constant strings.
 * Used as a singleton throughout the application
 * @since v1.0.0
 */

public class Specs {

    private String specsId;
    private String boardName;
    private String event;
    private String alliance;

    private ArrayList<Integer> matchSchedule = new ArrayList<>();

    private ArrayList<DataConstant> dataConstants = new ArrayList<>();

    private ArrayList<ScoutingLayout> layouts = new ArrayList<>();


    private Specs(String json) throws JSONException {

        JSONObject specs_json = new JSONObject(json);

        specsId = specs_json.getString(ID);
        boardName = specs_json.getString(BOARD_NAME);

        event = specs_json.optString(EVENT, "");
        alliance = specs_json.optString(ALLIANCE, "N");

        if (specs_json.has(MATCH_SCHEDULE)) {
            JSONArray schedule = specs_json.getJSONArray(MATCH_SCHEDULE);
            for (int i = 0; i < schedule.length(); i++) {
                matchSchedule.add(schedule.getInt(i));
            }
        }

        if (specs_json.has(CONSTANTS)) {
            JSONArray constants = specs_json.getJSONArray(CONSTANTS);
            for (int i = 0; i < constants.length(); i++) {
                dataConstants.add(new DataConstant(i, constants.getJSONObject(i)));
            }
        }

        if (specs_json.has(LAYOUT)) {
            JSONArray layoutsArray = specs_json.getJSONArray(LAYOUT);
            for (int i = 0; i < layoutsArray.length(); i++) {
                layouts.add(new ScoutingLayout(layoutsArray.getJSONObject(i)));
            }
        }
    }


    public boolean hasMatchSchedule() {
        return !matchSchedule.isEmpty();
    }

    public boolean hasIndexInConstants(int id) {
        return id >= 0 && id < dataConstants.size();
    }

    public boolean matchIsInSchedule(int m, int t) {
        return hasMatchSchedule() &&
                t == (m < matchSchedule.size() && m >= 0 ? matchSchedule.get(m) : -1);
    }

    public String getAlliance() {
        return alliance;
    }

    public String getBoardName() {
        return boardName;
    }

    public String getEvent() {
        return event.isEmpty() ? "No Event" : event;
    }

    public String getSpecsId() {
        return specsId;
    }


    public DataConstant getDataConstantByIndex(int id) {
        return dataConstants.get(id);
    }

    public DataConstant getDataConstantByStringID(String id) {

        for (DataConstant dc : dataConstants) {
            if (dc.getId().equals(id)) {
                return dc;
            }
        }
        return null;
    }

    public ArrayList<ScoutingLayout> getLayouts() {
        return layouts;
    }


    static String readFile(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();

        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        br.close();
        return sb.toString();
    }

    private static final String
            ID = "id",
            BOARD_NAME = "board",
            ALLIANCE = "alliance",
            EVENT = "event",
            MATCH_SCHEDULE = "schedule",
            LAYOUT = "layout",
            CONSTANTS = "data";


    private static Specs activeSpecs = null;

    public static boolean hasInstance() {
        return activeSpecs != null;
    }

    public static Specs getInstance() {
        return activeSpecs;
    }

    public static Specs setInstance(String filename) {
        try {

            activeSpecs = new Specs(readFile(new File(AppResources.getSpecsRoot(), filename)));

        } catch (IOException | JSONException e) {

            e.printStackTrace();
            activeSpecs = null;
        }
        return activeSpecs;
    }

}
