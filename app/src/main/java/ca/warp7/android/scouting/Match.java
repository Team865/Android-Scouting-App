package ca.warp7.android.scouting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created on 2018-01-24.
 * The Data model for the app
 */

public class Match {

    class MatchData {
        int typeIndex;
        int value;

        MatchData(int typeIndex, int value) {
            this.typeIndex = typeIndex;
            this.value = value;
        }

        int getIndex() {
            return typeIndex;
        }

        int getValue() {
            return value;
        }

        int getData() {
            return typeIndex * 256 + value;
        }
    }


    private int timestamp = 0;
    private int id;
    private int match = 0;
    private int team = 0;
    private int critid = 0;
    private String name = "";
    private String alliance = "";
    private String comments = "";
    private ArrayList<MatchData> data = new ArrayList<MatchData>();


    private String formatHex(int value, int digits) {
        String s = Integer.toHexString(value);
        if (digits > s.length()){
            s = new String(new char[digits - s.length()]).replace("\0", "0") + s;
        }
        return s;
    }

    private int parseHex32(String h) {
        return Integer.parseInt(h.substring(0, 4), 16) * 65536 + Integer.parseInt(h.substring(4, 8), 16);
    }

    public Match(int match, int team, String alliance, String name){
        this.match = match;
        this.team = team;
        this.alliance = alliance;
        this.name = name;
        this.id = new Random().nextInt();
    }

    public Match(String d){
        String[] sections = d.split("_");
        this.match = Integer.parseInt(sections[1]);
        this.team = Integer.parseInt(sections[2]);
        this.alliance = sections[3];
        this.name = sections[4];

        this.id = parseHex32(sections[5].substring(0, 8));
        this.critid = parseHex32(sections[5].substring(8, 16));
        this.timestamp = parseHex32(sections[5].substring(16, 24));

        for (int i = 0; i < sections[6].length() / 4; i++) {
            String ivs = sections[6].substring(i * 4, i * 4 + 4);
            addData(Integer.parseInt(ivs.substring(0,2), 16), Integer.parseInt(ivs.substring(2,4), 16));
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < sections[7].length(); j += 2) {
            String str = sections[7].substring(j, j + 2);
            sb.append((char) Integer.parseInt(str, 16));
        }
        comments = sb.toString();
    }


    // Prints out the match scouting data for debug and preview

    public String toFormattedString() {

        StringBuilder sb = new StringBuilder();

        sb.append("id:\t\t0x").append(Integer.toHexString(id));
        sb.append("\ncriteria id:\t0x").append(Integer.toHexString(critid));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        sb.append("\nstart time:\t").append(sdf.format(new Date(timestamp * 1000L)));
        sb.append("\nmatch number:\t").append(match);
        sb.append("\nteam number:\t").append(team);
        sb.append("\nscouter:\t").append(name);
        sb.append("\nalliance:\t").append(alliance);
        sb.append("\ndata:\n");


        for (MatchData i : data) {
            sb.append("\n\ttype:\t").append(i.getIndex()).append("\t\t\tvalue:\t").append(i.getValue());
        }

        sb.append("\n\n\ncomments:\t").append(comments);

        return sb.toString();

    }


    private String toHeadDataString() {
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));

        sb.append(sdf.format(new Date(timestamp * 1000L))).append("_");
        sb.append(match).append("_");
        sb.append(team).append("_");
        sb.append(alliance).append("_");
        sb.append(name);

        return sb.toString();
    }



    // Encodes the match data into hex code
    public String toHexEncodeString() {

        StringBuilder sb = new StringBuilder();
        sb.append(toHeadDataString()).append("_");

        sb.append(formatHex(id, 8)).append(formatHex(critid, 8));
        sb.append(formatHex(timestamp, 8)).append("_");

        for (MatchData i : data) {
            sb.append(formatHex(i.getData(), 4));
        }

        sb.append("_");

        // Add the encoded comments
        for (char ch : comments.toCharArray()) {
            sb.append(Integer.toHexString((int) ch));
        }

        return sb.toString();
    }


    public void addComment(String comments) {
        this.comments = comments;
    }


    public void startMatch() {
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }

    public void addData(int typeIndex, int value) {
        data.add(new MatchData(typeIndex, value));
    }

}
