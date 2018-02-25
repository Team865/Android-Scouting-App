package ca.warp7.android.scouting;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Data Model for reading metrics, settings,
 * match schedules, and constant strings.
 * Used as a singleton throughout the application
 */

final class Specs {

    private String specsId;
    private String boardName;
    private String event;
    private String alliance;

    private int timer;

    private ArrayList<Integer> matchSchedule = new ArrayList<>();

    private ArrayList<DataConstant> dataConstants = new ArrayList<>();

    private ArrayList<Layout> layouts = new ArrayList<>();


    private Specs(String json) throws JSONException{

        JSONObject specs_json = new JSONObject(json);

        specsId = specs_json.getString(ID);
        boardName = specs_json.getString(BOARD_NAME);

        event = specs_json.optString(EVENT, "");
        timer = specs_json.optInt(TIMER, 150);
        alliance = specs_json.optString(ALLIANCE, "N");

        if(specs_json.has(MATCH_SCHEDULE)){
            JSONArray schedule = specs_json.getJSONArray(MATCH_SCHEDULE);
            for(int i = 0; i < schedule.length(); i++) {
                matchSchedule.add(schedule.getInt(i));
            }
        }

        if(specs_json.has(CONSTANTS)){
            JSONArray constants = specs_json.getJSONArray(CONSTANTS);
            for (int i = 0; i < constants.length(); i++){
                dataConstants.add(new DataConstant(i, constants.getJSONObject(i)));
            }
        }

        if(specs_json.has(LAYOUT)){
            JSONArray layoutsArray = specs_json.getJSONArray(LAYOUT);
            for (int i = 0; i < layoutsArray.length(); i++){
                layouts.add(new Layout(layoutsArray.getJSONObject(i)));
            }
        }
    }


    boolean hasMatchSchedule(){
        return !matchSchedule.isEmpty();
    }

    boolean hasIndexInConstants(int id) {
        return id >= 0 && id < dataConstants.size();
    }

    boolean matchIsInSchedule(int m, int t) {
        return hasMatchSchedule() &&
                t == (m < matchSchedule.size() && m >= 0 ? matchSchedule.get(m) : -1);
    }

    int getTimer() {
        return timer;
    }


    String getAlliance() {
        return alliance;
    }

    String getBoardName() {
        return boardName;
    }

    String getEvent(){
        return event.isEmpty() ? "No Event" : event;
    }

    String getSpecsId() {
        return specsId;
    }


    DataConstant getDataConstantByIndex(int id){
        return dataConstants.get(id);
    }

    DataConstant getDataConstantByStringID(String id) {

        for (DataConstant dc : dataConstants) {
            if (dc.getId().equals(id)) {
                return dc;
            }
        }
        return null;
    }

    ArrayList<Layout> getLayouts() {
        return layouts;
    }


    private static final String SPECS_ROOT = "Warp7/specs/";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static File getSpecsRoot() {
        File r = new File(Environment.getExternalStorageDirectory(), SPECS_ROOT);
        r.mkdirs();
        return r;
    }

    private static String readFile(File f) throws IOException {
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
            TIMER = "timer",
            MATCH_SCHEDULE = "schedule",
            LAYOUT = "layout",
            CONSTANTS = "data";


    private static Specs activeSpecs = null;

    static boolean hasInstance() {
        return activeSpecs != null;
    }

    static Specs getInstance() {
        return activeSpecs;
    }

    static Specs setInstance(String filename) {
        try {

            activeSpecs = new Specs(readFile(new File(getSpecsRoot(), filename)));

        } catch (IOException | JSONException e) {

            e.printStackTrace();
            activeSpecs = null;
        }
        return activeSpecs;
    }

    static final class DataConstant {

        private static final String
                C_ID = "id",
                C_LOG = "log",
                C_LABEL = "label",
                C_TYPE = "type",
                C_MAX = "max",
                C_CHOICES = "choices";

        private static final String
                T_TIME = "timestamp",
                T_CHOICE = "choice",
                T_RATING = "rating",
                T_CHECKBOX = "checkbox",
                T_DURATION = "duration";


        static final int
                TIME = 0,
                CHOICE = 1,
                RATING = 2,
                CHECKBOX = 3,
                DURATION = 4;


        private static int toIntegerType(String type){
            switch (type) {
                case DataConstant.T_TIME:
                    return DataConstant.TIME;

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


        final int index;

        final String id;

        final String logTitle;

        final String label;

        final int max;

        final int type;

        final String[] choices;


        DataConstant(int index, JSONObject data) throws JSONException {

            this.index = index;
            this.id = data.getString(C_ID);


            logTitle = data.optString(C_LOG, "$" + id);
            label = data.optString(C_LABEL, "$" + id);
            max = data.optInt(C_MAX, -1);

            type = data.has(C_TYPE) ? toIntegerType(data.getString(C_TYPE)) : -1;

            if (data.has(C_CHOICES)){
                JSONArray ca = data.getJSONArray(C_CHOICES);
                choices = new String[ca.length()];

                for (int i = 0; i < choices.length; i++){
                    choices[i] = ca.getString(i);
                }

            } else {
                choices = new String[]{"None"};
            }
        }

        int getIndex() {
            return index;
        }

        String getId() {
            return id;
        }

        String getLogTitle() {
            return logTitle;
        }

        String getLabel() {
            return label;
        }

        int getType() {
            return type;
        }

        int getMax() {
            return max;
        }

        String[] getChoices() {
            return choices;
        }

        String format(int v){
            switch (type){
                case TIME:
                case DURATION:
                    return String.format(Locale.CANADA,"%dm %02ds",
                            v / 60, v % 60);

                case CHOICE:
                    return v >= 0 && v < choices.length ?
                            "<" + choices[v] + ">" : String.valueOf(v);

                case RATING:
                    return v + " out of " + max;

                case CHECKBOX:
                    return v != 0? "True" : "False";

                default:
                    return String.valueOf(v);
            }
        }

    }

    static final class Index {

        private ArrayList<String> files = new ArrayList<>();

        ArrayList<String> names = new ArrayList<>();

        ArrayList<String> identifiers = new ArrayList<>();


        Index(File file){
            try {
                JSONObject index = new JSONObject(readFile(file));

                JSONArray files = index.getJSONArray("files");
                JSONArray names = index.getJSONArray("names");
                JSONArray ids = index.getJSONArray("identifiers");

                for(int i = 0; i < ids.length(); i++){
                    this.files.add(files.getString(i));
                    this.names.add(names.getString(i));
                    this.identifiers.add(ids.getString(i));
                }

            } catch (IOException | JSONException e){
                e.printStackTrace();
            }
        }

        ArrayList<String> getNames() {
            return names;
        }

        String getFileByName(String name) {
            return files.get(names.indexOf(name));
        }
    }

    static final class Layout{

        String title;

        ArrayList<String[]> fields = new ArrayList<>();


        Layout(JSONObject data) throws JSONException {
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

        String getTitle() {
            return title;
        }

        ArrayList<String[]> getFields() {
            return fields;
        }
    }
}
