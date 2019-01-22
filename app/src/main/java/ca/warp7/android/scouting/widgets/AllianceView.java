package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.constants.RobotPosition;
import ca.warp7.android.scouting.model2018.MatchWithAllianceItem;

/**
 * @since v0.4.2
 */

public class AllianceView extends View {

    private static final float kPad = 24;
    private static final int kRadius = 16;

    private final Paint mAlmostRedPaint = new Paint();
    private final Paint mAlmostBluePaint = new Paint();
    private final Paint mAlmostBlackTextPaint = new Paint();
    private final Paint mGrayTextPaint = new Paint();
    private final Paint mRedTextPaint = new Paint();
    private final Paint mBlueTextPaint = new Paint();
    private final Paint mRedBoldTextPaint = new Paint();
    private final Paint mBlueBoldTextPaint = new Paint();

    private float mMinimumWidth;

    private String mR1 = "Red 1";
    private String mR2 = "Red 2";
    private String mR3 = "Red 3";
    private String mB1 = "Blue 1";
    private String mB2 = "Blue 2";
    private String mB3 = "Blue 3";

    private int mFocusPosition = RobotPosition.RED1;
    private boolean mShouldFocus = false;


    public AllianceView(Context context) {
        super(context);
        initPaints();
    }

    public AllianceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void setTextPaintStyle(Paint paint, boolean bold) {
        paint.setTextSize(32);
        paint.setAntiAlias(true);
        paint.setTypeface(bold ? Typeface.create(Typeface.SANS_SERIF,
                Typeface.BOLD) : Typeface.SANS_SERIF);
    }

    private void initPaints() {
        Context context = getContext();
        mAlmostRedPaint.setColor(ContextCompat.getColor(context, R.color.colorAlmostRed));
        mAlmostBluePaint.setColor(ContextCompat.getColor(context, R.color.colorAlmostBlue));

        mAlmostBlackTextPaint.setColor(ContextCompat.getColor(context, R.color.colorAlmostBlack));
        mGrayTextPaint.setColor(ContextCompat.getColor(context, R.color.colorGray));
        mRedTextPaint.setColor(ContextCompat.getColor(context, R.color.colorRed));
        mBlueTextPaint.setColor(ContextCompat.getColor(context, R.color.colorBlue));
        mRedBoldTextPaint.setColor(ContextCompat.getColor(context, R.color.colorRed));
        mBlueBoldTextPaint.setColor(ContextCompat.getColor(context, R.color.colorBlue));

        setTextPaintStyle(mAlmostBlackTextPaint, false);
        setTextPaintStyle(mGrayTextPaint, false);
        setTextPaintStyle(mRedTextPaint, false);
        setTextPaintStyle(mBlueTextPaint, false);
        setTextPaintStyle(mRedBoldTextPaint, true);
        setTextPaintStyle(mBlueBoldTextPaint, true);

        mMinimumWidth = mRedBoldTextPaint.measureText("8888");
    }

    public void setDataFromScheduledMatchItem(MatchWithAllianceItem matchItem) {
        mR1 = String.valueOf(matchItem.getTeamAt(0));
        mR2 = String.valueOf(matchItem.getTeamAt(1));
        mR3 = String.valueOf(matchItem.getTeamAt(2));
        mB1 = String.valueOf(matchItem.getTeamAt(3));
        mB2 = String.valueOf(matchItem.getTeamAt(4));
        mB3 = String.valueOf(matchItem.getTeamAt(5));
        mFocusPosition = matchItem.getFocusPosition();
        mShouldFocus = matchItem.shouldFocus();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getMeasuredWidth();
        final int h = getMeasuredHeight();
        canvas.drawRect(0, kRadius, w, h / 2f, mAlmostRedPaint);
        canvas.drawRect(kRadius, 0, w - kRadius, kRadius, mAlmostRedPaint);
        canvas.drawCircle(kRadius, kRadius, kRadius, mAlmostRedPaint);
        canvas.drawCircle(w - kRadius, kRadius, kRadius, mAlmostRedPaint);
        canvas.drawRect(0, h / 2f, w, h - kRadius, mAlmostBluePaint);
        canvas.drawRect(kRadius, h - kRadius, w - kRadius, h, mAlmostBluePaint);
        canvas.drawCircle(kRadius, h - kRadius, kRadius, mAlmostBluePaint);
        canvas.drawCircle(w - kRadius, h - kRadius, kRadius, mAlmostBluePaint);
        canvas.drawLine(w / 3f, 0, w / 3f, h, mGrayTextPaint);
        canvas.drawLine(w / 3f * 2, 0, w / 3f * 2, h, mGrayTextPaint);
        canvas.drawLine(0, h / 2f, w, h / 2f, mGrayTextPaint);
        Paint R1Paint = mShouldFocus ? (mFocusPosition == RobotPosition.RED1 ?
                mRedBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint R2Paint = mShouldFocus ? (mFocusPosition == RobotPosition.RED2 ?
                mRedBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint R3Paint = mShouldFocus ? (mFocusPosition == RobotPosition.RED3 ?
                mRedBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint B1Paint = mShouldFocus ? (mFocusPosition == RobotPosition.BLUE1 ?
                mBlueBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint B2Paint = mShouldFocus ? (mFocusPosition == RobotPosition.BLUE2 ?
                mBlueBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint B3Paint = mShouldFocus ? (mFocusPosition == RobotPosition.BLUE3 ?
                mBlueBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        int fromBottom = 10;
        canvas.drawText(mR1, (w / 3f - R1Paint
                .measureText(mR1)) / 2f, h / 2f - fromBottom, R1Paint);
        canvas.drawText(mR2, w / 3f + (w / 3f - R2Paint
                .measureText(mR2)) / 2f, h / 2f - fromBottom, R2Paint);
        canvas.drawText(mR3, w / 3f * 2 + (w / 3f - R3Paint
                .measureText(mR3)) / 2f, h / 2f - fromBottom, R3Paint);
        canvas.drawText(mB1, (w / 3f - B1Paint
                .measureText(mB1)) / 2f, h - fromBottom, B1Paint);
        canvas.drawText(mB2, w / 3f + (w / 3f - B2Paint
                .measureText(mB2)) / 2f, h - fromBottom, B2Paint);
        canvas.drawText(mB3, w / 3f * 2 + (w / 3f - B3Paint
                .measureText(mB3)) / 2f, h - fromBottom, B3Paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension((int) (mMinimumWidth * 3 + kPad * 6), 84);
    }
}
