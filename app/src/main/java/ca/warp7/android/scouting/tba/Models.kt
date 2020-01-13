// The Blue Alliance API Version 3.5 

@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference")

package ca.warp7.android.scouting.tba

import org.json.JSONObject

/**
 * Alliance Data
 */
class Alliances<T>(val blue: T, val red: T)

class APIStatus(
    val data: JSONObject,

    /** Year of the current FRC season. */
    val current_season: Int?,

    /** Maximum FRC season year for valid queries. */
    val max_season: Int?,

    /** True if the entire FMS API provided by FIRST is down. */
    val is_datafeed_down: Boolean?,

    /** An array of strings containing event keys of any active events that are no longer updating. */
    val down_events: List<String>?,

    val ios: APIStatusAppVersion?,

    val android: APIStatusAppVersion?
)

class APIStatusAppVersion(
    val data: JSONObject,

    /** Internal use - Minimum application version required to correctly connect and process data. */
    val min_app_version: Int?,

    /** Internal use - Latest application version available. */
    val latest_app_version: Int?
)

class TeamSimple(
    val data: JSONObject,

    /** TBA team key with the format `frcXXXX` with `XXXX` representing the team number. */
    val key: String?,

    /** Official team number issued by FIRST. */
    val team_number: Int?,

    /** Team nickname provided by FIRST. */
    val nickname: String?,

    /** Official long name registered with FIRST. */
    val name: String?,

    /** City of team derived from parsing the address registered with FIRST. */
    val city: String?,

    /** State of team derived from parsing the address registered with FIRST. */
    val state_prov: String?,

    /** Country of team derived from parsing the address registered with FIRST. */
    val country: String?
)

class Team(
    val data: JSONObject,

    /** TBA team key with the format `frcXXXX` with `XXXX` representing the team number. */
    val key: String?,

    /** Official team number issued by FIRST. */
    val team_number: Int?,

    /** Team nickname provided by FIRST. */
    val nickname: String?,

    /** Official long name registered with FIRST. */
    val name: String?,

    /** City of team derived from parsing the address registered with FIRST. */
    val city: String?,

    /** State of team derived from parsing the address registered with FIRST. */
    val state_prov: String?,

    /** Country of team derived from parsing the address registered with FIRST. */
    val country: String?,

    /** Will be NULL, for future development. */
    val address: String?,

    /** Postal code from the team address. */
    val postal_code: String?,

    /** Will be NULL, for future development. */
    val gmaps_place_id: String?,

    /** Will be NULL, for future development. */
    val gmaps_url: String?,

    /** Will be NULL, for future development. */
    val lat: Double?,

    /** Will be NULL, for future development. */
    val lng: Double?,

    /** Will be NULL, for future development. */
    val location_name: String?,

    /** Official website associated with the team. */
    val website: String?,

    /** First year the team officially competed. */
    val rookie_year: Int?,

    /** Team's motto as provided by FIRST. This field is deprecated and will return null - will be removed at end-of-season in 2019. */
    val motto: String?,

    /** Location of the team's home championship each year as a key-value pair. The year (as a string) is the key, and the city is the value. */
    val home_championship: JSONObject?
)

class TeamRobot(
    val data: JSONObject,

    /** Year this robot competed in. */
    val year: Int?,

    /** Name of the robot as provided by the team. */
    val robot_name: String?,

    /** Internal TBA identifier for this robot. */
    val key: String?,

    /** TBA team key for this robot. */
    val team_key: String?
)

class EventSimple(
    val data: JSONObject,

    /** TBA event key with the format yyyy[EVENT_CODE], where yyyy is the year, and EVENT_CODE is the event code of the event. */
    val key: String?,

    /** Official name of event on record either provided by FIRST or organizers of offseason event. */
    val name: String?,

    /** Event short code, as provided by FIRST. */
    val event_code: String?,

    /** Event Type, as defined here: https://github.com/the-blue-alliance/the-blue-alliance/blob/master/consts/event_type.py#L2 */
    val event_type: Int?,

    val district: DistrictList?,

    /** City, town, village, etc. the event is located in. */
    val city: String?,

    /** State or Province the event is located in. */
    val state_prov: String?,

    /** Country the event is located in. */
    val country: String?,

    /** Event start date in `yyyy-mm-dd` format. */
    val start_date: String?,

    /** Event end date in `yyyy-mm-dd` format. */
    val end_date: String?,

    /** Year the event data is for. */
    val year: Int?
)

class Event(
    val data: JSONObject,

    /** TBA event key with the format yyyy[EVENT_CODE], where yyyy is the year, and EVENT_CODE is the event code of the event. */
    val key: String?,

    /** Official name of event on record either provided by FIRST or organizers of offseason event. */
    val name: String?,

    /** Event short code, as provided by FIRST. */
    val event_code: String?,

    /** Event Type, as defined here: https://github.com/the-blue-alliance/the-blue-alliance/blob/master/consts/event_type.py#L2 */
    val event_type: Int?,

    val district: DistrictList?,

    /** City, town, village, etc. the event is located in. */
    val city: String?,

    /** State or Province the event is located in. */
    val state_prov: String?,

    /** Country the event is located in. */
    val country: String?,

    /** Event start date in `yyyy-mm-dd` format. */
    val start_date: String?,

    /** Event end date in `yyyy-mm-dd` format. */
    val end_date: String?,

    /** Year the event data is for. */
    val year: Int?,

    /** Same as `name` but doesn't include event specifiers, such as 'Regional' or 'District'. May be null. */
    val short_name: String?,

    /** Event Type, eg Regional, District, or Offseason. */
    val event_type_string: String?,

    /** Week of the event relative to the first official season event, zero-indexed. Only valid for Regionals, Districts, and District Championships. Null otherwise. (Eg. A season with a week 0 'preseason' event does not count, and week 1 events will show 0 here. Seasons with a week 0.5 regional event will show week 0 for those event(s) and week 1 for week 1 events and so on.) */
    val week: Int?,

    /** Address of the event's venue, if available. */
    val address: String?,

    /** Postal code from the event address. */
    val postal_code: String?,

    /** Google Maps Place ID for the event address. */
    val gmaps_place_id: String?,

    /** Link to address location on Google Maps. */
    val gmaps_url: String?,

    /** Latitude for the event address. */
    val lat: Double?,

    /** Longitude for the event address. */
    val lng: Double?,

    /** Name of the location at the address for the event, eg. Blue Alliance High School. */
    val location_name: String?,

    /** Timezone name. */
    val timezone: String?,

    /** The event's website, if any. */
    val website: String?,

    /** The FIRST internal Event ID, used to link to the event on the FRC webpage. */
    val first_event_id: String?,

    /** Public facing event code used by FIRST (on frc-events.firstinspires.org, for example) */
    val first_event_code: String?,

    val webcasts: List<Webcast>?,

    /** An array of event keys for the divisions at this event. */
    val division_keys: List<String>?,

    /** The TBA Event key that represents the event's parent. Used to link back to the event from a division event. It is also the inverse relation of `divison_keys`. */
    val parent_event_key: String?,

    /** Playoff Type, as defined here: https://github.com/the-blue-alliance/the-blue-alliance/blob/master/consts/playoff_type.py#L4, or null. */
    val playoff_type: Int?,

    /** String representation of the `playoff_type`, or null. */
    val playoff_type_string: String?
)

class TeamEventStatus(
    val data: JSONObject,

    val qual: TeamEventStatusRank?,

    val alliance: TeamEventStatusAlliance?,

    val playoff: TeamEventStatusPlayoff?,

    /** An HTML formatted string suitable for display to the user containing the team's alliance pick status. */
    val alliance_status_str: String?,

    /** An HTML formatter string suitable for display to the user containing the team's playoff status. */
    val playoff_status_str: String?,

    /** An HTML formatted string suitable for display to the user containing the team's overall status summary of the event. */
    val overall_status_str: String?,

    /** TBA match key for the next match the team is scheduled to play in at this event, or null. */
    val next_match_key: String?,

    /** TBA match key for the last match the team played in at this event, or null. */
    val last_match_key: String?
)

class TeamEventStatusRank(
    val data: JSONObject,

    /** Number of teams ranked. */
    val num_teams: Int?,

    val ranking: JSONObject?,

    /** Ordered list of names corresponding to the elements of the `sort_orders` array. */
    val sort_order_info: List<JSONObject>?,

    val status: String?
)

class TeamEventStatusAlliance(
    val data: JSONObject,

    /** Alliance name, may be null. */
    val name: String?,

    /** Alliance number. */
    val number: Int?,

    val backup: TeamEventStatusAllianceBackup?,

    /** Order the team was picked in the alliance from 0-2, with 0 being alliance captain. */
    val pick: Int?
)

/** Backup status, may be null. */
class TeamEventStatusAllianceBackup(
    val data: JSONObject,

    /** TBA key for the team replaced by the backup. */
    val out: String?,

    /** TBA key for the backup team called in. */
    val _in: String?
)

/** Playoff status for this team, may be null if the team did not make playoffs, or playoffs have not begun. */
class TeamEventStatusPlayoff(
    val data: JSONObject,

    /** The highest playoff level the team reached. */
    val level: String?,

    val current_level_record: WLTRecord?,

    val record: WLTRecord?,

    /** Current competition status for the playoffs. */
    val status: String?,

    /** The average match score during playoffs. Year specific. May be null if not relevant for a given year. */
    val playoff_average: Int?
)

class EventRanking(
    val data: JSONObject,

    /** List of rankings at the event. */
    val rankings: List<JSONObject>?,

    /** List of special TBA-generated values provided in the `extra_stats` array for each item. */
    val extra_stats_info: List<JSONObject>?,

    /** List of year-specific values provided in the `sort_orders` array for each team. */
    val sort_order_info: List<JSONObject>?
)

class EventDistrictPoints(
    val data: JSONObject,

    /** Points gained for each team at the event. Stored as a key-value pair with the team key as the key, and an object describing the points as its value. */
    val points: JSONObject?,

    /** Tiebreaker values for each team at the event. Stored as a key-value pair with the team key as the key, and an object describing the tiebreaker elements as its value. */
    val tiebreakers: JSONObject?
)

/** A year-specific event insight object expressed as a JSON string, separated in to `qual` and `playoff` fields. See also Event_Insights_2016, Event_Insights_2017, etc. */
class EventInsights(
    val data: JSONObject,

    /** Inights for the qualification round of an event */
    val qual: JSONObject?,

    /** Insights for the playoff round of an event */
    val playoff: JSONObject?
)

/** Insights for FIRST Stronghold qualification and elimination matches. */
class EventInsights2016(
    val data: JSONObject,

    /** For the Low Bar - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val LowBar: List<Double>?,

    /** For the Cheval De Frise - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val A_ChevalDeFrise: List<Double>?,

    /** For the Portcullis - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val A_Portcullis: List<Double>?,

    /** For the Ramparts - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val B_Ramparts: List<Double>?,

    /** For the Moat - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val B_Moat: List<Double>?,

    /** For the Sally Port - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val C_SallyPort: List<Double>?,

    /** For the Drawbridge - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val C_Drawbridge: List<Double>?,

    /** For the Rough Terrain - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val D_RoughTerrain: List<Double>?,

    /** For the Rock Wall - An array with three values, number of times damaged, number of opportunities to damage, and percentage. */
    val D_RockWall: List<Double>?,

    /** Average number of high goals scored. */
    val average_high_goals: Double?,

    /** Average number of low goals scored. */
    val average_low_goals: Double?,

    /** An array with three values, number of times breached, number of opportunities to breach, and percentage. */
    val breaches: List<Double>?,

    /** An array with three values, number of times scaled, number of opportunities to scale, and percentage. */
    val scales: List<Double>?,

    /** An array with three values, number of times challenged, number of opportunities to challenge, and percentage. */
    val challenges: List<Double>?,

    /** An array with three values, number of times captured, number of opportunities to capture, and percentage. */
    val captures: List<Double>?,

    /** Average winning score. */
    val average_win_score: Double?,

    /** Average margin of victory. */
    val average_win_margin: Double?,

    /** Average total score. */
    val average_score: Double?,

    /** Average autonomous score. */
    val average_auto_score: Double?,

    /** Average crossing score. */
    val average_crossing_score: Double?,

    /** Average boulder score. */
    val average_boulder_score: Double?,

    /** Average tower score. */
    val average_tower_score: Double?,

    /** Average foul score. */
    val average_foul_score: Double?,

    /** An array with three values, high score, match key from the match with the high score, and the name of the match. */
    val high_score: List<String>?
)

/** Insights for FIRST STEAMWORKS qualification and elimination matches. */
class EventInsights2017(
    val data: JSONObject,

    /** Average foul score. */
    val average_foul_score: Double?,

    /** Average fuel points scored. */
    val average_fuel_points: Double?,

    /** Average fuel points scored during auto. */
    val average_fuel_points_auto: Double?,

    /** Average fuel points scored during teleop. */
    val average_fuel_points_teleop: Double?,

    /** Average points scored in the high goal. */
    val average_high_goals: Double?,

    /** Average points scored in the high goal during auto. */
    val average_high_goals_auto: Double?,

    /** Average points scored in the high goal during teleop. */
    val average_high_goals_teleop: Double?,

    /** Average points scored in the low goal. */
    val average_low_goals: Double?,

    /** Average points scored in the low goal during auto. */
    val average_low_goals_auto: Double?,

    /** Average points scored in the low goal during teleop. */
    val average_low_goals_teleop: Double?,

    /** Average mobility points scored during auto. */
    val average_mobility_points_auto: Double?,

    /** Average points scored during auto. */
    val average_points_auto: Double?,

    /** Average points scored during teleop. */
    val average_points_teleop: Double?,

    /** Average rotor points scored. */
    val average_rotor_points: Double?,

    /** Average rotor points scored during auto. */
    val average_rotor_points_auto: Double?,

    /** Average rotor points scored during teleop. */
    val average_rotor_points_teleop: Double?,

    /** Average score. */
    val average_score: Double?,

    /** Average takeoff points scored during teleop. */
    val average_takeoff_points_teleop: Double?,

    /** Average margin of victory. */
    val average_win_margin: Double?,

    /** Average winning score. */
    val average_win_score: Double?,

    /** An array with three values, kPa scored, match key from the match with the high kPa, and the name of the match */
    val high_kpa: List<String>?,

    /** An array with three values, high score, match key from the match with the high score, and the name of the match */
    val high_score: List<String>?,

    /** An array with three values, number of times kPa bonus achieved, number of opportunities to bonus, and percentage. */
    val kpa_achieved: List<Double>?,

    /** An array with three values, number of times mobility bonus achieved, number of opportunities to bonus, and percentage. */
    val mobility_counts: List<Double>?,

    /** An array with three values, number of times rotor 1 engaged, number of opportunities to engage, and percentage. */
    val rotor_1_engaged: List<Double>?,

    /** An array with three values, number of times rotor 1 engaged in auto, number of opportunities to engage in auto, and percentage. */
    val rotor_1_engaged_auto: List<Double>?,

    /** An array with three values, number of times rotor 2 engaged, number of opportunities to engage, and percentage. */
    val rotor_2_engaged: List<Double>?,

    /** An array with three values, number of times rotor 2 engaged in auto, number of opportunities to engage in auto, and percentage. */
    val rotor_2_engaged_auto: List<Double>?,

    /** An array with three values, number of times rotor 3 engaged, number of opportunities to engage, and percentage. */
    val rotor_3_engaged: List<Double>?,

    /** An array with three values, number of times rotor 4 engaged, number of opportunities to engage, and percentage. */
    val rotor_4_engaged: List<Double>?,

    /** An array with three values, number of times takeoff was counted, number of opportunities to takeoff, and percentage. */
    val takeoff_counts: List<Double>?,

    /** An array with three values, number of times a unicorn match (Win + kPa & Rotor Bonuses) occured, number of opportunities to have a unicorn match, and percentage. */
    val unicorn_matches: List<Double>?
)

/** Insights for FIRST Power Up qualification and elimination matches. */
class EventInsights2018(
    val data: JSONObject,

    /** An array with three values, number of times auto quest was completed, number of opportunities to complete the auto quest, and percentage. */
    val auto_quest_achieved: List<Double>?,

    /** Average number of boost power up scored (out of 3). */
    val average_boost_played: Double?,

    /** Average endgame points. */
    val average_endgame_points: Double?,

    /** Average number of force power up scored (out of 3). */
    val average_force_played: Double?,

    /** Average foul score. */
    val average_foul_score: Double?,

    /** Average points scored during auto. */
    val average_points_auto: Double?,

    /** Average points scored during teleop. */
    val average_points_teleop: Double?,

    /** Average mobility points scored during auto. */
    val average_run_points_auto: Double?,

    /** Average scale ownership points scored. */
    val average_scale_ownership_points: Double?,

    /** Average scale ownership points scored during auto. */
    val average_scale_ownership_points_auto: Double?,

    /** Average scale ownership points scored during teleop. */
    val average_scale_ownership_points_teleop: Double?,

    /** Average score. */
    val average_score: Double?,

    /** Average switch ownership points scored. */
    val average_switch_ownership_points: Double?,

    /** Average switch ownership points scored during auto. */
    val average_switch_ownership_points_auto: Double?,

    /** Average switch ownership points scored during teleop. */
    val average_switch_ownership_points_teleop: Double?,

    /** Average value points scored. */
    val average_vault_points: Double?,

    /** Average margin of victory. */
    val average_win_margin: Double?,

    /** Average winning score. */
    val average_win_score: Double?,

    /** An array with three values, number of times a boost power up was played, number of opportunities to play a boost power up, and percentage. */
    val boost_played_counts: List<Double>?,

    /** An array with three values, number of times a climb occurred, number of opportunities to climb, and percentage. */
    val climb_counts: List<Double>?,

    /** An array with three values, number of times an alliance faced the boss, number of opportunities to face the boss, and percentage. */
    val face_the_boss_achieved: List<Double>?,

    /** An array with three values, number of times a force power up was played, number of opportunities to play a force power up, and percentage. */
    val force_played_counts: List<Double>?,

    /** An array with three values, high score, match key from the match with the high score, and the name of the match */
    val high_score: List<String>?,

    /** An array with three values, number of times a levitate power up was played, number of opportunities to play a levitate power up, and percentage. */
    val levitate_played_counts: List<Double>?,

    /** An array with three values, number of times a team scored mobility points in auto, number of opportunities to score mobility points in auto, and percentage. */
    val run_counts_auto: List<Double>?,

    /** Average scale neutral percentage. */
    val scale_neutral_percentage: Double?,

    /** Average scale neutral percentage during auto. */
    val scale_neutral_percentage_auto: Double?,

    /** Average scale neutral percentage during teleop. */
    val scale_neutral_percentage_teleop: Double?,

    /** An array with three values, number of times a switch was owned during auto, number of opportunities to own a switch during auto, and percentage. */
    val switch_owned_counts_auto: List<Double>?,

    /** An array with three values, number of times a unicorn match (Win + Auto Quest + Face the Boss) occurred, number of opportunities to have a unicorn match, and percentage. */
    val unicorn_matches: List<Double>?,

    /** Average opposing switch denail percentage for the winning alliance during teleop. */
    val winning_opp_switch_denial_percentage_teleop: Double?,

    /** Average own switch ownership percentage for the winning alliance. */
    val winning_own_switch_ownership_percentage: Double?,

    /** Average own switch ownership percentage for the winning alliance during auto. */
    val winning_own_switch_ownership_percentage_auto: Double?,

    /** Average own switch ownership percentage for the winning alliance during teleop. */
    val winning_own_switch_ownership_percentage_teleop: Double?,

    /** Average scale ownership percentage for the winning alliance. */
    val winning_scale_ownership_percentage: Double?,

    /** Average scale ownership percentage for the winning alliance during auto. */
    val winning_scale_ownership_percentage_auto: Double?,

    /** Average scale ownership percentage for the winning alliance during teleop. */
    val winning_scale_ownership_percentage_teleop: Double?
)

/** OPR, DPR, and CCWM for teams at the event. */
class EventOPRs(
    val data: JSONObject,

    /** A key-value pair with team key (eg `frc254`) as key and OPR as value. */
    val oprs: JSONObject?,

    /** A key-value pair with team key (eg `frc254`) as key and DPR as value. */
    val dprs: JSONObject?,

    /** A key-value pair with team key (eg `frc254`) as key and CCWM as value. */
    val ccwms: JSONObject?
)

/** JSON Object containing prediction information for the event. Contains year-specific information and is subject to change. */
class EventPredictions(
    val data: JSONObject
)

class MatchSimple(
    val data: JSONObject,

    /** TBA match key with the format `yyyy[EVENT_CODE]_[COMP_LEVEL]m[MATCH_NUMBER]`, where `yyyy` is the year, and `EVENT_CODE` is the event code of the event, `COMP_LEVEL` is (qm, ef, qf, sf, f), and `MATCH_NUMBER` is the match number in the competition level. A set number may append the competition level if more than one match in required per set. */
    val key: String?,

    /** The competition level the match was played at. */
    val comp_level: String?,

    /** The set number in a series of matches where more than one match is required in the match series. */
    val set_number: Int?,

    /** The match number of the match in the competition level. */
    val match_number: Int?,

    /** A list of alliances, the teams on the alliances, and their score. */
    val alliances: Alliances<MatchAlliance?>?,

    /** The color (red/blue) of the winning alliance. Will contain an empty string in the event of no winner, or a tie. */
    val winning_alliance: String?,

    /** Event key of the event the match was played at. */
    val event_key: String?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) of the scheduled match time, as taken from the published schedule. */
    val time: Int?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) of the TBA predicted match start time. */
    val predicted_time: Int?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) of actual match start time. */
    val actual_time: Int?
)

class Match(
    val data: JSONObject,

    /** TBA match key with the format `yyyy[EVENT_CODE]_[COMP_LEVEL]m[MATCH_NUMBER]`, where `yyyy` is the year, and `EVENT_CODE` is the event code of the event, `COMP_LEVEL` is (qm, ef, qf, sf, f), and `MATCH_NUMBER` is the match number in the competition level. A set number may be appended to the competition level if more than one match in required per set. */
    val key: String?,

    /** The competition level the match was played at. */
    val comp_level: String?,

    /** The set number in a series of matches where more than one match is required in the match series. */
    val set_number: Int?,

    /** The match number of the match in the competition level. */
    val match_number: Int?,

    /** A list of alliances, the teams on the alliances, and their score. */
    val alliances: Alliances<MatchAlliance?>?,

    /** The color (red/blue) of the winning alliance. Will contain an empty string in the event of no winner, or a tie. */
    val winning_alliance: String?,

    /** Event key of the event the match was played at. */
    val event_key: String?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) of the scheduled match time, as taken from the published schedule. */
    val time: Int?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) of actual match start time. */
    val actual_time: Int?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) of the TBA predicted match start time. */
    val predicted_time: Int?,

    /** UNIX timestamp (seconds since 1-Jan-1970 00:00:00) when the match result was posted. */
    val post_result_time: Int?,

    /** Score breakdown for auto, teleop, etc. points. Varies from year to year. May be null. */
    val score_breakdown: JSONObject?,

    /** Array of video objects associated with this match. */
    val videos: List<JSONObject>?
)

class MatchAlliance(
    val data: JSONObject,

    /** Score for this alliance. Will be null or -1 for an unplayed match. */
    val score: Int?,

    val team_keys: List<String>?,

    /** TBA team keys (eg `frc254`) of any teams playing as a surrogate. */
    val surrogate_team_keys: List<String>?,

    /** TBA team keys (eg `frc254`) of any disqualified teams. */
    val dq_team_keys: List<String>?
)

/** See the 2015 FMS API documentation for a description of each value */
class MatchScoreBreakdown2015(
    val data: JSONObject,

    val blue: MatchScoreBreakdown2015Alliance?,

    val red: MatchScoreBreakdown2015Alliance?,

    val coopertition: String?,

    val coopertition_points: Int?
)

class MatchScoreBreakdown2015Alliance(
    val data: JSONObject,

    val auto_points: Int?,

    val teleop_points: Int?,

    val container_points: Int?,

    val tote_points: Int?,

    val litter_points: Int?,

    val foul_points: Int?,

    val adjust_points: Int?,

    val total_points: Int?,

    val foul_count: Int?,

    val tote_count_far: Int?,

    val tote_count_near: Int?,

    val tote_set: Boolean?,

    val tote_stack: Boolean?,

    val container_count_level1: Int?,

    val container_count_level2: Int?,

    val container_count_level3: Int?,

    val container_count_level4: Int?,

    val container_count_level5: Int?,

    val container_count_level6: Int?,

    val container_set: Boolean?,

    val litter_count_container: Int?,

    val litter_count_landfill: Int?,

    val litter_count_unprocessed: Int?,

    val robot_set: Boolean?
)

/** See the 2016 FMS API documentation for a description of each value. */
class MatchScoreBreakdown2016(
    val data: JSONObject,

    val blue: MatchScoreBreakdown2016Alliance?,

    val red: MatchScoreBreakdown2016Alliance?
)

class MatchScoreBreakdown2016Alliance(
    val data: JSONObject,

    val autoPoints: Int?,

    val teleopPoints: Int?,

    val breachPoints: Int?,

    val foulPoints: Int?,

    val capturePoints: Int?,

    val adjustPoints: Int?,

    val totalPoints: Int?,

    val robot1Auto: String?,

    val robot2Auto: String?,

    val robot3Auto: String?,

    val autoReachPoints: Int?,

    val autoCrossingPoints: Int?,

    val autoBouldersLow: Int?,

    val autoBouldersHigh: Int?,

    val autoBoulderPoints: Int?,

    val teleopCrossingPoints: Int?,

    val teleopBouldersLow: Int?,

    val teleopBouldersHigh: Int?,

    val teleopBoulderPoints: Int?,

    val teleopDefensesBreached: Boolean?,

    val teleopChallengePoints: Int?,

    val teleopScalePoints: Int?,

    val teleopTowerCaptured: Int?,

    val towerFaceA: String?,

    val towerFaceB: String?,

    val towerFaceC: String?,

    val towerEndStrength: Int?,

    val techFoulCount: Int?,

    val foulCount: Int?,

    val position2: String?,

    val position3: String?,

    val position4: String?,

    val position5: String?,

    val position1crossings: Int?,

    val position2crossings: Int?,

    val position3crossings: Int?,

    val position4crossings: Int?,

    val position5crossings: Int?
)

/** See the 2017 FMS API documentation for a description of each value. */
class MatchScoreBreakdown2017(
    val data: JSONObject,

    val blue: MatchScoreBreakdown2017Alliance?,

    val red: MatchScoreBreakdown2017Alliance?
)

class MatchScoreBreakdown2017Alliance(
    val data: JSONObject,

    val autoPoints: Int?,

    val teleopPoints: Int?,

    val foulPoints: Int?,

    val adjustPoints: Int?,

    val totalPoints: Int?,

    val robot1Auto: String?,

    val robot2Auto: String?,

    val robot3Auto: String?,

    val rotor1Auto: Boolean?,

    val rotor2Auto: Boolean?,

    val autoFuelLow: Int?,

    val autoFuelHigh: Int?,

    val autoMobilityPoints: Int?,

    val autoRotorPoints: Int?,

    val autoFuelPoints: Int?,

    val teleopFuelPoints: Int?,

    val teleopFuelLow: Int?,

    val teleopFuelHigh: Int?,

    val teleopRotorPoints: Int?,

    val kPaRankingPointAchieved: Boolean?,

    val teleopTakeoffPoints: Int?,

    val kPaBonusPoints: Int?,

    val rotorBonusPoints: Int?,

    val rotor1Engaged: Boolean?,

    val rotor2Engaged: Boolean?,

    val rotor3Engaged: Boolean?,

    val rotor4Engaged: Boolean?,

    val rotorRankingPointAchieved: Boolean?,

    val techFoulCount: Int?,

    val foulCount: Int?,

    val touchpadNear: String?,

    val touchpadMiddle: String?,

    val touchpadFar: String?
)

/** See the 2018 FMS API documentation for a description of each value. */
class MatchScoreBreakdown2018(
    val data: JSONObject,

    val blue: MatchScoreBreakdown2018Alliance?,

    val red: MatchScoreBreakdown2018Alliance?
)

class MatchScoreBreakdown2018Alliance(
    val data: JSONObject,

    val adjustPoints: Int?,

    val autoOwnershipPoints: Int?,

    val autoPoints: Int?,

    val autoQuestRankingPoint: Boolean?,

    val autoRobot1: String?,

    val autoRobot2: String?,

    val autoRobot3: String?,

    val autoRunPoints: Int?,

    val autoScaleOwnershipSec: Int?,

    val autoSwitchAtZero: Boolean?,

    val autoSwitchOwnershipSec: Int?,

    val endgamePoints: Int?,

    val endgameRobot1: String?,

    val endgameRobot2: String?,

    val endgameRobot3: String?,

    val faceTheBossRankingPoint: Boolean?,

    val foulCount: Int?,

    val foulPoints: Int?,

    val rp: Int?,

    val techFoulCount: Int?,

    val teleopOwnershipPoints: Int?,

    val teleopPoints: Int?,

    val teleopScaleBoostSec: Int?,

    val teleopScaleForceSec: Int?,

    val teleopScaleOwnershipSec: Int?,

    val teleopSwitchBoostSec: Int?,

    val teleopSwitchForceSec: Int?,

    val teleopSwitchOwnershipSec: Int?,

    val totalPoints: Int?,

    val vaultBoostPlayed: Int?,

    val vaultBoostTotal: Int?,

    val vaultForcePlayed: Int?,

    val vaultForceTotal: Int?,

    val vaultLevitatePlayed: Int?,

    val vaultLevitateTotal: Int?,

    val vaultPoints: Int?,

    /** Unofficial TBA-computed value of the FMS provided GameData given to the alliance teams at the start of the match. 3 Character String containing `L` and `R` only. The first character represents the near switch, the 2nd the scale, and the 3rd the far, opposing, switch from the alliance's perspective. An `L` in a position indicates the platform on the left will be lit for the alliance while an `R` will indicate the right platform will be lit for the alliance. See also [WPI Screen Steps](https://wpilib.screenstepslive.com/s/currentCS/m/getting_started/l/826278-2018-game-data-details). */
    val tba_gameData: String?
)

/** Timeseries data for the 2018 game *FIRST* POWER UP.
*WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up.
*WARNING:* This model is currently under active development and may change at any time, including in breaking ways. */
class MatchTimeseries2018(
    val data: JSONObject,

    /** TBA event key with the format yyyy[EVENT_CODE], where yyyy is the year, and EVENT_CODE is the event code of the event. */
    val event_key: String?,

    /** Match ID consisting of the level, match number, and set number, eg `qm45` or `f1m1`. */
    val match_id: String?,

    /** Current mode of play, can be `pre_match`, `auto`, `telop`, or `post_match`. */
    val mode: String?,

    val play: Int?,

    /** Amount of time remaining in the match, only valid during `auto` and `teleop` modes. */
    val time_remaining: Int?,

    /** 1 if the blue alliance is credited with the AUTO QUEST, 0 if not. */
    val blue_auto_quest: Int?,

    /** Number of POWER CUBES in the BOOST section of the blue alliance VAULT. */
    val blue_boost_count: Int?,

    /** Returns 1 if the blue alliance BOOST was played, or 0 if not played. */
    val blue_boost_played: Int?,

    /** Name of the current blue alliance POWER UP being played, or `null`. */
    val blue_current_powerup: String?,

    /** 1 if the blue alliance is credited with FACING THE BOSS, 0 if not. */
    val blue_face_the_boss: Int?,

    /** Number of POWER CUBES in the FORCE section of the blue alliance VAULT. */
    val blue_force_count: Int?,

    /** Returns 1 if the blue alliance FORCE was played, or 0 if not played. */
    val blue_force_played: Int?,

    /** Number of POWER CUBES in the LEVITATE section of the blue alliance VAULT. */
    val blue_levitate_count: Int?,

    /** Returns 1 if the blue alliance LEVITATE was played, or 0 if not played. */
    val blue_levitate_played: Int?,

    /** Number of seconds remaining in the blue alliance POWER UP time, or 0 if none is active. */
    val blue_powerup_time_remaining: String?,

    /** 1 if the blue alliance owns the SCALE, 0 if not. */
    val blue_scale_owned: Int?,

    /** Current score for the blue alliance. */
    val blue_score: Int?,

    /** 1 if the blue alliance owns their SWITCH, 0 if not. */
    val blue_switch_owned: Int?,

    /** 1 if the red alliance is credited with the AUTO QUEST, 0 if not. */
    val red_auto_quest: Int?,

    /** Number of POWER CUBES in the BOOST section of the red alliance VAULT. */
    val red_boost_count: Int?,

    /** Returns 1 if the red alliance BOOST was played, or 0 if not played. */
    val red_boost_played: Int?,

    /** Name of the current red alliance POWER UP being played, or `null`. */
    val red_current_powerup: String?,

    /** 1 if the red alliance is credited with FACING THE BOSS, 0 if not. */
    val red_face_the_boss: Int?,

    /** Number of POWER CUBES in the FORCE section of the red alliance VAULT. */
    val red_force_count: Int?,

    /** Returns 1 if the red alliance FORCE was played, or 0 if not played. */
    val red_force_played: Int?,

    /** Number of POWER CUBES in the LEVITATE section of the red alliance VAULT. */
    val red_levitate_count: Int?,

    /** Returns 1 if the red alliance LEVITATE was played, or 0 if not played. */
    val red_levitate_played: Int?,

    /** Number of seconds remaining in the red alliance POWER UP time, or 0 if none is active. */
    val red_powerup_time_remaining: String?,

    /** 1 if the red alliance owns the SCALE, 0 if not. */
    val red_scale_owned: Int?,

    /** Current score for the red alliance. */
    val red_score: Int?,

    /** 1 if the red alliance owns their SWITCH, 0 if not. */
    val red_switch_owned: Int?
)

/** See the 2019 FMS API documentation for a description of each value. https://frcevents2.docs.apiary.io/#reference/match-results/score-details */
class MatchScoreBreakdown2019(
    val data: JSONObject,

    val blue: MatchScoreBreakdown2019Alliance?,

    val red: MatchScoreBreakdown2019Alliance?
)

class MatchScoreBreakdown2019Alliance(
    val data: JSONObject,

    val adjustPoints: Int?,

    val autoPoints: Int?,

    val bay1: String?,

    val bay2: String?,

    val bay3: String?,

    val bay4: String?,

    val bay5: String?,

    val bay6: String?,

    val bay7: String?,

    val bay8: String?,

    val cargoPoints: Int?,

    val completeRocketRankingPoint: Boolean?,

    val completedRocketFar: Boolean?,

    val completedRocketNear: Boolean?,

    val endgameRobot1: String?,

    val endgameRobot2: String?,

    val endgameRobot3: String?,

    val foulCount: Int?,

    val foulPoints: Int?,

    val habClimbPoints: Int?,

    val habDockingRankingPoint: Boolean?,

    val habLineRobot1: String?,

    val habLineRobot2: String?,

    val habLineRobot3: String?,

    val hatchPanelPoints: Int?,

    val lowLeftRocketFar: String?,

    val lowLeftRocketNear: String?,

    val lowRightRocketFar: String?,

    val lowRightRocketNear: String?,

    val midLeftRocketFar: String?,

    val midLeftRocketNear: String?,

    val midRightRocketFar: String?,

    val midRightRocketNear: String?,

    val preMatchBay1: String?,

    val preMatchBay2: String?,

    val preMatchBay3: String?,

    val preMatchBay6: String?,

    val preMatchBay7: String?,

    val preMatchBay8: String?,

    val preMatchLevelRobot1: String?,

    val preMatchLevelRobot2: String?,

    val preMatchLevelRobot3: String?,

    val rp: Int?,

    val sandStormBonusPoints: Int?,

    val techFoulCount: Int?,

    val teleopPoints: Int?,

    val topLeftRocketFar: String?,

    val topLeftRocketNear: String?,

    val topRightRocketFar: String?,

    val topRightRocketNear: String?,

    val totalPoints: Int?
)

/** The `Media` object contains a reference for most any media associated with a team or event on TBA. */
class Media(
    val data: JSONObject,

    /** TBA identifier for this media. */
    val key: String?,

    /** String type of the media element. */
    val type: String?,

    /** The key used to identify this media on the media site. */
    val foreign_key: String?,

    /** If required, a JSON dict of additional media information. */
    val details: JSONObject?,

    /** True if the media is of high quality. */
    val preferred: Boolean?,

    /** Direct URL to the media. */
    val direct_url: String?,

    /** The URL that leads to the full web page for the media, if one exists. */
    val view_url: String?
)

class EliminationAlliance(
    val data: JSONObject,

    /** Alliance name, may be null. */
    val name: String?,

    /** Backup team called in, may be null. */
    val backup: JSONObject?,

    /** List of teams that declined the alliance. */
    val declines: List<String>?,

    /** List of team keys picked for the alliance. First pick is captain. */
    val picks: List<String>?,

    val status: JSONObject?
)

class Award(
    val data: JSONObject,

    /** The name of the award as provided by FIRST. May vary for the same award type. */
    val name: String?,

    /** Type of award given. See https://github.com/the-blue-alliance/the-blue-alliance/blob/master/consts/award_type.py#L6 */
    val award_type: Int?,

    /** The event_key of the event the award was won at. */
    val event_key: String?,

    /** A list of recipients of the award at the event. May have either a team_key or an awardee, both, or neither (in the case the award wasn't awarded at the event). */
    val recipient_list: List<AwardRecipient>?,

    /** The year this award was won. */
    val year: Int?
)

/** An `Award_Recipient` object represents the team and/or person who received an award at an event. */
class AwardRecipient(
    val data: JSONObject,

    /** The TBA team key for the team that was given the award. May be null. */
    val team_key: String?,

    /** The name of the individual given the award. May be null. */
    val awardee: String?
)

class DistrictList(
    val data: JSONObject,

    /** The short identifier for the district. */
    val abbreviation: String?,

    /** The long name for the district. */
    val display_name: String?,

    /** Key for this district, e.g. `2016ne`. */
    val key: String?,

    /** Year this district participated. */
    val year: Int?
)

/** Rank of a team in a district. */
class DistrictRanking(
    val data: JSONObject,

    /** TBA team key for the team. */
    val team_key: String?,

    /** Numerical rank of the team, 1 being top rank. */
    val rank: Int?,

    /** Any points added to a team as a result of the rookie bonus. */
    val rookie_bonus: Int?,

    /** Total district points for the team. */
    val point_total: Int?,

    /** List of events that contributed to the point total for the team. */
    val event_points: List<JSONObject>?
)

/** A Win-Loss-Tie record for a team, or an alliance. */
class WLTRecord(
    val data: JSONObject,

    /** Number of losses. */
    val losses: Int?,

    /** Number of wins. */
    val wins: Int?,

    /** Number of ties. */
    val ties: Int?
)

class Webcast(
    val data: JSONObject,

    /** Type of webcast, typically descriptive of the streaming provider. */
    val type: String?,

    /** Type specific channel information. May be the YouTube stream, or Twitch channel name. In the case of iframe types, contains HTML to embed the stream in an HTML iframe. */
    val channel: String?,

    /** File identification as may be required for some types. May be null. */
    val file: String?
)
