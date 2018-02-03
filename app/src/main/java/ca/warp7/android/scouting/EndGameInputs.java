package ca.warp7.android.scouting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;


public class EndGameInputs extends Fragment
    implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    Spinner rampSpinner;
    Spinner climbSpinner;
    TimedScoutingActivity activity;


    public EndGameInputs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_end_game_inputs, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (TimedScoutingActivity) getActivity();

        rampSpinner = (Spinner) view.findViewById(R.id.ramp_spinner);
        climbSpinner = (Spinner) view.findViewById(R.id.climb_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.success_choices, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rampSpinner.setAdapter(adapter);
        climbSpinner.setAdapter(adapter);

        rampSpinner.setOnItemSelectedListener(this);
        climbSpinner.setOnItemSelectedListener(this);

        ((SeekBar) view.findViewById(R.id.attachment_seek)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.climb_speed_seek)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.intake_speed_seek)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.intake_consistency_seek)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.exchange_seek)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.switch_seek)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.scale_seek)).setOnSeekBarChangeListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()){

            case R.id.attachment_seek:
                activity.pushState(Static.END_ATTACHMENT, progress);
                break;

            case R.id.climb_speed_seek:
                activity.pushState(Static.END_CLIMB_SPEED, progress);
                break;

            case R.id.intake_speed_seek:
                activity.pushState(Static.END_INTAKE_SPEED, progress);
                break;

            case R.id.intake_consistency_seek:
                activity.pushState(Static.END_INTAKE_CONSISTENCY, progress);
                break;

            case R.id.exchange_seek:
                activity.pushState(Static.END_EXCHANGE, progress);
                break;

            case R.id.switch_seek:
                activity.pushState(Static.END_SWITCH, progress);
                break;

            case R.id.scale_seek:
                activity.pushState(Static.END_SCALE, progress);
                break;

            default:

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

        int data = item.equals("Success")? 2 : (item.equals("Attempt")? 1 : 0);

        if (parent.getId() == R.id.ramp_spinner)
            activity.pushState(Static.END_RAMP, data);

        else if (parent.getId() == R.id.climb_spinner)
            activity.pushState(Static.END_CLIMB, data);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
