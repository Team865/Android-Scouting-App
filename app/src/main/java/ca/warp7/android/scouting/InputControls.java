package ca.warp7.android.scouting;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

        boolean canUpdateTime();

        void push(int t, int v, int s);

        void pushOnce(int t, int v, int s);

        void pushTime(int t, int s);

        int getState(int index);

        void setState(int index, int state);
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
     * A Base button for other buttons to extend onto
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
            setTextColor(getResources().getColor(R.color.colorAccent));
        }

        @Override
        public void onClick(View v) {
            if (listener.canUpdateTime()) {
                setTextColor(0xFFFFFFFF);
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);

                listener.getVibrator().vibrate(35);

                listener.pushTime(dc.getIndex(), 1);

                listener.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTextColor(getResources().getColor(R.color.colorAccent));
                        getBackground().clearColorFilter();
                    }
                }, 1000);
            }
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

            int state = listener.getState(dc.getIndex());

            if (state == -1) {
                listener.setState(dc.getIndex(), 0);
            } else if (state == 1) {
                isOn = true;
            }

            updateLooks();
        }

        @Override
        public void onClick(View v) {
            if (listener.canUpdateTime()) {
                isOn = !isOn;
                listener.pushTime(dc.getIndex(), isOn ? 1 : 0);
                listener.setState(dc.getIndex(), isOn ? 1 : 0);
                updateLooks();
                listener.getVibrator().vibrate(60);
            }
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
     * A button that gives the user a list of options to choose
     */
    static final class ChoicesButton
            extends AppCompatTextView
            implements BaseControl,
            View.OnClickListener {

        Specs.DataConstant dc;
        ActivityListener listener;

        public ChoicesButton(Context context) {
            super(context);
        }

        public ChoicesButton(Context context, Specs.DataConstant dc, ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOnClickListener(this);

            String[] choices = dc.getChoices();

            if (choices.length > 0) {
                setText(dc.getChoices()[0]);
            } else {
                setText(dc.getLabel());
            }
            setTextColor(getResources().getColor(R.color.colorAccent));

            setAllCaps(false);
            setTextSize(20);

            setTypeface(new Button(context).getTypeface());

            setGravity(Gravity.CENTER);

            listener.pushOnce(dc.getIndex(), 0, 1);
        }

        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(getContext())
                    .setTitle(dc.getLabel())
                    .setItems(dc.getChoices(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.getVibrator().vibrate(30);
                            setText(dc.getChoices()[which]);
                            listener.pushOnce(dc.getIndex(), which, 1);
                        }
                    }).show();
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
     * A checkbox that gives true or false values
     */
    static final class Checkbox
            extends AppCompatCheckBox
            implements BaseControl,
            View.OnClickListener {

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

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(20);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

            setText(dc.getLabel());

            updateLooks();

            int state = listener.getState(dc.getIndex());

            if (state == -1) {
                listener.pushOnce(dc.getIndex(), 0, 1);
                listener.setState(dc.getIndex(), 0);

            } else if (state == 1) {
                setChecked(true);
            }
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

                setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        @Override
        public void onClick(View v) {
            listener.getVibrator().vibrate(30);
            listener.pushOnce(dc.getIndex(), isChecked() ? 1 : 0, 1);
            listener.setState(dc.getIndex(), isChecked() ? 1 : 0);
            updateLooks();
        }
    }

    /**
     * Creates a ratings bar based on the maximum value specified
     */
    static final class SeekBar
            extends AppCompatSeekBar
            implements BaseControl,
            AppCompatSeekBar.OnSeekBarChangeListener {


        Specs.DataConstant dc;
        ActivityListener listener;

        int lastProgress;

        public SeekBar(Context context) {
            super(context);
        }

        public SeekBar(Context context,
                       Specs.DataConstant dc,
                       ActivityListener listener) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOnSeekBarChangeListener(this);

            setBackgroundColor(0);

            setMax(dc.getMax());
            setProgress(0);
            lastProgress = 0;


            int state = listener.getState(dc.getIndex());

            if (state <= -1) {
                listener.pushOnce(dc.getIndex(), 0, 1);
                lastProgress = 0;
                setProgress(0);
                listener.setState(dc.getIndex(), 0);
            } else {
                lastProgress = state;
                setProgress(state);
            }

        }

        @Override
        public void onProgressChanged(android.widget.SeekBar seekBar,
                                      int progress,
                                      boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(android.widget.SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(android.widget.SeekBar seekBar) {

            if (getProgress() != lastProgress) {
                listener.getVibrator().vibrate(20);
                lastProgress = getProgress();

                listener.pushOnce(dc.getIndex(), lastProgress, 1);
                listener.setState(dc.getIndex(), lastProgress);
            }
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
     * Creates a box container for a label and another control
     */
    static final class LabeledControlLayout
            extends LinearLayout
            implements BaseControl {

        Specs.DataConstant dc;
        ActivityListener listener;

        public LabeledControlLayout(Context context) {
            super(context);
        }

        public LabeledControlLayout(Context context,
                                    Specs.DataConstant dc,
                                    ActivityListener listener,
                                    View control) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            setOrientation(VERTICAL);

            // Set the background of the view

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new Button(getContext()).getBackground());
            } else {
                setBackgroundResource(android.R.drawable.btn_default);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(4);
            }

            TableRow.LayoutParams childLayout = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f);

            // Add the views

            TextView label = new TextView(context);

            label.setText(dc.getLabel());
            label.setGravity(Gravity.CENTER);
            label.setTextSize(15);

            label.setLayoutParams(childLayout);
            addView(label);


            control.setLayoutParams(childLayout);
            addView(control);
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
     * Creates a box container that centers the control inside it
     */
    static final class CenteredControlLayout
            extends ConstraintLayout
            implements BaseControl {

        Specs.DataConstant dc;
        ActivityListener listener;

        public CenteredControlLayout(Context context) {
            super(context);
        }

        public CenteredControlLayout(Context context,
                                     Specs.DataConstant dc,
                                     ActivityListener listener,
                                     View control) {
            super(context);
            setDataConstant(dc);
            setActivityListener(listener);

            // Set the background of the view

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new Button(getContext()).getBackground());
            } else {
                setBackgroundResource(android.R.drawable.btn_default);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(4);
            }


            ConstraintLayout.LayoutParams childLayout;

            childLayout = new ConstraintLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            childLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

            control.setLayoutParams(childLayout);

            addView(control);
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
