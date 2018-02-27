package ca.warp7.android.scouting;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

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
     * A button that records time as it is pressed
     */
    static final class TimerButton
            extends AppCompatButton
            implements BaseControl,
            View.OnClickListener {

        Specs.DataConstant dc;
        ActivityListener listener;

        public TimerButton(Context context) {
            super(context);
        }

        public TimerButton(Context context,
                           Specs.DataConstant dc,
                           ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(20);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF);
            setTextColor(getResources().getColor(R.color.colorAccent));

            setText(dc.getLabel());
        }

        @Override
        public void onClick(View v) {

            setTextColor(0xFFFFFFFF);
            getBackground().setColorFilter(
                    getResources().getColor(R.color.colorAccent),
                    PorterDuff.Mode.MULTIPLY);

            listener.getVibrator().vibrate(30);

            listener.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTextColor(getResources().getColor(R.color.colorAccent));
                    getBackground().clearColorFilter();
                }
            }, 1000);
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
     * A button that measures duration,
     * equivalent in function as a ToggleButton.
     * It records the time of when the button is
     */
    static final class DurationButton
            extends AppCompatButton
            implements BaseControl,
            View.OnClickListener {


        boolean isOn = false;

        Specs.DataConstant dc;
        ActivityListener listener;

        public DurationButton(Context context) {
            super(context);
        }

        public DurationButton(Context context,
                              Specs.DataConstant dc,
                              ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(20);
            setLines(2);

            updateLooks();

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

        @Override
        public void onClick(View v) {
            isOn = !isOn;
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

            setText(text);

            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            setTextColor(0xFFFFFFFF);
            getBackground().setColorFilter(
                    getResources().getColor(android.R.color.black),
                    PorterDuff.Mode.MULTIPLY);

            listener.getVibrator().vibrate(30);

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
