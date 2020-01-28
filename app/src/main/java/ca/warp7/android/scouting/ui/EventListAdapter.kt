package ca.warp7.android.scouting.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.tba.EventSimple

class EventListAdapter(
    context: Context,
    items: List<EventSimple>
) : ArrayAdapter<EventSimple>(context, 0, items) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = if (convertView is RelativeLayout)
            convertView else layoutInflater.inflate(
            R.layout.list_item_event_info, parent, false)
        val item = getItem(position) ?: return itemView

        val text1 = itemView.findViewById<TextView>(R.id.text1)
        val text2 = itemView.findViewById<TextView>(R.id.text2)

        text1.text = item.name
        val date = "${item.start_date} in ${item.city}, ${item.state_prov}, ${item.country}"
        text2.text = date
        return itemView
    }
}