package ca.warp7.android.scouting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class EndGameInputs extends Fragment {

    Spinner rampSpinner;
    Spinner climbSpinner;


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

        rampSpinner = (Spinner) view.findViewById(R.id.ramp_spinner);
        climbSpinner = (Spinner) view.findViewById(R.id.climb_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.success_choices, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rampSpinner.setAdapter(adapter);
        climbSpinner.setAdapter(adapter);

    }
}
