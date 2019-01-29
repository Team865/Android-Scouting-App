package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.EntryItem

class EntriesListAdapter(
    context: Context,
    scheduleItems: List<EntryItem>
) : ArrayAdapter<EntryItem>(context, 0, scheduleItems) {

    private val mInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = if (convertView is LinearLayout)
            convertView else mInflater.inflate(R.layout.list_item_v5_match, parent, false)
        getItem(position)?.apply {
            itemView.findViewById<TextView>(R.id.match_number).text = match
        }
        return itemView
    }
}
