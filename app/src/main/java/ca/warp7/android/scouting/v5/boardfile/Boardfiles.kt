@file:Suppress("unused")

package ca.warp7.android.scouting.v5.boardfile

import java.io.File

fun File.toBoardfile(): Boardfile {
    TODO()
}

val exampleBoardfile = Boardfile(
    eventName = "Humber College",
    eventKey = "2019onto3",
    matchSchedule = MatchSchedule(exampleMatchSchedule),
    robotScoutTemplate = ScoutTemplate(
        listOf(
            TemplateScreen(
                "Sandstorm", listOf(
                    listOf(
                        TemplateField(
                            "start_position", V5FieldType.Toggle,
                            listOf("default:None", "L2", "L1", "C1", "R1", "R2")
                        )
                    ),
                    listOf(
                        TemplateField("hab_line", V5FieldType.Checkbox),
                        TemplateField("camera_control", V5FieldType.Checkbox)
                    ),
                    listOf(
                        TemplateField(
                            "sandstorm_field_area", V5FieldType.Toggle, listOf("default:Left", "Right")
                        ),
                        TemplateField(
                            "rocket", V5FieldType.Button,
                            listOf("resets:sandstorm_game_piece=1")
                        )
                    ),
                    listOf(
                        TemplateField(
                            "front_cargo_ship", V5FieldType.Button,
                            listOf("resets:sandstorm_game_piece=1")
                        ),
                        TemplateField(
                            "side_cargo_ship", V5FieldType.Button,
                            listOf("resets:sandstorm_game_piece=1")
                        )
                    ),
                    listOf(
                        TemplateField(
                            "sandstorm_game_piece", V5FieldType.Toggle,
                            listOf("Cargo", "default:None", "Hatch")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Teleop", listOf(
                    listOf(
                        TemplateField("rocket_3", V5FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("defending", V5FieldType.Switch, listOf("resets:game_piece=1"))
                    ),
                    listOf(
                        TemplateField("rocket_2", V5FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("defended", V5FieldType.Switch, listOf("resets:game_piece=1"))
                    ),
                    listOf(
                        TemplateField("rocket_1", V5FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("cargo_ship", V5FieldType.Button, listOf("resets:game_piece=1"))
                    ),
                    listOf(
                        TemplateField(
                            "game_piece", V5FieldType.Toggle,
                            listOf("Cargo", "default:None", "Hatch")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Endgame", listOf(
                    listOf(
                        TemplateField("climb_level", V5FieldType.Toggle, listOf("default:None", "1", "2", "3"))
                    ),
                    listOf(
                        TemplateField("assisted_climb", V5FieldType.Checkbox)
                    ),
                    listOf(TemplateField("lifting_robot_1", V5FieldType.Toggle, listOf("default:None", "2", "3"))),
                    listOf(TemplateField("lifting_robot_2", V5FieldType.Toggle, listOf("default:None", "2", "3")))
                )
            )
        ), listOf()
    ),
    superScoutTemplate = ScoutTemplate(listOf(), listOf())
)

val exampleTeams = mutableListOf(
    746,
    771,
    854,
    865,
    907,
    1114,
    1310,
    1374,
    2198,
    2405,
    2935,
    3683,
    4039,
    4308,
    4343,
    4939,
    5031,
    5834,
    5870,
    6009,
    6141,
    6513,
    6977,
    6978,
    7013,
    7480,
    7509,
    7558,
    7603,
    7623,
    7723,
    7902
)