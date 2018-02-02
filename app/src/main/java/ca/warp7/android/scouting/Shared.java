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
    static final String MSG_ENCODE_DATA = "ca.warp7.android.scouting.msg.encode_data";

    static final String SAVE_SCOUT_NAME = "ca.warp7.android.scouting.save.scout_name";
    static final String ROOT_DOMAIN = "ca.warp7.android.scouting";
    
    static final int AUTO_ROBO_CROSS_LINE = 0;
    static final int AUTO_SCALE_ATTEMPT = 1;
    static final int AUTO_SCALE_SUCCESS = 2;
    static final int AUTO_SWITCH_ATTEMPT = 3;
    static final int AUTO_SWITCH_SUCCESS = 4;
    static final int AUTO_EXCHANGE_ATTEMPT = 5;
    static final int AUTO_EXCHANGE_SUCCESS = 6;

    static final int TELE_INTAKE = 7;
    static final int TELE_DEFENSE_START = 8;
    static final int TELE_DEFENSE_END = 9;
    static final int TELE_EXCHANGE = 10;
    static final int TELE_ALLIANCE_SWITCH = 11;
    static final int TELE_OPPONENT_SWITCH = 12;
    static final int TELE_SCALE = 13;

    static final int END_RAMP = 14;
    static final int END_CLIMB = 15;
    static final int END_ATTACHMENT = 16;
    static final int END_CLIMB_SPEED = 17;
    static final int END_INTAKE_SPEED = 18;
    static final int END_INTAKE_CONSISTENCY = 19;
    static final int END_EXCHANGE = 20;
    static final int END_SWITCH = 21;
    static final int END_SCALE = 22;

 

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
