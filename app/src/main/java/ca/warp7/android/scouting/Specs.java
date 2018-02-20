package ca.warp7.android.scouting;

import android.os.Environment;
import android.util.Log;

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
 * Data Model for reading files with constants
 * Can be used as a singleton
 */

final class Specs {

    static final class DataConstant {

        private static final String
                C_ID = "id",
                C_LOG = "log",
                C_LABEL = "display",
                C_TYPE = "type",
                C_MAX = "max",
                C_CHOICES = "choices",

                T_TIME = "timestamp",
                T_CHOICE = "choice",
                T_RATING = "rating";


        static final int
                TIME = 0,
                CHOICE = 1,
                RATING = 2;


        final int index;

        final String id;

        final String logTitle;

        final String label;

        final int type;

        final int max;

        final String[] choices;

        DataConstant(int index, JSONObject data) throws JSONException {

            this.index = index;
            this.id = data.getString(C_ID);

            logTitle = data.has(C_LOG) ? data.getString(C_LOG) : "$" + id;

            label = data.has(C_LABEL) ? data.getString(C_LABEL) : "$" + id;

            type = data.has(C_TYPE) ? toIntegerType(data.getString(C_TYPE)) : -1;

            max = data.has(C_MAX) ? data.getInt(C_MAX) : -1;

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

        public int getType() {
            return type;
        }

        public int getMax() {
            return max;
        }

        public String[] getChoices() {
            return choices;
        }

        public String formatValue(int v){
            switch (type){
                case TIME:
                    return String.format(Locale.CANADA,"%dm %02ds",
                            v / 60, v % 60);

                case CHOICE:
                    if (v >= 0 && v < choices.length){
                        return "<" + choices[v] + ">";
                    }
                    return String.valueOf(v);

                case RATING:
                    return v + " out of " + max;

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

        public ArrayList<String> getNames() {
            return names;
        }

        public String getFileByName(String name) {
            return files.get(names.indexOf(name));
        }
    }

    static final class Layout{

        String title;

        public Layout(JSONObject data) throws JSONException {
            title = data.getString("title");
        }

        public String getTitle() {
            return title;
        }
    }

    private static int parseHex32(String h) {
        return Integer.parseInt(h.substring(0, 4), 16) * 65536
                + Integer.parseInt(h.substring(4, 8), 16);
    }

    private static int toIntegerType(String type){
        switch (type) {
            case DataConstant.T_TIME:
                return DataConstant.TIME;

            case DataConstant.T_CHOICE:
                return DataConstant.CHOICE;

            case DataConstant.T_RATING:
                return DataConstant.RATING;
        }
        return -1;
    }

    static File getSpecsRoot(){
        File r = new File(Environment.getExternalStorageDirectory(), SPECS_ROOT);
        r.mkdirs();
        return r;
    }

    private static String readFile(File f) throws IOException{
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


    private static final String SPECS_ROOT = "Warp7/specs/";

    private static final String
            ID  = "id",
            BOARD_NAME = "board",
            ALLIANCE = "alliance",
            EVENT = "event",
            TIMER = "timer",
            MATCH_SCHEDULE = "schedule",
            LAYOUT = "layout",
            CONSTANTS = "data";


    private static Specs activeSpecs = null;


    static boolean hasInstance(){
        return activeSpecs != null;
    }

    static Specs getInstance(){
        return activeSpecs;
    }

    static Specs setInstance(String filename){
        try{

           activeSpecs = new Specs(readFile(new File(getSpecsRoot(), filename)));

        } catch (IOException | JSONException e){

           e.printStackTrace();
           activeSpecs = null;
        }
        return activeSpecs;
    }

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

        event = specs_json.has(EVENT) ? specs_json.getString(EVENT) : "";
        timer = specs_json.has(TIMER) ? specs_json.getInt(TIMER) : 150;
        alliance = specs_json.has(ALLIANCE) ? specs_json.getString(ALLIANCE) : "N";

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

    boolean matchExistsInSchedule(int m, int t){
        return hasMatchSchedule() &&
                t == (m < matchSchedule.size() && m >= 0 ? matchSchedule.get(m) : -1);
    }

    String getSpecsId(){
        return specsId;
    }

    String getBoardName() {
        return boardName;
    }

    String getEvent(){
        return event.isEmpty() ? "No Event" : event;
    }

    int getTimer() {
        return timer;
    }

    DataConstant getDataConstantByIndex(int id){
        return dataConstants.get(id);
    }

    boolean hasIndexInConstants(int id){
        return id >= 0 && id < dataConstants.size();
    }

    ArrayList<Layout> getLayouts() {
        return layouts;
    }

    String getAlliance(){
        return alliance;
    }
}
