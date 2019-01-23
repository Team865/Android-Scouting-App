package ca.warp7.android.scouting.v5.ui

import android.content.Context
import ca.warp7.android.scouting.v5.BaseScoutingActivity
import ca.warp7.android.scouting.v5.boardfile.TemplateField

data class FieldGroup(val context: Context,
                      val templateField: TemplateField,
                      val scoutingActivity: BaseScoutingActivity
)