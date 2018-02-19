package ca.warp7.android.scouting;


class Static {

    static final String BOARD_PATH = "Warp7/board/board.json";

    static final String MSG_SCOUT_NAME = "ca.warp7.android.scouting.msg.scout_name";
    static final String MSG_TEAM_NUMBER = "ca.warp7.android.scouting.msg.team_number";
    static final String MSG_MATCH_NUMBER = "ca.warp7.android.scouting.msg.match_number";

    static final String MSG_PRINT_DATA = "ca.warp7.android.scouting.msg.print_data";
    static final String MSG_ENCODE_DATA = "ca.warp7.android.scouting.msg.encode_data";

    static final String SAVE_SCOUT_NAME = "ca.warp7.android.scouting.save.scout_name";
    static final String ROOT_DOMAIN = "ca.warp7.android.scouting";
    
    static final int AUTO_ROBOT_CROSS_LINE = 0;
    static final int AUTO_ROBOT_CROSS_TIME = 1;
    static final int AUTO_SCALE_ATTEMPT = 2;
    static final int AUTO_SCALE_SUCCESS = 3;
    static final int AUTO_SWITCH_ATTEMPT = 4;
    static final int AUTO_SWITCH_SUCCESS = 5;
    static final int AUTO_EXCHANGE_ATTEMPT = 6;
    static final int AUTO_EXCHANGE_SUCCESS = 7;

    static final int TELE_INTAKE = 8;
    static final int TELE_DEFENSE_START = 9;
    static final int TELE_DEFENSE_END = 10;
    static final int TELE_EXCHANGE = 11;
    static final int TELE_ALLIANCE_SWITCH = 12;
    static final int TELE_OPPONENT_SWITCH = 13;
    static final int TELE_SCALE = 14;

    static final int END_RAMP = 15;
    static final int END_CLIMB = 16;
    static final int END_ATTACHMENT = 17;
    static final int END_CLIMB_SPEED = 18;
    static final int END_INTAKE_SPEED = 19;
    static final int END_INTAKE_CONSISTENCY = 20;
    static final int END_EXCHANGE = 21;
    static final int END_SWITCH = 22;
    static final int END_SCALE = 23;

    static final int MATCH_LENGTH = 150;
 

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
        return Static.formatRightByLength(Integer.toHexString(n), digits, "0");
    }
}
