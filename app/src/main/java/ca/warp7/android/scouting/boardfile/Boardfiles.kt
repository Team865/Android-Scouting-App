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
                            "start_position", FieldType.Toggle,
                            listOf("default:None", "L2", "L1", "C1", "R1", "R2")
                        )
                    ),
                    listOf(
                        TemplateField("hab_line", FieldType.Checkbox),
                        TemplateField("camera_control", FieldType.Checkbox)
                    ),
                    listOf(
                        TemplateField(
                            "sandstorm_field_area", FieldType.Toggle, listOf("default:Left", "Right")
                        ),
                        TemplateField(
                            "rocket", FieldType.Button,
                            listOf("resets:sandstorm_game_piece=1")
                        )
                    ),
                    listOf(
                        TemplateField(
                            "front_cargo_ship", FieldType.Button,
                            listOf("resets:sandstorm_game_piece=1")
                        ),
                        TemplateField(
                            "side_cargo_ship", FieldType.Button,
                            listOf("resets:sandstorm_game_piece=1")
                        )
                    ),
                    listOf(
                        TemplateField(
                            "sandstorm_game_piece", FieldType.Toggle,
                            listOf("Cargo", "default:None", "Hatch")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Teleop", listOf(
                    listOf(
                        TemplateField("rocket_3", FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("opponent_field", FieldType.Switch)
                    ),
                    listOf(
                        TemplateField("rocket_2", FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("defended", FieldType.Switch)
                    ),
                    listOf(
                        TemplateField("rocket_1", FieldType.Button, listOf("resets:game_piece=1")),
                        TemplateField("cargo_ship", FieldType.Button, listOf("resets:game_piece=1"))
                    ),
                    listOf(
                        TemplateField(
                            "game_piece", FieldType.Toggle,
                            listOf("Cargo", "default:None", "Hatch")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Evan", listOf(
                    listOf(
                        TemplateField("wheel", FieldType.Switch),
                        TemplateField("defending", FieldType.Switch),
                        TemplateField("resisting", FieldType.Switch)
                    ),
                    listOf(
                        TemplateField("low", FieldType.Button),
                        TemplateField("inner", FieldType.Button),
                        TemplateField("outer", FieldType.Button)
                    ),
                    listOf(
                        TemplateField("low_miss", FieldType.Button),
                        TemplateField("high_miss", FieldType.Button)
                    ),
                    listOf(
                        TemplateField(
                            "filed_area", FieldType.Toggle,
                            listOf("Cross", "Far", "Init", "Target")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Endgame", listOf(
                    listOf(
                        TemplateField("climb_level", FieldType.Toggle, listOf("default:None", "1", "2", "3"))
                    ),
                    listOf(
                        TemplateField("assisted_climb", FieldType.Checkbox)
                    ),
                    listOf(TemplateField("lifting_robot_1", FieldType.Toggle, listOf("default:None", "2", "3"))),
                    listOf(TemplateField("lifting_robot_2", FieldType.Toggle, listOf("default:None", "2", "3")))
                )
            )
        ), listOf()
    ),
    superScoutTemplate = ScoutTemplate(listOf(), listOf())
)