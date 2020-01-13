// API Version 3.5 

@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference", "UNUSED_VARIABLE", "DuplicatedCode")

package ca.warp7.android.scouting.tba


import org.json.JSONObject

/**
 * Returns API status, and TBA status information.
 */
fun TBA.getStatus(): APIStatus = get("/status").toAPIStatus()

/**
 * Gets a list of `Team` objects, paginated in groups of 500.
 */
fun TBA.getTeams(
    page_num: Int
): List<Team> = getArray("/teams/$page_num").map { (it as JSONObject).toTeam() }

/**
 * Gets a list of short form `Team_Simple` objects, paginated in groups of 500.
 */
fun TBA.getTeamsSimple(
    page_num: Int
): List<TeamSimple> = getArray("/teams/$page_num/simple").map { (it as JSONObject).toTeamSimple() }

/**
 * Gets a list of Team keys, paginated in groups of 500. (Note, each page will not have 500 teams, but will include the teams within that range of 500.)
 */
fun TBA.getTeamsKeys(
    page_num: Int
): List<String> = getArray("/teams/$page_num/keys").map { it as String }

/**
 * Gets a list of `Team` objects that competed in the given year, paginated in groups of 500.
 */
fun TBA.getTeamsByYear(
    year: Int,
    page_num: Int
): List<Team> = getArray("/teams/$year/$page_num").map { (it as JSONObject).toTeam() }

/**
 * Gets a list of short form `Team_Simple` objects that competed in the given year, paginated in groups of 500.
 */
fun TBA.getTeamsByYearSimple(
    year: Int,
    page_num: Int
): List<TeamSimple> = getArray("/teams/$year/$page_num/simple").map { (it as JSONObject).toTeamSimple() }

/**
 * Gets a list Team Keys that competed in the given year, paginated in groups of 500.
 */
fun TBA.getTeamsByYearKeys(
    year: Int,
    page_num: Int
): List<String> = getArray("/teams/$year/$page_num/keys").map { it as String }

/**
 * Gets a `Team` object for the team referenced by the given key.
 */
fun TBA.getTeam(
    team_key: String
): Team = get("/team/$team_key").toTeam()

/**
 * Gets a `Team_Simple` object for the team referenced by the given key.
 */
fun TBA.getTeamSimple(
    team_key: String
): TeamSimple = get("/team/$team_key/simple").toTeamSimple()

/**
 * Gets a list of years in which the team participated in at least one competition.
 */
fun TBA.getTeamYearsParticipated(
    team_key: String
): List<Int> = getArray("/team/$team_key/years_participated").map { it as Int }

/**
 * Gets an array of districts representing each year the team was in a district. Will return an empty array if the team was never in a district.
 */
fun TBA.getTeamDistricts(
    team_key: String
): List<DistrictList> = getArray("/team/$team_key/districts").map { (it as JSONObject).toDistrictList() }

/**
 * Gets a list of year and robot name pairs for each year that a robot name was provided. Will return an empty array if the team has never named a robot.
 */
fun TBA.getTeamRobots(
    team_key: String
): List<TeamRobot> = getArray("/team/$team_key/robots").map { (it as JSONObject).toTeamRobot() }

/**
 * Gets a list of all events this team has competed at.
 */
fun TBA.getTeamEvents(
    team_key: String
): List<Event> = getArray("/team/$team_key/events").map { (it as JSONObject).toEvent() }

/**
 * Gets a short-form list of all events this team has competed at.
 */
fun TBA.getTeamEventsSimple(
    team_key: String
): List<EventSimple> = getArray("/team/$team_key/events/simple").map { (it as JSONObject).toEventSimple() }

/**
 * Gets a list of the event keys for all events this team has competed at.
 */
fun TBA.getTeamEventsKeys(
    team_key: String
): List<String> = getArray("/team/$team_key/events/keys").map { it as String }

/**
 * Gets a list of events this team has competed at in the given year.
 */
fun TBA.getTeamEventsByYear(
    team_key: String,
    year: Int
): List<Event> = getArray("/team/$team_key/events/$year").map { (it as JSONObject).toEvent() }

/**
 * Gets a short-form list of events this team has competed at in the given year.
 */
fun TBA.getTeamEventsByYearSimple(
    team_key: String,
    year: Int
): List<EventSimple> = getArray("/team/$team_key/events/$year/simple").map { (it as JSONObject).toEventSimple() }

/**
 * Gets a list of the event keys for events this team has competed at in the given year.
 */
fun TBA.getTeamEventsByYearKeys(
    team_key: String,
    year: Int
): List<String> = getArray("/team/$team_key/events/$year/keys").map { it as String }

/**
 * Gets a key-value list of the event statuses for events this team has competed at in the given year.
 */
fun TBA.getTeamEventsStatusesByYear(
    team_key: String,
    year: Int
): Map<String, TeamEventStatus?> = get("/team/$team_key/events/$year/statuses").mapValues { (it as JSONObject?)!!.toTeamEventStatus() }

/**
 * Gets a list of matches for the given team and event.
 */
fun TBA.getTeamEventMatches(
    team_key: String,
    event_key: String
): List<Match> = getArray("/team/$team_key/event/$event_key/matches").map { (it as JSONObject).toMatch() }

/**
 * Gets a short-form list of matches for the given team and event.
 */
fun TBA.getTeamEventMatchesSimple(
    team_key: String,
    event_key: String
): List<Match> = getArray("/team/$team_key/event/$event_key/matches/simple").map { (it as JSONObject).toMatch() }

/**
 * Gets a list of match keys for matches for the given team and event.
 */
fun TBA.getTeamEventMatchesKeys(
    team_key: String,
    event_key: String
): List<String> = getArray("/team/$team_key/event/$event_key/matches/keys").map { it as String }

/**
 * Gets a list of awards the given team won at the given event.
 */
fun TBA.getTeamEventAwards(
    team_key: String,
    event_key: String
): List<Award> = getArray("/team/$team_key/event/$event_key/awards").map { (it as JSONObject).toAward() }

/**
 * Gets the competition rank and status of the team at the given event.
 */
fun TBA.getTeamEventStatus(
    team_key: String,
    event_key: String
): TeamEventStatus = get("/team/$team_key/event/$event_key/status").toTeamEventStatus()

/**
 * Gets a list of awards the given team has won.
 */
fun TBA.getTeamAwards(
    team_key: String
): List<Award> = getArray("/team/$team_key/awards").map { (it as JSONObject).toAward() }

/**
 * Gets a list of awards the given team has won in a given year.
 */
fun TBA.getTeamAwardsByYear(
    team_key: String,
    year: Int
): List<Award> = getArray("/team/$team_key/awards/$year").map { (it as JSONObject).toAward() }

/**
 * Gets a list of matches for the given team and year.
 */
fun TBA.getTeamMatchesByYear(
    team_key: String,
    year: Int
): List<Match> = getArray("/team/$team_key/matches/$year").map { (it as JSONObject).toMatch() }

/**
 * Gets a short-form list of matches for the given team and year.
 */
fun TBA.getTeamMatchesByYearSimple(
    team_key: String,
    year: Int
): List<MatchSimple> = getArray("/team/$team_key/matches/$year/simple").map { (it as JSONObject).toMatchSimple() }

/**
 * Gets a list of match keys for matches for the given team and year.
 */
fun TBA.getTeamMatchesByYearKeys(
    team_key: String,
    year: Int
): List<String> = getArray("/team/$team_key/matches/$year/keys").map { it as String }

/**
 * Gets a list of Media (videos / pictures) for the given team and year.
 */
fun TBA.getTeamMediaByYear(
    team_key: String,
    year: Int
): List<Media> = getArray("/team/$team_key/media/$year").map { (it as JSONObject).toMedia() }

/**
 * Gets a list of Media (videos / pictures) for the given team and tag.
 */
fun TBA.getTeamMediaByTag(
    team_key: String,
    media_tag: String
): List<Media> = getArray("/team/$team_key/media/tag/$media_tag").map { (it as JSONObject).toMedia() }

/**
 * Gets a list of Media (videos / pictures) for the given team, tag and year.
 */
fun TBA.getTeamMediaByTagYear(
    team_key: String,
    media_tag: String,
    year: Int
): List<Media> = getArray("/team/$team_key/media/tag/$media_tag/$year").map { (it as JSONObject).toMedia() }

/**
 * Gets a list of Media (social media) for the given team.
 */
fun TBA.getTeamSocialMedia(
    team_key: String
): List<Media> = getArray("/team/$team_key/social_media").map { (it as JSONObject).toMedia() }

/**
 * Gets a list of events in the given year.
 */
fun TBA.getEventsByYear(
    year: Int
): List<Event> = getArray("/events/$year").map { (it as JSONObject).toEvent() }

/**
 * Gets a short-form list of events in the given year.
 */
fun TBA.getEventsByYearSimple(
    year: Int
): List<EventSimple> = getArray("/events/$year/simple").map { (it as JSONObject).toEventSimple() }

/**
 * Gets a list of event keys in the given year.
 */
fun TBA.getEventsByYearKeys(
    year: Int
): List<String> = getArray("/events/$year/keys").map { it as String }

/**
 * Gets an Event.
 */
fun TBA.getEvent(
    event_key: String
): Event = get("/event/$event_key").toEvent()

/**
 * Gets a short-form Event.
 */
fun TBA.getEventSimple(
    event_key: String
): EventSimple = get("/event/$event_key/simple").toEventSimple()

/**
 * Gets a list of Elimination Alliances for the given Event.
 */
fun TBA.getEventAlliances(
    event_key: String
): List<EliminationAlliance> = getArray("/event/$event_key/alliances").map { (it as JSONObject).toEliminationAlliance() }

/**
 * Gets a set of Event-specific insights for the given Event.
 */
fun TBA.getEventInsights(
    event_key: String
): EventInsights = get("/event/$event_key/insights").toEventInsights()

/**
 * Gets a set of Event OPRs (including OPR, DPR, and CCWM) for the given Event.
 */
fun TBA.getEventOPRs(
    event_key: String
): EventOPRs = get("/event/$event_key/oprs").toEventOPRs()

/**
 * Gets information on TBA-generated predictions for the given Event. Contains year-specific information. *WARNING* This endpoint is currently under development and may change at any time.
 */
fun TBA.getEventPredictions(
    event_key: String
): EventPredictions = get("/event/$event_key/predictions").toEventPredictions()

/**
 * Gets a list of team rankings for the Event.
 */
fun TBA.getEventRankings(
    event_key: String
): EventRanking = get("/event/$event_key/rankings").toEventRanking()

/**
 * Gets a list of team rankings for the Event.
 */
fun TBA.getEventDistrictPoints(
    event_key: String
): EventDistrictPoints = get("/event/$event_key/district_points").toEventDistrictPoints()

/**
 * Gets a list of `Team` objects that competed in the given event.
 */
fun TBA.getEventTeams(
    event_key: String
): List<Team> = getArray("/event/$event_key/teams").map { (it as JSONObject).toTeam() }

/**
 * Gets a short-form list of `Team` objects that competed in the given event.
 */
fun TBA.getEventTeamsSimple(
    event_key: String
): List<TeamSimple> = getArray("/event/$event_key/teams/simple").map { (it as JSONObject).toTeamSimple() }

/**
 * Gets a list of `Team` keys that competed in the given event.
 */
fun TBA.getEventTeamsKeys(
    event_key: String
): List<String> = getArray("/event/$event_key/teams/keys").map { it as String }

/**
 * Gets a key-value list of the event statuses for teams competing at the given event.
 */
fun TBA.getEventTeamsStatuses(
    event_key: String
): Map<String, TeamEventStatus?> = get("/event/$event_key/teams/statuses").mapValues { (it as JSONObject?)!!.toTeamEventStatus() }

/**
 * Gets a list of matches for the given event.
 */
fun TBA.getEventMatches(
    event_key: String
): List<Match> = getArray("/event/$event_key/matches").map { (it as JSONObject).toMatch() }

/**
 * Gets a short-form list of matches for the given event.
 */
fun TBA.getEventMatchesSimple(
    event_key: String
): List<MatchSimple> = getArray("/event/$event_key/matches/simple").map { (it as JSONObject).toMatchSimple() }

/**
 * Gets a list of match keys for the given event.
 */
fun TBA.getEventMatchesKeys(
    event_key: String
): List<String> = getArray("/event/$event_key/matches/keys").map { it as String }

/**
 * Gets an array of Match Keys for the given event key that have timeseries data. Returns an empty array if no matches have timeseries data.
*WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up.
*WARNING:* This endpoint and corresponding data models are under *active development* and may change at any time, including in breaking ways.
 */
fun TBA.getEventMatchTimeseries(
    event_key: String
): List<String> = getArray("/event/$event_key/matches/timeseries").map { it as String }

/**
 * Gets a list of awards from the given event.
 */
fun TBA.getEventAwards(
    event_key: String
): List<Award> = getArray("/event/$event_key/awards").map { (it as JSONObject).toAward() }

/**
 * Gets a `Match` object for the given match key.
 */
fun TBA.getMatch(
    match_key: String
): Match = get("/match/$match_key").toMatch()

/**
 * Gets a short-form `Match` object for the given match key.
 */
fun TBA.getMatchSimple(
    match_key: String
): MatchSimple = get("/match/$match_key/simple").toMatchSimple()

/**
 * Gets an array of game-specific Match Timeseries objects for the given match key or an empty array if not available.
*WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up.
*WARNING:* This endpoint and corresponding data models are under *active development* and may change at any time, including in breaking ways.
 */
fun TBA.getMatchTimeseries(
    match_key: String
): List<JSONObject> = getArray("/match/$match_key/timeseries").map { it as JSONObject }

/**
 * Gets a list of districts and their corresponding district key, for the given year.
 */
fun TBA.getDistrictsByYear(
    year: Int
): List<DistrictList> = getArray("/districts/$year").map { (it as JSONObject).toDistrictList() }

/**
 * Gets a list of events in the given district.
 */
fun TBA.getDistrictEvents(
    district_key: String
): List<Event> = getArray("/district/$district_key/events").map { (it as JSONObject).toEvent() }

/**
 * Gets a short-form list of events in the given district.
 */
fun TBA.getDistrictEventsSimple(
    district_key: String
): List<EventSimple> = getArray("/district/$district_key/events/simple").map { (it as JSONObject).toEventSimple() }

/**
 * Gets a list of event keys for events in the given district.
 */
fun TBA.getDistrictEventsKeys(
    district_key: String
): List<String> = getArray("/district/$district_key/events/keys").map { it as String }

/**
 * Gets a list of `Team` objects that competed in events in the given district.
 */
fun TBA.getDistrictTeams(
    district_key: String
): List<Team> = getArray("/district/$district_key/teams").map { (it as JSONObject).toTeam() }

/**
 * Gets a short-form list of `Team` objects that competed in events in the given district.
 */
fun TBA.getDistrictTeamsSimple(
    district_key: String
): List<TeamSimple> = getArray("/district/$district_key/teams/simple").map { (it as JSONObject).toTeamSimple() }

/**
 * Gets a list of `Team` objects that competed in events in the given district.
 */
fun TBA.getDistrictTeamsKeys(
    district_key: String
): List<String> = getArray("/district/$district_key/teams/keys").map { it as String }

/**
 * Gets a list of team district rankings for the given district.
 */
fun TBA.getDistrictRankings(
    district_key: String
): List<DistrictRanking> = getArray("/district/$district_key/rankings").map { (it as JSONObject).toDistrictRanking() }
