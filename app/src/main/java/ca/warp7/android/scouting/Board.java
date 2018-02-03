package ca.warp7.android.scouting;


import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


class Board {


    private int boardId;
    private String boardName;
    private int[] boardMatches;

    int getBoardId() {
        return boardId;
    }

    String getBoardName() {
        return boardName;
    }

    boolean matchDoesExist(int m, int t) {

        return boardMatches != null &&
                t == (m < boardMatches.length && m >= 0 ? boardMatches[m] : -1);
    }

    Board() {

        String json;

        try {

            // TODO Add to ensure file permissions, right now it must be explicitly allowed in Settings
            // Maybe have to in another class?
            File root = Environment.getExternalStorageDirectory();
            File filePath = new File(root, Static.BOARD_PATH);

            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            json = sb.toString();

            JSONObject jobj = new JSONObject(json);

            String idString = jobj.getString("board_id");
            boardId = Integer.parseInt(idString.substring(0, 4), 16) * 65536;
            boardId += Integer.parseInt(idString.substring(4, 8), 16);

            boardName = jobj.getString("board_name");
            JSONArray jarr = jobj.getJSONArray("board_matches");
            boardMatches = new int[jarr.length()];
            for(int i = 0; i < jarr.length(); i++) {
                boardMatches[i] = jarr.getInt(i);
            }


        } catch(Exception e) {

            boardId = 0;
            boardName = "Unspecified";

            e.printStackTrace();
        }

    }
}
