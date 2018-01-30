package ca.warp7.android.scouting;


import android.os.Environment;
import android.util.Log;

import java.io.File;


class SharedUtils {

    private static final String BOARD_PATH = "Warp7/board/board.json";
    static final String MSG_SCOUT_NAME = "ca.warp7.android.scouting.msg.scout_name";
    static final String MSG_BOARD_ID = "ca.warp7.android.scouting.msg.board_id";
    static final String MSG_TEAM_NUMBER = "ca.warp7.android.scouting.msg.team_number";
    static final String MSG_MATCH_NUMBER = "ca.warp7.android.scouting.msg.match_number";
    static final String SAVE_SCOUT_NAME = "ca.warp7.android.scouting.save.scout_name";

    static final String ROOT_DOMAIN = "ca.warp7.android.scouting";

    static File getBoardFile() {
        // TODO Add to ensure file permissions, right now it must be explicitly allowed in Settings
        // Maybe have to in another class?
        File root = Environment.getExternalStorageDirectory();
        File f = new File(root, BOARD_PATH);

        if (!f.exists()){
            if(!f.mkdirs()){
                Log.e("io", "Directory not created");
            }
        }
        return f;
    }
}
