package ca.warp7.android.scouting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class InputsFragment extends Fragment {

    private InputsFragmentListener listener;

    public InputsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inputs, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof InputsFragmentListener) {
            listener = (InputsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InputsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    interface InputsFragmentListener {
    }
}
