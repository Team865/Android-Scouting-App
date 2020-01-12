// The Blue Alliance API Version 3.5 

@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference")

package ca.warp7.android.scouting.tba

import org.json.JSONObject


fun JSONObject.toAPIStatus() = APIStatus(
    this,
    int("current_season"),
    int("max_season"),
    boolean("is_datafeed_down"),
    stringList("down_events"),
    obj("ios")?.toAPIStatusAppVersion(),
    obj("android")?.toAPIStatusAppVersion()
)

fun JSONObject.toAPIStatusAppVersion() = APIStatusAppVersion(
    this,
    int("min_app_version"),
    int("latest_app_version")
)

fun JSONObject.toTeamSimple() = TeamSimple(
    this,
    string("key"),
    int("team_number"),
    string("nickname"),
    string("name"),
    string("city"),
    string("state_prov"),
    string("country")
)

fun JSONObject.toTeam() = Team(
    this,
    string("key"),
    int("team_number"),
    string("nickname"),
    string("name"),
    string("city"),
    string("state_prov"),
    string("country"),
    string("address"),
    string("postal_code"),
    string("gmaps_place_id"),
    string("gmaps_url"),
    double("lat"),
    double("lng"),
    string("location_name"),
    string("website"),
    int("rookie_year"),
    string("motto"),
    obj("home_championship")
)

fun JSONObject.toTeamRobot() = TeamRobot(
    this,
    int("year"),
    string("robot_name"),
    string("key"),
    string("team_key")
)

fun JSONObject.toEventSimple() = EventSimple(
    this,
    string("key"),
    string("name"),
    string("event_code"),
    int("event_type"),
    obj("district")?.toDistrictList(),
    string("city"),
    string("state_prov"),
    string("country"),
    string("start_date"),
    string("end_date"),
    int("year")
)

fun JSONObject.toEvent() = Event(
    this,
    string("key"),
    string("name"),
    string("event_code"),
    int("event_type"),
    obj("district")?.toDistrictList(),
    string("city"),
    string("state_prov"),
    string("country"),
    string("start_date"),
    string("end_date"),
    int("year"),
    string("short_name"),
    string("event_type_string"),
    int("week"),
    string("address"),
    string("postal_code"),
    string("gmaps_place_id"),
    string("gmaps_url"),
    double("lat"),
    double("lng"),
    string("location_name"),
    string("timezone"),
    string("website"),
    string("first_event_id"),
    string("first_event_code"),
    genericArray("webcasts")?.mapToList { it.toWebcast() },
    stringList("division_keys"),
    string("parent_event_key"),
    int("playoff_type"),
    string("playoff_type_string")
)

fun JSONObject.toTeamEventStatus() = TeamEventStatus(
    this,
    obj("qual")?.toTeamEventStatusRank(),
    obj("alliance")?.toTeamEventStatusAlliance(),
    obj("playoff")?.toTeamEventStatusPlayoff(),
    string("alliance_status_str"),
    string("playoff_status_str"),
    string("overall_status_str"),
    string("next_match_key"),
    string("last_match_key")
)

fun JSONObject.toTeamEventStatusRank() = TeamEventStatusRank(
    this,
    int("num_teams"),
    obj("ranking"),
    objList("sort_order_info"),
    string("status")
)

fun JSONObject.toTeamEventStatusAlliance() = TeamEventStatusAlliance(
    this,
    string("name"),
    int("number"),
    obj("backup")?.toTeamEventStatusAllianceBackup(),
    int("pick")
)

fun JSONObject.toTeamEventStatusAllianceBackup() = TeamEventStatusAllianceBackup(
    this,
    string("out"),
    string("in")
)

fun JSONObject.toTeamEventStatusPlayoff() = TeamEventStatusPlayoff(
    this,
    string("level"),
    obj("current_level_record")?.toWLTRecord(),
    obj("record")?.toWLTRecord(),
    string("status"),
    int("playoff_average")
)

fun JSONObject.toEventRanking() = EventRanking(
    this,
    objList("rankings"),
    objList("extra_stats_info"),
    objList("sort_order_info")
)

fun JSONObject.toEventDistrictPoints() = EventDistrictPoints(
    this,
    obj("points"),
    obj("tiebreakers")
)

fun JSONObject.toEventInsights() = EventInsights(
    this,
    obj("qual"),
    obj("playoff")
)

fun JSONObject.toEventInsights2016() = EventInsights2016(
    this,
    doubleList("LowBar"),
    doubleList("A_ChevalDeFrise"),
    doubleList("A_Portcullis"),
    doubleList("B_Ramparts"),
    doubleList("B_Moat"),
    doubleList("C_SallyPort"),
    doubleList("C_Drawbridge"),
    doubleList("D_RoughTerrain"),
    doubleList("D_RockWall"),
    double("average_high_goals"),
    double("average_low_goals"),
    doubleList("breaches"),
    doubleList("scales"),
    doubleList("challenges"),
    doubleList("captures"),
    double("average_win_score"),
    double("average_win_margin"),
    double("average_score"),
    double("average_auto_score"),
    double("average_crossing_score"),
    double("average_boulder_score"),
    double("average_tower_score"),
    double("average_foul_score"),
    stringList("high_score")
)

fun JSONObject.toEventInsights2017() = EventInsights2017(
    this,
    double("average_foul_score"),
    double("average_fuel_points"),
    double("average_fuel_points_auto"),
    double("average_fuel_points_teleop"),
    double("average_high_goals"),
    double("average_high_goals_auto"),
    double("average_high_goals_teleop"),
    double("average_low_goals"),
    double("average_low_goals_auto"),
    double("average_low_goals_teleop"),
    double("average_mobility_points_auto"),
    double("average_points_auto"),
    double("average_points_teleop"),
    double("average_rotor_points"),
    double("average_rotor_points_auto"),
    double("average_rotor_points_teleop"),
    double("average_score"),
    double("average_takeoff_points_teleop"),
    double("average_win_margin"),
    double("average_win_score"),
    stringList("high_kpa"),
    stringList("high_score"),
    doubleList("kpa_achieved"),
    doubleList("mobility_counts"),
    doubleList("rotor_1_engaged"),
    doubleList("rotor_1_engaged_auto"),
    doubleList("rotor_2_engaged"),
    doubleList("rotor_2_engaged_auto"),
    doubleList("rotor_3_engaged"),
    doubleList("rotor_4_engaged"),
    doubleList("takeoff_counts"),
    doubleList("unicorn_matches")
)

fun JSONObject.toEventInsights2018() = EventInsights2018(
    this,
    doubleList("auto_quest_achieved"),
    double("average_boost_played"),
    double("average_endgame_points"),
    double("average_force_played"),
    double("average_foul_score"),
    double("average_points_auto"),
    double("average_points_teleop"),
    double("average_run_points_auto"),
    double("average_scale_ownership_points"),
    double("average_scale_ownership_points_auto"),
    double("average_scale_ownership_points_teleop"),
    double("average_score"),
    double("average_switch_ownership_points"),
    double("average_switch_ownership_points_auto"),
    double("average_switch_ownership_points_teleop"),
    double("average_vault_points"),
    double("average_win_margin"),
    double("average_win_score"),
    doubleList("boost_played_counts"),
    doubleList("climb_counts"),
    doubleList("face_the_boss_achieved"),
    doubleList("force_played_counts"),
    stringList("high_score"),
    doubleList("levitate_played_counts"),
    doubleList("run_counts_auto"),
    double("scale_neutral_percentage"),
    double("scale_neutral_percentage_auto"),
    double("scale_neutral_percentage_teleop"),
    doubleList("switch_owned_counts_auto"),
    doubleList("unicorn_matches"),
    double("winning_opp_switch_denial_percentage_teleop"),
    double("winning_own_switch_ownership_percentage"),
    double("winning_own_switch_ownership_percentage_auto"),
    double("winning_own_switch_ownership_percentage_teleop"),
    double("winning_scale_ownership_percentage"),
    double("winning_scale_ownership_percentage_auto"),
    double("winning_scale_ownership_percentage_teleop")
)

fun JSONObject.toEventOPRs() = EventOPRs(
    this,
    obj("oprs"),
    obj("dprs"),
    obj("ccwms")
)

fun JSONObject.toEventPredictions() = EventPredictions(
    this
)

fun JSONObject.toMatchSimple() = MatchSimple(
    this,
    string("key"),
    string("comp_level"),
    int("set_number"),
    int("match_number"),
    Alliances(
        obj("alliances")?.obj("blue")?.toMatchAlliance(),
        obj("alliances")?.obj("red")?.toMatchAlliance()
    ),
    string("winning_alliance"),
    string("event_key"),
    int("time"),
    int("predicted_time"),
    int("actual_time")
)

fun JSONObject.toMatch() = Match(
    this,
    string("key"),
    string("comp_level"),
    int("set_number"),
    int("match_number"),
    Alliances(
        obj("alliances")?.obj("blue")?.toMatchAlliance(),
        obj("alliances")?.obj("red")?.toMatchAlliance()
    ),
    string("winning_alliance"),
    string("event_key"),
    int("time"),
    int("actual_time"),
    int("predicted_time"),
    int("post_result_time"),
    obj("score_breakdown"),
    objList("videos")
)

fun JSONObject.toMatchAlliance() = MatchAlliance(
    this,
    int("score"),
    stringList("team_keys"),
    stringList("surrogate_team_keys"),
    stringList("dq_team_keys")
)

fun JSONObject.toMatchScoreBreakdown2015() = MatchScoreBreakdown2015(
    this,
    obj("blue")?.toMatchScoreBreakdown2015Alliance(),
    obj("red")?.toMatchScoreBreakdown2015Alliance(),
    string("coopertition"),
    int("coopertition_points")
)

fun JSONObject.toMatchScoreBreakdown2015Alliance() = MatchScoreBreakdown2015Alliance(
    this,
    int("auto_points"),
    int("teleop_points"),
    int("container_points"),
    int("tote_points"),
    int("litter_points"),
    int("foul_points"),
    int("adjust_points"),
    int("total_points"),
    int("foul_count"),
    int("tote_count_far"),
    int("tote_count_near"),
    boolean("tote_set"),
    boolean("tote_stack"),
    int("container_count_level1"),
    int("container_count_level2"),
    int("container_count_level3"),
    int("container_count_level4"),
    int("container_count_level5"),
    int("container_count_level6"),
    boolean("container_set"),
    int("litter_count_container"),
    int("litter_count_landfill"),
    int("litter_count_unprocessed"),
    boolean("robot_set")
)

fun JSONObject.toMatchScoreBreakdown2016() = MatchScoreBreakdown2016(
    this,
    obj("blue")?.toMatchScoreBreakdown2016Alliance(),
    obj("red")?.toMatchScoreBreakdown2016Alliance()
)

fun JSONObject.toMatchScoreBreakdown2016Alliance() = MatchScoreBreakdown2016Alliance(
    this,
    int("autoPoints"),
    int("teleopPoints"),
    int("breachPoints"),
    int("foulPoints"),
    int("capturePoints"),
    int("adjustPoints"),
    int("totalPoints"),
    string("robot1Auto"),
    string("robot2Auto"),
    string("robot3Auto"),
    int("autoReachPoints"),
    int("autoCrossingPoints"),
    int("autoBouldersLow"),
    int("autoBouldersHigh"),
    int("autoBoulderPoints"),
    int("teleopCrossingPoints"),
    int("teleopBouldersLow"),
    int("teleopBouldersHigh"),
    int("teleopBoulderPoints"),
    boolean("teleopDefensesBreached"),
    int("teleopChallengePoints"),
    int("teleopScalePoints"),
    int("teleopTowerCaptured"),
    string("towerFaceA"),
    string("towerFaceB"),
    string("towerFaceC"),
    int("towerEndStrength"),
    int("techFoulCount"),
    int("foulCount"),
    string("position2"),
    string("position3"),
    string("position4"),
    string("position5"),
    int("position1crossings"),
    int("position2crossings"),
    int("position3crossings"),
    int("position4crossings"),
    int("position5crossings")
)

fun JSONObject.toMatchScoreBreakdown2017() = MatchScoreBreakdown2017(
    this,
    obj("blue")?.toMatchScoreBreakdown2017Alliance(),
    obj("red")?.toMatchScoreBreakdown2017Alliance()
)

fun JSONObject.toMatchScoreBreakdown2017Alliance() = MatchScoreBreakdown2017Alliance(
    this,
    int("autoPoints"),
    int("teleopPoints"),
    int("foulPoints"),
    int("adjustPoints"),
    int("totalPoints"),
    string("robot1Auto"),
    string("robot2Auto"),
    string("robot3Auto"),
    boolean("rotor1Auto"),
    boolean("rotor2Auto"),
    int("autoFuelLow"),
    int("autoFuelHigh"),
    int("autoMobilityPoints"),
    int("autoRotorPoints"),
    int("autoFuelPoints"),
    int("teleopFuelPoints"),
    int("teleopFuelLow"),
    int("teleopFuelHigh"),
    int("teleopRotorPoints"),
    boolean("kPaRankingPointAchieved"),
    int("teleopTakeoffPoints"),
    int("kPaBonusPoints"),
    int("rotorBonusPoints"),
    boolean("rotor1Engaged"),
    boolean("rotor2Engaged"),
    boolean("rotor3Engaged"),
    boolean("rotor4Engaged"),
    boolean("rotorRankingPointAchieved"),
    int("techFoulCount"),
    int("foulCount"),
    string("touchpadNear"),
    string("touchpadMiddle"),
    string("touchpadFar")
)

fun JSONObject.toMatchScoreBreakdown2018() = MatchScoreBreakdown2018(
    this,
    obj("blue")?.toMatchScoreBreakdown2018Alliance(),
    obj("red")?.toMatchScoreBreakdown2018Alliance()
)

fun JSONObject.toMatchScoreBreakdown2018Alliance() = MatchScoreBreakdown2018Alliance(
    this,
    int("adjustPoints"),
    int("autoOwnershipPoints"),
    int("autoPoints"),
    boolean("autoQuestRankingPoint"),
    string("autoRobot1"),
    string("autoRobot2"),
    string("autoRobot3"),
    int("autoRunPoints"),
    int("autoScaleOwnershipSec"),
    boolean("autoSwitchAtZero"),
    int("autoSwitchOwnershipSec"),
    int("endgamePoints"),
    string("endgameRobot1"),
    string("endgameRobot2"),
    string("endgameRobot3"),
    boolean("faceTheBossRankingPoint"),
    int("foulCount"),
    int("foulPoints"),
    int("rp"),
    int("techFoulCount"),
    int("teleopOwnershipPoints"),
    int("teleopPoints"),
    int("teleopScaleBoostSec"),
    int("teleopScaleForceSec"),
    int("teleopScaleOwnershipSec"),
    int("teleopSwitchBoostSec"),
    int("teleopSwitchForceSec"),
    int("teleopSwitchOwnershipSec"),
    int("totalPoints"),
    int("vaultBoostPlayed"),
    int("vaultBoostTotal"),
    int("vaultForcePlayed"),
    int("vaultForceTotal"),
    int("vaultLevitatePlayed"),
    int("vaultLevitateTotal"),
    int("vaultPoints"),
    string("tba_gameData")
)

fun JSONObject.toMatchTimeseries2018() = MatchTimeseries2018(
    this,
    string("event_key"),
    string("match_id"),
    string("mode"),
    int("play"),
    int("time_remaining"),
    int("blue_auto_quest"),
    int("blue_boost_count"),
    int("blue_boost_played"),
    string("blue_current_powerup"),
    int("blue_face_the_boss"),
    int("blue_force_count"),
    int("blue_force_played"),
    int("blue_levitate_count"),
    int("blue_levitate_played"),
    string("blue_powerup_time_remaining"),
    int("blue_scale_owned"),
    int("blue_score"),
    int("blue_switch_owned"),
    int("red_auto_quest"),
    int("red_boost_count"),
    int("red_boost_played"),
    string("red_current_powerup"),
    int("red_face_the_boss"),
    int("red_force_count"),
    int("red_force_played"),
    int("red_levitate_count"),
    int("red_levitate_played"),
    string("red_powerup_time_remaining"),
    int("red_scale_owned"),
    int("red_score"),
    int("red_switch_owned")
)

fun JSONObject.toMatchScoreBreakdown2019() = MatchScoreBreakdown2019(
    this,
    obj("blue")?.toMatchScoreBreakdown2019Alliance(),
    obj("red")?.toMatchScoreBreakdown2019Alliance()
)

fun JSONObject.toMatchScoreBreakdown2019Alliance() = MatchScoreBreakdown2019Alliance(
    this,
    int("adjustPoints"),
    int("autoPoints"),
    string("bay1"),
    string("bay2"),
    string("bay3"),
    string("bay4"),
    string("bay5"),
    string("bay6"),
    string("bay7"),
    string("bay8"),
    int("cargoPoints"),
    boolean("completeRocketRankingPoint"),
    boolean("completedRocketFar"),
    boolean("completedRocketNear"),
    string("endgameRobot1"),
    string("endgameRobot2"),
    string("endgameRobot3"),
    int("foulCount"),
    int("foulPoints"),
    int("habClimbPoints"),
    boolean("habDockingRankingPoint"),
    string("habLineRobot1"),
    string("habLineRobot2"),
    string("habLineRobot3"),
    int("hatchPanelPoints"),
    string("lowLeftRocketFar"),
    string("lowLeftRocketNear"),
    string("lowRightRocketFar"),
    string("lowRightRocketNear"),
    string("midLeftRocketFar"),
    string("midLeftRocketNear"),
    string("midRightRocketFar"),
    string("midRightRocketNear"),
    string("preMatchBay1"),
    string("preMatchBay2"),
    string("preMatchBay3"),
    string("preMatchBay6"),
    string("preMatchBay7"),
    string("preMatchBay8"),
    string("preMatchLevelRobot1"),
    string("preMatchLevelRobot2"),
    string("preMatchLevelRobot3"),
    int("rp"),
    int("sandStormBonusPoints"),
    int("techFoulCount"),
    int("teleopPoints"),
    string("topLeftRocketFar"),
    string("topLeftRocketNear"),
    string("topRightRocketFar"),
    string("topRightRocketNear"),
    int("totalPoints")
)

fun JSONObject.toMedia() = Media(
    this,
    string("key"),
    string("type"),
    string("foreign_key"),
    obj("details"),
    boolean("preferred"),
    string("direct_url"),
    string("view_url")
)

fun JSONObject.toEliminationAlliance() = EliminationAlliance(
    this,
    string("name"),
    obj("backup"),
    stringList("declines"),
    stringList("picks"),
    obj("status")
)

fun JSONObject.toAward() = Award(
    this,
    string("name"),
    int("award_type"),
    string("event_key"),
    genericArray("recipient_list")?.mapToList { it.toAwardRecipient() },
    int("year")
)

fun JSONObject.toAwardRecipient() = AwardRecipient(
    this,
    string("team_key"),
    string("awardee")
)

fun JSONObject.toDistrictList() = DistrictList(
    this,
    string("abbreviation"),
    string("display_name"),
    string("key"),
    int("year")
)

fun JSONObject.toDistrictRanking() = DistrictRanking(
    this,
    string("team_key"),
    int("rank"),
    int("rookie_bonus"),
    int("point_total"),
    objList("event_points")
)

fun JSONObject.toWLTRecord() = WLTRecord(
    this,
    int("losses"),
    int("wins"),
    int("ties")
)

fun JSONObject.toWebcast() = Webcast(
    this,
    string("type"),
    string("channel"),
    string("file")
)
