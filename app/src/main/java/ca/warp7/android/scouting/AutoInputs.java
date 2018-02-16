package ca.warp7.android.scouting;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


public class AutoInputs extends Fragment
    implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    CheckBox lineCheck;
    TimedScoutingActivity activity;

    int counter = 0;


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

        counter = 0;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.scale_attempt_button:
                activity.pushElapsed(Static.AUTO_SCALE_ATTEMPT);
                getView().findViewById(R.id.msg_top).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getView().findViewById(R.id.msg_top).setVisibility(View.INVISIBLE);
                    }
                }, 1000);

                break;

            case R.id.scale_success_button:
                activity.pushElapsed(Static.AUTO_SCALE_SUCCESS);
                getView().findViewById(R.id.msg_buttom).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getView().findViewById(R.id.msg_buttom).setVisibility(View.INVISIBLE);
                    }
                }, 1000);
                break;

            case R.id.switch_attempt_button:
                activity.pushElapsed(Static.AUTO_SWITCH_ATTEMPT);
                Toast.makeText(activity, "Switch Attempt at x minutes x seconds",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.switch_success_button:
                activity.pushElapsed(Static.AUTO_SWITCH_SUCCESS);
                final Button b = getView().findViewById(R.id.switch_success_button);
                b.setTextColor(0xFFFFFFFF);
                b.setBackgroundColor(0xFF3F51B5);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setBackgroundResource(android.R.drawable.btn_default);
                        b.setTextColor(0xFF000000);
                    }
                }, 500);
                break;

            case R.id.exchange_attempt_button:
                activity.pushElapsed(Static.AUTO_EXCHANGE_ATTEMPT);
                ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE))
                        .vibrate(40);
                break;

            case R.id.exchange_success_button:
                activity.pushElapsed(Static.AUTO_EXCHANGE_ATTEMPT);
                counter++;
                ((Button) getView().findViewById(R.id.exchange_success_button))
                        .setText("Exchange Success \n" + counter);
                ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE))
                        .vibrate(15);
                break;

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
