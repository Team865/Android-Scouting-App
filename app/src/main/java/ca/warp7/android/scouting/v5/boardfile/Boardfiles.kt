@file:Suppress("unused")

package ca.warp7.android.scouting.v5.boardfile

import java.io.File

fun File.toBoardfile(): Boardfile {
    TODO()
}

fun exampleBoardfile(): Boardfile {
    return Boardfile(
        eventName = "Humber College",
        eventKey = "2019onto3",
        matchSchedule = MatchSchedule(listOf()),
        robotScoutTemplate = ScoutTemplate(
            listOf(
                TemplateScreen(
                    "Sandstorm", listOf(
                        listOf(
                            TemplateField(
                                "start_position", V5FieldType.Choice, listOf(
                                    "None",
                                    "Level 2 Left",
                                    "Level 2 Right",
                                    "Level 1 Left",
                                    "Level 1 Center",
                                    "Level 1 Right"
                                )
                            ),
                            TemplateField("hab_line", V5FieldType.Checkbox)
                        ),
                        listOf(
                            TemplateField(
                                "auto_field_area", V5FieldType.Toggle, listOf(
                                    "Left",
                                    "Right"
                                )
                            ),
                            TemplateField("camera_control", V5FieldType.Checkbox)
                        ),
                        listOf(
                            TemplateField("rocket", V5FieldType.Button),
                            TemplateField("front_cargo_ship", V5FieldType.Button)
                        ),
                        listOf(
                            TemplateField("auto_game_piece", V5FieldType.Unknown)
                        )
                    )
                ),
                TemplateScreen(
                    "Teleop", listOf(
                        listOf(
                            TemplateField("rocket_3", V5FieldType.Button),
                            TemplateField("defending", V5FieldType.Switch)
                        ),
                        listOf(
                            TemplateField("rocket_2", V5FieldType.Button),
                            TemplateField("defended", V5FieldType.Switch)
                        ),
                        listOf(
                            TemplateField("rocket_1", V5FieldType.Button),
                            TemplateField("cargo_ship", V5FieldType.Button)
                        ),
                        listOf(
                            TemplateField("game_piece", V5FieldType.Unknown)
                        )
                    )
                ),
                TemplateScreen(
                    "Endgame", listOf(
                        listOf(
                            TemplateField("climb_level", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("assisted_climb", V5FieldType.Checkbox)
                        ),
                        listOf(
                            TemplateField("lifting_1", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("lifting_@", V5FieldType.Unknown)
                        )
                    )
                )
            ), listOf()
        ),
        superScoutTemplate = ScoutTemplate(listOf(), listOf())
    )
}