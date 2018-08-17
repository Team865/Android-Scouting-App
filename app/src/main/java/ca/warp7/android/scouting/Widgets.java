package ca.warp7.android.scouting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.SANS_SERIF;

public class Widgets {

    @SuppressWarnings("unused")
    static class AllianceView extends View {

        static final float kPad = 36;
        static final int kRadius = 16;

        Paint mAlmostRedPaint = new Paint();
        Paint mAlmostBluePaint = new Paint();
        Paint mGrayTextPaint = new Paint();
        Paint mRedTextPaint = new Paint();
        Paint mBlueTextPaint = new Paint();
        Paint mRedFocusedTextPaint = new Paint();
        Paint mBlueFocusedTextPaint = new Paint();

        private float mMinimumWidth;

        private String mR1Team = "Red 1";
        private String mR2Team = "Red 2";
        private String mR3Team = "Red 3";
        private String mB1Team = "Blue 1";
        private String mB2Team = "Blue 2";
        private String mB3Team = "Blue 3";

        private ManagedData.RobotPosition mFocusedRobotPosition = ManagedData.RobotPosition.RED1;
        private boolean mShouldFocusARobot = false;


        public AllianceView(Context context) {
            super(context);
            initPaints();
        }

        public AllianceView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initPaints();
        }

        private void initPaints() {
            mAlmostRedPaint.setColor(getResources().getColor(R.color.colorAlmostRed));
            mAlmostBluePaint.setColor(getResources().getColor(R.color.colorAlmostBlue));
            mGrayTextPaint.setColor(getResources().getColor(R.color.colorGray));
            mGrayTextPaint.setTextSize(36);
            mGrayTextPaint.setTypeface(Typeface.SANS_SERIF);
            mGrayTextPaint.setAntiAlias(true);
            mRedTextPaint.setColor(getResources().getColor(R.color.colorRed));
            mRedTextPaint.setTextSize(36);
            mRedTextPaint.setAntiAlias(true);
            mBlueTextPaint.setColor(getResources().getColor(R.color.colorBlue));
            mBlueTextPaint.setTextSize(36);
            mBlueTextPaint.setAntiAlias(true);
            mRedFocusedTextPaint.setColor(getResources().getColor(R.color.colorRed));
            mRedFocusedTextPaint.setTypeface(Typeface.create(SANS_SERIF, BOLD));
            mRedFocusedTextPaint.setTextSize(36);
            mRedFocusedTextPaint.setAntiAlias(true);
            mBlueFocusedTextPaint.setColor(getResources().getColor(R.color.colorBlue));
            mBlueFocusedTextPaint.setTypeface(Typeface.create(SANS_SERIF, BOLD));
            mBlueFocusedTextPaint.setTextSize(36);
            mBlueFocusedTextPaint.setAntiAlias(true);

            mMinimumWidth = mRedFocusedTextPaint.measureText("8888");
        }

        public void setFocusedRobotPosition(ManagedData.RobotPosition position) {
            mFocusedRobotPosition = position;
            mShouldFocusARobot = true;
        }

        public void setNoRobotFocused() {
            mShouldFocusARobot = false;
        }

        public void setAllianceFromMatchInfo(ManagedData.MatchInfo matchInfo) {
            mR1Team = String.valueOf(matchInfo.getTeamAt(0));
            mR2Team = String.valueOf(matchInfo.getTeamAt(1));
            mR3Team = String.valueOf(matchInfo.getTeamAt(2));
            mB1Team = String.valueOf(matchInfo.getTeamAt(3));
            mB2Team = String.valueOf(matchInfo.getTeamAt(4));
            mB3Team = String.valueOf(matchInfo.getTeamAt(5));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            final int w = getMeasuredWidth();
            final int h = getMeasuredHeight();
            canvas.drawRect(0, kRadius, w, h / 2, mAlmostRedPaint);
            canvas.drawRect(kRadius, 0, w - kRadius, kRadius, mAlmostRedPaint);
            canvas.drawCircle(kRadius, kRadius, kRadius, mAlmostRedPaint);
            canvas.drawCircle(w - kRadius, kRadius, kRadius, mAlmostRedPaint);
            canvas.drawRect(0, h / 2, w, h - kRadius, mAlmostBluePaint);
            canvas.drawRect(kRadius, h - kRadius, w - kRadius, h, mAlmostBluePaint);
            canvas.drawCircle(kRadius, h - kRadius, kRadius, mAlmostBluePaint);
            canvas.drawCircle(w - kRadius, h - kRadius, kRadius, mAlmostBluePaint);
            canvas.drawLine(w / 3, 0, w / 3, h, mGrayTextPaint);
            canvas.drawLine(w / 3 * 2, 0, w / 3 * 2, h, mGrayTextPaint);
            canvas.drawLine(0, h / 2, w, h / 2, mGrayTextPaint);
            Paint R1Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.RED1 ?
                    mRedFocusedTextPaint : mGrayTextPaint) : mRedTextPaint;
            Paint R2Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.RED2 ?
                    mRedFocusedTextPaint : mGrayTextPaint) : mRedTextPaint;
            Paint R3Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.RED3 ?
                    mRedFocusedTextPaint : mGrayTextPaint) : mRedTextPaint;
            Paint B1Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.BLUE1 ?
                    mBlueFocusedTextPaint : mGrayTextPaint) : mBlueTextPaint;
            Paint B2Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.BLUE2 ?
                    mBlueFocusedTextPaint : mGrayTextPaint) : mBlueTextPaint;
            Paint B3Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.BLUE3 ?
                    mBlueFocusedTextPaint : mGrayTextPaint) : mBlueTextPaint;
            canvas.drawText(mR1Team, (w / 3 - R1Paint
                    .measureText(mR1Team)) / 2, h / 2 - 16, R1Paint);
            canvas.drawText(mR2Team, w / 3 + (w / 3 - R2Paint
                    .measureText(mR2Team)) / 2, h / 2 - 16, R2Paint);
            canvas.drawText(mR3Team, w / 3 * 2 + (w / 3 - R3Paint
                    .measureText(mR3Team)) / 2, h / 2 - 16, R3Paint);
            canvas.drawText(mB1Team, (w / 3 - B1Paint
                    .measureText(mB1Team)) / 2, h - 16, B1Paint);
            canvas.drawText(mB2Team, w / 3 + (w / 3 - B2Paint
                    .measureText(mB2Team)) / 2, h - 16, B2Paint);
            canvas.drawText(mB3Team, w / 3 * 2 + (w / 3 - B3Paint
                    .measureText(mB3Team)) / 2, h - 16, B3Paint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension((int) (mMinimumWidth * 3 + kPad * 6), 120);
        }
    }
}
