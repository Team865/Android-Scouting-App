package ca.warp7.android.scouting;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Includes a set of controls for input used in the inputs fragment
 * that implement custom behaviours for scouting purposes.
 */

class InputControls {

    /**
     * A base interface to set the data constant and the activity listener
     */
    interface BaseControl {
    }

    interface ParentControlListener {
        View getSupportView();
    }

    interface ChildControl {
        void setParentListener(ParentControlListener listener);
    }

    /**
     * A Base button for other buttons to extend onto
     */
    static class BaseButton
            extends AppCompatButton
            implements BaseControl,
            ChildControl,
            View.OnClickListener {

        Specs.DataConstant dc;
        ScoutingActivityListener listener;
        ParentControlListener parentControlListener;
        View parentSupportView;

        public BaseButton(Context context) {
            super(context);
        }

        public BaseButton(Context context,
                          Specs.DataConstant dc,
                          ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(18);
            setLines(2);
            setTypeface(Typeface.SANS_SERIF);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStateListAnimator(null);
                setElevation(4);
            }
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public void setParentListener(ParentControlListener listener) {
            parentControlListener = listener;
            parentSupportView = listener.getSupportView();
        }
    }

    /**
     * A button that records time as it is pressed
     */
    static final class TimerButton
            extends BaseButton {

        int counter;

        public TimerButton(Context context) {
            super(context);
        }

        public TimerButton(Context context,
                           Specs.DataConstant dc,
                           ScoutingActivityListener listener) {
            super(context, dc, listener);

            setText(dc.getLabel().replace(" ", "\n"));

            counter = listener.getEntryModel().getCount(dc.getIndex());
            updateCounterView(false);

            if (listener.timedInputsShouldDisable()) {
                setEnabled(false);
                setTextColor(getResources().getColor(R.color.colorGray));
            } else {
                setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        @Override
        public void onClick(View v) {
            if (listener.timeIsRecordable()) {

                counter++;
                updateCounterView(true);

                setTextColor(getResources().getColor(R.color.colorWhite));
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);

                listener.getVibrator().vibrate(35);

                listener.pushCurrentTimeAsValue(dc.getIndex(), 1);

                listener.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateCounterView(false);
                        setTextColor(getResources().getColor(R.color.colorAccent));
                        getBackground().clearColorFilter();
                    }
                }, 1000);

                listener.pushStatus(dc.getLabel() + " - {t}s");
            } else {
                listener.pushStatus("Cannot Record Time");
            }
        }

        @Override
        public void setParentListener(ParentControlListener listener) {
            super.setParentListener(listener);
            updateCounterView(false);

        }

        private void updateCounterView(boolean white) {
            if (parentSupportView instanceof TextView) {
                TextView counterView = (TextView) parentSupportView;
                counterView.setText(String.valueOf(counter));

                if (white) {
                    counterView.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    counterView.setTextColor(getResources().getColor(R.color.colorAlmostBlack));
                }
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

        boolean isOn;

        public DurationButton(Context context) {
            super(context);
        }

        public DurationButton(Context context,
                              Specs.DataConstant dc,
                              ScoutingActivityListener listener) {
            super(context, dc, listener);

            isOn = listener.getEntryModel().getCount(dc.getIndex()) % 2 != 0;

            updateLooks();

            if (listener.timedInputsShouldDisable()) {
                setEnabled(false);
                setTextColor(getResources().getColor(R.color.colorGray));
            }
        }

        @Override
        public void onClick(View v) {
            if (listener.timeIsRecordable()) {
                isOn = !isOn;
                listener.pushCurrentTimeAsValue(dc.getIndex(), isOn ? 1 : 0);
                listener.pushStatus(getText().toString() + " - {t}s");

                updateLooks();
                listener.getVibrator().vibrate(60);

            } else {
                listener.pushStatus("Cannot Record Time");
            }
        }

        void updateLooks() {
            if (isOn) {
                setTextColor(getResources().getColor(R.color.colorWhite));
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
        ScoutingActivityListener listener;

        int lastWhich = 0;

        public ChoicesButton(Context context) {
            super(context);
        }

        public ChoicesButton(Context context,
                             Specs.DataConstant dc,
                             ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnClickListener(this);

            setTextColor(getResources().getColor(R.color.colorAccent));

            setAllCaps(false);
            setTextSize(18);

            setTypeface(Typeface.SANS_SERIF);
            setGravity(Gravity.CENTER);

            String[] choices = dc.getChoices();

            lastWhich = listener.getEntryModel().getLastValue(dc.getIndex());

            setText(choices[lastWhich]);
        }

        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(getContext())
                    .setTitle(dc.getLabel())
                    .setItems(dc.getChoices(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which != lastWhich) {
                                lastWhich = which;
                                setText(dc.getChoices()[which]);
                                listener.getVibrator().vibrate(30);
                                listener.getEntryModel().push(dc.getIndex(), which, 1);
                                listener.pushStatus(dc.getLabel() + " <" + getText() + ">");
                            }
                        }
                    }).show();
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
        ScoutingActivityListener listener;

        public Checkbox(Context context) {
            super(context);
        }

        public Checkbox(Context context,
                        Specs.DataConstant dc,
                        ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(18);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF);

            setText(dc.getLabel());

            setChecked(listener.getEntryModel().getCount(dc.getIndex()) % 2 != 0);

            // Temporary Fix before board format changed TODO <<<
            if (listener.timedInputsShouldDisable()) {
                setEnabled(false);
                setTextColor(getResources().getColor(R.color.colorGray));
            } else {
                setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        @Override
        public void onClick(View v) {
            listener.getVibrator().vibrate(30);
            listener.getEntryModel().push(dc.getIndex(), isChecked() ? 1 : 0, 1);
            listener.pushStatus(getText().toString() + " - " + (isChecked() ? "On" : "Off"));
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
        ScoutingActivityListener listener;

        int lastProgress;

        public SeekBar(Context context) {
            super(context);
        }

        public SeekBar(Context context,
                       Specs.DataConstant dc,
                       ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnSeekBarChangeListener(this);

            setBackgroundColor(0);

            setMax(dc.getMax());

            lastProgress = listener.getEntryModel().getLastValue(dc.getIndex());
            setProgress(lastProgress);

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

                listener.getEntryModel().push(dc.getIndex(), lastProgress, 1);

                listener.pushStatus(dc.getLabel() + " - " + lastProgress + "/" + dc.getMax());
            }
        }
    }

    /**
     * Creates a box container for a label and another control
     */

    static final class LabeledControlLayout
            extends LinearLayout
            implements BaseControl {

        //TODO Change to new parent/child interface

        Specs.DataConstant dc;
        ScoutingActivityListener listener;

        public LabeledControlLayout(Context context) {
            super(context);
        }

        public LabeledControlLayout(Context context,
                                    Specs.DataConstant dc,
                                    ScoutingActivityListener listener,
                                    View control) {
            super(context);
            this.dc = dc;
            this.listener = listener;

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
            label.setTextColor(getResources().getColor(R.color.colorAlmostBlack));

            label.setText(dc.getLabel());
            label.setGravity(Gravity.CENTER);
            label.setTextSize(15);

            label.setLayoutParams(childLayout);
            addView(label);


            control.setLayoutParams(childLayout);
            addView(control);
        }
    }

    /**
     * Creates a box container that centers the control inside it
     */
    static final class CenteredControlLayout
            extends ConstraintLayout
            implements BaseControl {

        //TODO Change to new parent/child interface

        Specs.DataConstant dc;
        ScoutingActivityListener listener;

        public CenteredControlLayout(Context context) {
            super(context);
        }

        public CenteredControlLayout(Context context,
                                     Specs.DataConstant dc,
                                     ScoutingActivityListener listener,
                                     View control) {
            super(context);
            this.dc = dc;
            this.listener = listener;

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

            childLayout = new ConstraintLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            childLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

            control.setLayoutParams(childLayout);

            addView(control);
        }
    }

    /**
     * A counter for the buttons
     */
    static final class CountedControlLayout
            extends FrameLayout
            implements BaseControl,
            ParentControlListener {

        Specs.DataConstant dc;
        ScoutingActivityListener listener;

        TextView counter;

        public CountedControlLayout(@NonNull Context context) {
            super(context);
        }

        public CountedControlLayout(Context context,
                                    Specs.DataConstant dc,
                                    ScoutingActivityListener listener,
                                    View control) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            addView(control);

            counter = new TextView(context);
            counter.setTextSize(15);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                counter.setElevation(10);
            }

            LayoutParams childLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            childLayout.leftMargin = 24;
            childLayout.topMargin = 16;

            counter.setLayoutParams(childLayout);

            addView(counter);

            if (control instanceof ChildControl) {
                ((ChildControl) control).setParentListener(this);
            }

        }

        @Override
        public View getSupportView() {
            return counter;
        }
    }

    /**
     * Creates a placeholder button that shows button is not found
     */
    static final class UnknownControl
            extends AppCompatButton
            implements View.OnClickListener {

        ScoutingActivityListener listener;

        public UnknownControl(Context context) {
            super(context);
        }

        public UnknownControl(Context context, String text, ScoutingActivityListener listener) {
            super(context);

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(18);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF);

            setTextColor(getResources().getColor(android.R.color.black));

            setText(text.replace(" ", "\n"));

            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            setTextColor(getResources().getColor(R.color.colorWhite));
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
