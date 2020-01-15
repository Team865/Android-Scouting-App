package ca.warp7.android.scouting.ui.field

import android.content.Context
import ca.warp7.android.scouting.BaseScoutingActivity
import ca.warp7.android.scouting.boardfile.TemplateField

data class FieldData(val context: Context,
                     val templateField: TemplateField,
                     val scoutingActivity: BaseScoutingActivity,
                     val modifiedName: String,
                     val typeIndex: Int
)