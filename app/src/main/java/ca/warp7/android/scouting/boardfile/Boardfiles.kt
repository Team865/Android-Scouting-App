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
                "Teleop", listOf(
                    listOf(
                        TemplateField("control_panel", FieldType.Switch),
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
                            "field_area", FieldType.Toggle,
                            listOf("Cross", "Mid", "Init", "Target")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Endgame", listOf(
                    listOf(
                        TemplateField("parked_in_rendezvous_zone", FieldType.Checkbox)
                    ),
                    listOf(
                        TemplateField("side_hang", FieldType.Checkbox),
                        TemplateField("middle_hang", FieldType.Checkbox)
                    ),
                    listOf(
                        TemplateField("balanced", FieldType.Checkbox)
                    ),
                    listOf(
                        TemplateField("lifting_another_robot", FieldType.Checkbox)
                    )
                )
            )
        ), listOf()
    ),
    superScoutTemplate = ScoutTemplate(listOf(), listOf())
)