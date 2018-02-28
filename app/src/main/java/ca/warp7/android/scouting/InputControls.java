package ca.warp7.android.scouting;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

/**
 * Includes a set of controls for input used in the inputs fragment
 * that implement custom behaviours for scouting purposes.
 */

class InputControls {

    /**
     * This interface is to be implemented by the activity that contains
     * these controls to make available communication
     */
    interface ActivityListener {

        /**
         * @return The time Handler of the activity
         */
        Handler getHandler();

        /**
         * @return The Vibrator service of the activity
         */
        Vibrator getVibrator();
    }

    /**
     * A base interface to set the data constant and the activity listener
     */
    interface BaseControl {

        /**
         * Sets a bundle of constant values provided for the control
         *
         * @param dc the set of constant values
         */
        void setDataConstant(Specs.DataConstant dc);

        /**
         * Sets the activity listener
         *
         * @param listener the activity listener for sending data
         */
        void setActivityListener(ActivityListener listener);
    }

    /**
     * A Base button for other bottons to extend onto
     */
    static class BaseButton
            extends AppCompatButton
            implements BaseControl,
            View.OnClickListener {

        Specs.DataConstant dc;
        ActivityListener listener;

        public BaseButton(Context context) {
            super(context);
        }

        public BaseButton(Context context,
                          Specs.DataConstant dc,
                          ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(20);
            setLines(2);
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public void setDataConstant(Specs.DataConstant dc) {
            this.dc = dc;
        }

        @Override
        public void setActivityListener(ActivityListener listener) {
            this.listener = listener;
        }
    }

    /**
     * A button that records time as it is pressed
     */
    static final class TimerButton
            extends BaseButton {

        public TimerButton(Context context) {
            super(context);
        }

        public TimerButton(Context context,
                           Specs.DataConstant dc,
                           ActivityListener listener) {
            super(context, dc, listener);

            setText(dc.getLabel().replace(" ", "\n"));

            setTypeface(Typeface.SANS_SERIF);
            setTextColor(getResources().getColor(R.color.colorAccent));
        }

        @Override
        public void onClick(View v) {

            setTextColor(0xFFFFFFFF);
            getBackground().setColorFilter(
                    getResources().getColor(R.color.colorAccent),
                    PorterDuff.Mode.MULTIPLY);

            listener.getVibrator().vibrate(35);

            listener.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTextColor(getResources().getColor(R.color.colorAccent));
                    getBackground().clearColorFilter();
                }
            }, 1000);
        }
    }

    /**
     * A button that measures duration,
     * equivalent in function as a ToggleButton.
     * It records the time of when the button is
     */
    static final class DurationButton
            extends BaseButton {


        boolean isOn = false;

        public DurationButton(Context context) {
            super(context);
        }

        public DurationButton(Context context,
                              Specs.DataConstant dc,
                              ActivityListener listener) {
            super(context, dc, listener);
            updateLooks();
        }

        @Override
        public void onClick(View v) {
            isOn = !isOn;
            updateLooks();
            listener.getVibrator().vibrate(60);
        }

        void updateLooks() {
            if (isOn) {
                setTextColor(0xFFFFFFFF);
                setText(dc.getLabelOn());
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorRed),
                        PorterDuff.Mode.MULTIPLY);
            } else {
                setTextColor(getResources().getColor(R.color.colorLightGreen));
                setText(dc.getLabel());
                getBackground().clearColorFilter();
            }
        }

    }

    /**
     * A checkbox that gives true or false values
     */
    static final class Checkbox
            extends AppCompatCheckBox
            implements BaseControl,
            CompoundButton.OnCheckedChangeListener {

        Specs.DataConstant dc;
        ActivityListener listener;

        public Checkbox(Context context) {
            super(context);
        }

        public Checkbox(Context context,
                        Specs.DataConstant dc,
                        ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOnCheckedChangeListener(this);

            setAllCaps(false);
            setTextSize(20);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

            setText(dc.getLabel());

            updateLooks();

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            listener.getVibrator().vibrate(30);
            updateLooks();
        }

        @Override
        public void setDataConstant(Specs.DataConstant dc) {
            this.dc = dc;
        }

        @Override
        public void setActivityListener(ActivityListener listener) {
            this.listener = listener;
        }

        void updateLooks() {
            if (isChecked()) {
                setTextColor(getResources().getColor(R.color.colorAccent));
            } else {
                setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    /**
     * An options chooser
     */
    static final class Spinner
            extends AppCompatSpinner
            implements BaseControl,
            AdapterView.OnItemSelectedListener {

        Specs.DataConstant dc;
        ActivityListener listener;

        public Spinner(Context context) {
            super(context);
        }

        public Spinner(Context context,
                       Specs.DataConstant dc,
                       ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            ArrayAdapter<CharSequence> adapter;

            adapter = new ArrayAdapter<CharSequence>(getContext(),
                    android.R.layout.simple_spinner_item, dc.getChoices());

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            setAdapter(adapter);

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }


        @Override
        public void setDataConstant(Specs.DataConstant dc) {
            this.dc = dc;
        }

        @Override
        public void setActivityListener(ActivityListener listener) {
            this.listener = listener;
        }
    }

    /**
     * Creates a placeholder button that shows button is not found
     */
    static final class UnknownControl
            extends AppCompatButton
            implements View.OnClickListener {

        ActivityListener listener;

        public UnknownControl(Context context) {
            super(context);
        }

        public UnknownControl(Context context, String text, ActivityListener listener) {
            super(context);

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(20);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF);

            setTextColor(getResources().getColor(android.R.color.black));

            setText(text.replace(" ", "\n"));

            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            setTextColor(0xFFFFFFFF);
            getBackground().setColorFilter(
                    getResources().getColor(android.R.color.black),
                    PorterDuff.Mode.MULTIPLY);

            listener.getVibrator().vibrate(new long[]{0, 20, 60, 20}, -1);

            listener.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTextColor(getResources().getColor(android.R.color.black));
                    getBackground().clearColorFilter();
                }
            }, 1000);
        }
    }

}
