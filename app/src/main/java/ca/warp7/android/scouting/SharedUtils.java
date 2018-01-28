package ca.warp7.android.scouting;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created on 2018-01-27.
 */

class SharedUtils {

    private static final String BOARD_PATH = "Warp7/board/board.json";

    public static File getBoardFile() {
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
