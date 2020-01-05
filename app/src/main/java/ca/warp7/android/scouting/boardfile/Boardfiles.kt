@file:Suppress("unused")

package ca.warp7.android.scouting.boardfile

import java.io.File

fun File.toBoardfile(): Boardfile {
    TODO()
}

val exampleBoardfile = Boardfile(
    eventName = "ONT Science Division",
    eventKey = "2019oncmp1",
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
                        TemplateField("opponent_field", V5FieldType.Switch)
                    ),
                    listOf(
                        TemplateField("rocket_2", V5FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("defended", V5FieldType.Switch)
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