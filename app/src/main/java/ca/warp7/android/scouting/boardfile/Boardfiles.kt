@file:Suppress("unused")

package ca.warp7.android.scouting.boardfile

val exampleBoardfile = Boardfile(
    version = "2020v1",
    eventName = "ONT Science Division",
    eventKey = "oncmp1",
    matchSchedule = MatchSchedule(exampleMatchSchedule),
    robotScoutTemplate = ScoutTemplate(
        listOf(
            TemplateScreen(
                "Auto", listOf(
                    listOf(
                        TemplateField("trench_intake", FieldType.Button),
                        TemplateField("fed", FieldType.Button),
                        TemplateField("other_intake", FieldType.Button)
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
                            "field_area", FieldType.MultiToggle,
                            listOf("Cross", "Mid", "Init", "Target")
                        )
                    )
                )
            ),
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
                            "field_area", FieldType.MultiToggle,
                            listOf("Cross", "Mid", "Init", "Target")
                        )
                    )
                )
            ),
            TemplateScreen(
                "Endgame", listOf(
                    listOf(
                        TemplateField("climb", FieldType.MultiToggle,
                            listOf("None", "Attempt", "Success"))
                    ),
                    listOf(
                        TemplateField(
                            "climb_location", FieldType.MultiToggle,
                            listOf("Middle", "Up", "Down", "Balanced")
                        )
                    ),
                    listOf(
                        TemplateField("balanced_after_climb", FieldType.Checkbox)
                    ),
                    listOf(
                        TemplateField("active_movement", FieldType.Checkbox)
                    )
                )
            )
        ), listOf()
    ),
    superScoutTemplate = ScoutTemplate(listOf(), listOf())
)