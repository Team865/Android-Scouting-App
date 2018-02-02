package ca.warp7.android.scouting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class AutoInputs extends Fragment
    implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    CheckBox lineCheck;
    TimedScoutingActivity activity;


    public AutoInputs(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auto_inputs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (TimedScoutingActivity) getActivity();

        lineCheck = (CheckBox) view.findViewById(R.id.cross_line_check);

        lineCheck.setChecked(false);
        lineCheck.setEnabled(true);

        lineCheck.setOnCheckedChangeListener(this);

        view.findViewById(R.id.scale_attempt_button).setOnClickListener(this);
        view.findViewById(R.id.scale_success_button).setOnClickListener(this);
        view.findViewById(R.id.switch_attempt_button).setOnClickListener(this);
        view.findViewById(R.id.switch_success_button).setOnClickListener(this);
        view.findViewById(R.id.exchange_attempt_button).setOnClickListener(this);
        view.findViewById(R.id.exchange_success_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.scale_attempt_button:
                activity.pushElapsed(Static.AUTO_SCALE_ATTEMPT);

            case R.id.scale_success_button:
                activity.pushElapsed(Static.AUTO_SCALE_SUCCESS);

            case R.id.switch_attempt_button:
                activity.pushElapsed(Static.AUTO_SWITCH_ATTEMPT);

            case R.id.switch_success_button:
                activity.pushElapsed(Static.AUTO_SWITCH_SUCCESS);

            case R.id.exchange_attempt_button:
                activity.pushElapsed(Static.AUTO_EXCHANGE_ATTEMPT);

            case R.id.exchange_success_button:
                activity.pushElapsed(Static.AUTO_EXCHANGE_ATTEMPT);
            default:

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (lineCheck.isChecked()){
            lineCheck.setEnabled(false);
            activity.pushState(Static.AUTO_ROBOT_CROSS_LINE, 1);
            activity.pushElapsed(Static.AUTO_ROBOT_CROSS_TIME);
        }
    }
}
