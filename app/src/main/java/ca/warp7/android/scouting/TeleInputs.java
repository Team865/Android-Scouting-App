package ca.warp7.android.scouting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class TeleInputs extends Fragment
    implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    TimedScoutingActivity activity;
    ToggleButton defense;


    public TeleInputs() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tele_inputs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (TimedScoutingActivity) getActivity();

        defense = (ToggleButton) view.findViewById(R.id.defense_toggle);
        defense.setOnCheckedChangeListener(this);
        defense.setChecked(false);
        defense.setTextColor(0xFF008800);

        view.findViewById(R.id.intake_button).setOnClickListener(this);
        view.findViewById(R.id.exchange_button).setOnClickListener(this);
        view.findViewById(R.id.aswitch_button).setOnClickListener(this);
        view.findViewById(R.id.oswitch_button).setOnClickListener(this);
        view.findViewById(R.id.scale_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.intake_button:
                activity.pushElapsed(Static.TELE_INTAKE);
                break;

            case R.id.exchange_button:
                activity.pushElapsed(Static.TELE_EXCHANGE);
                break;

            case R.id.aswitch_button:
                activity.pushElapsed(Static.TELE_ALLIANCE_SWITCH);
                break;

            case R.id.oswitch_button:
                activity.pushElapsed(Static.TELE_OPPONENT_SWITCH);
                break;

            case R.id.scale_button:
                activity.pushElapsed(Static.TELE_SCALE);
                break;

            default:
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //TODO defense data may not be closed off [ISSUE!!!]
        if(defense.isChecked()){
            activity.pushElapsed(Static.TELE_DEFENSE_START);
            defense.setTextColor(0xFFFF0000);
        } else {
            activity.pushElapsed(Static.TELE_DEFENSE_END);
            defense.setTextColor(0xFF008800);
        }
    }
}
