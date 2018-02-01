package ca.warp7.android.scouting;


import android.os.Environment;
import android.util.Log;

import java.io.File;


class Shared {

    static final String BOARD_PATH = "Warp7/board/board.json";

    static final String MSG_SCOUT_NAME = "ca.warp7.android.scouting.msg.scout_name";
    static final String MSG_TEAM_NUMBER = "ca.warp7.android.scouting.msg.team_number";
    static final String MSG_MATCH_NUMBER = "ca.warp7.android.scouting.msg.match_number";

    static final String MSG_PRINT_DATA = "ca.warp7.android.scouting.msg.print_data";

    static final String SAVE_SCOUT_NAME = "ca.warp7.android.scouting.save.scout_name";

    static final String ROOT_DOMAIN = "ca.warp7.android.scouting";

    static String formatRightByLength(String s, int digits, String replacer){
        String r = s;
        if (digits > r.length())
            r = new String(new char[digits - r.length()]).replace("\0", replacer) + r;
        return r;
    }

    static String formatLeftByLength(String s, int digits, String replacer){
        String r = s;
        if (digits > r.length())
            r += new String(new char[digits - r.length()]).replace("\0", replacer);
        return r;
    }

    static String formatHex(int n, int digits){
        return Shared.formatRightByLength(Integer.toHexString(n), digits, "0");
    }

    static int parseHex32(String h) {
        return Integer.parseInt(h.substring(0, 4), 16) * 65536 + Integer.parseInt(h.substring(4, 8), 16);
    }
}
