package ca.warp7.android.scouting.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.Board.*
import ca.warp7.android.scouting.entry.EntryItem
import ca.warp7.android.scouting.entry.EntryItemState.*

class EntryListAdapter(
    context: Context,
    scheduleItems: List<EntryItem>
) : ArrayAdapter<EntryItem>(context, 0, scheduleItems) {

    private val layoutInflater = LayoutInflater.from(context)
    private val red = ContextCompat.getColor(context, R.color.colorRed)
    private val blue = ContextCompat.getColor(context, R.color.colorBlue)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)
    private val completeColor = ContextCompat.getColor(context, R.color.colorEntryCompleted)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = if (convertView is LinearLayout)
            convertView else layoutInflater.inflate(R.layout.list_item_match_info, parent, false)
        val item = getItem(position) ?: return itemView

        // update the match number
        val matchNumber = itemView.findViewById<TextView>(R.id.match_number)
        matchNumber.text = item.match.let {
            val split = it.split("_")
            if (split.size == 2) "M" + split[1] else it
        }

        // set the icon on top of the match number
        matchNumber.setCompoundDrawablesWithIntrinsicBounds(
            0,
            when (item.state) {
                Waiting -> R.drawable.ic_layers_ablack_small
                Added -> R.drawable.ic_add_ablack
                Completed -> R.drawable.ic_done_ablack_small
            }, 0, 0
        )

        // set the background of the list item
        when (item.state) {
            Waiting -> itemView.setBackgroundColor(0)
            Completed -> itemView.setBackgroundColor(completeColor)
            Added -> itemView.setBackgroundColor(completeColor)
        }

        val red1 = itemView.findViewById<TextView>(R.id.red_1)
        val red2 = itemView.findViewById<TextView>(R.id.red_2)
        val red3 = itemView.findViewById<TextView>(R.id.red_3)
        val blue1 = itemView.findViewById<TextView>(R.id.blue_1)
        val blue2 = itemView.findViewById<TextView>(R.id.blue_2)
        val blue3 = itemView.findViewById<TextView>(R.id.blue_3)

        // reset team colors
        val teamsArray = arrayOf(red1, red2, red3, blue1, blue2, blue3)
        teamsArray.forEach { it.setTextColor(gray) }

        // set the correct color for board
        when (item.board) {
            R1 -> red1.setTextColor(red)
            R2 -> red2.setTextColor(red)
            R3 -> red3.setTextColor(red)
            B1 -> blue1.setTextColor(blue)
            B2 -> blue2.setTextColor(blue)
            B3 -> blue3.setTextColor(blue)
            RX -> arrayOf(red1, red2, red3).forEach { it.setTextColor(red) }
            BX -> arrayOf(blue1, blue2, blue3).forEach { it.setTextColor(blue) }
        }

        // check if teams list actually has a full alliance
        if (item.teams.size > 5) {
            for (i in 0 until 6) {
                val number = item.teams[i]
                teamsArray[i].text = if (number > 0) number.toString() else "- - -"
            }
        } else {
            for (i in 0 until 6) teamsArray[i].text = "- - - "
        }

        return itemView
    }
}
