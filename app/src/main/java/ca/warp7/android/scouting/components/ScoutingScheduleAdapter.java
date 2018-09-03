package ca.warp7.android.scouting.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.ScoutingScheduleItem;
import ca.warp7.android.scouting.model.MatchWithAllianceItem;
import ca.warp7.android.scouting.widgets.AllianceView;

public class ScoutingScheduleAdapter extends ArrayAdapter<ScoutingScheduleItem> {

    private LayoutInflater mInflater;

    public ScoutingScheduleAdapter(@NonNull Context context,
                                   List<ScoutingScheduleItem> scheduleItems) {
        super(context, 0, scheduleItems);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView;
        if (convertView != null && convertView instanceof LinearLayout) {
            itemView = convertView;
        } else {
            itemView = mInflater.inflate(R.layout.listitem_schedule_match, parent, false);
        }

        ScoutingScheduleItem scoutingScheduleItem = getItem(position);
        if (scoutingScheduleItem != null &&
                scoutingScheduleItem instanceof MatchWithAllianceItem) {

            MatchWithAllianceItem matchItem =
                    (MatchWithAllianceItem) scoutingScheduleItem;
            AllianceView allianceView = itemView.findViewById(R.id.alliance_view);
            allianceView.setDataFromScheduledMatchItem(matchItem);
            allianceView.invalidate(); // Fix cached image in convert view

            TextView matchNumberView = itemView.findViewById(R.id.match_number);
            matchNumberView.setText(String.valueOf(matchItem.getMatchNumber()));
        }

        return itemView;
    }
}
