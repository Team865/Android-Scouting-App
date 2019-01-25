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
                            TemplateField("start_position", V5FieldType.Unknown),
                            TemplateField("hab_line", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("auto_field_area", V5FieldType.Unknown),
                            TemplateField("camera_control", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("rocket", V5FieldType.Button),
                            TemplateField("front_cargo_ship", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("auto_game_piece", V5FieldType.Unknown)
                        )
                    )
                ),
                TemplateScreen(
                    "Teleop", listOf(
                        listOf(
                            TemplateField("rocket_3", V5FieldType.Unknown),
                            TemplateField("defending", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("rocket_2", V5FieldType.Unknown),
                            TemplateField("defended", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("rocket_1", V5FieldType.Unknown),
                            TemplateField("cargo_ship", V5FieldType.Unknown)
                        ),
                        listOf(
                            TemplateField("game_piece", V5FieldType.Unknown)
                        )
                    )
                )
            ), listOf()
        ),
        superScoutTemplate = ScoutTemplate(listOf(), listOf())
    )
}