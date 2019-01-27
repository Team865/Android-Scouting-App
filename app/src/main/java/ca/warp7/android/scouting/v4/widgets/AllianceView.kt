package ca.warp7.android.scouting.v4.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.constants.RobotPosition
import ca.warp7.android.scouting.v4.model.MatchWithAllianceItem

/**
 * @since v0.4.2
 */

class AllianceView : View {

    private val mAlmostRedPaint = Paint()
    private val mAlmostBluePaint = Paint()
    private val mAlmostBlackTextPaint = Paint()
    private val mGrayTextPaint = Paint()
    private val mRedTextPaint = Paint()
    private val mBlueTextPaint = Paint()
    private val mRedBoldTextPaint = Paint()
    private val mBlueBoldTextPaint = Paint()

    private var mMinimumWidth: Float = 0.toFloat()

    private var mR1 = "Red 1"
    private var mR2 = "Red 2"
    private var mR3 = "Red 3"
    private var mB1 = "Blue 1"
    private var mB2 = "Blue 2"
    private var mB3 = "Blue 3"

    private var mFocusPosition = RobotPosition.RED1
    private var mShouldFocus = false


    constructor(context: Context) : super(context) {
        initPaints()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initPaints()
    }

    private fun setTextPaintStyle(paint: Paint, bold: Boolean) {
        paint.textSize = 32f
        paint.isAntiAlias = true
        paint.typeface = if (bold)
            Typeface.create(
                Typeface.SANS_SERIF,
                Typeface.BOLD
            )
        else
            Typeface.SANS_SERIF
    }

    private fun initPaints() {
        val context = context
        mAlmostRedPaint.color = ContextCompat.getColor(context, R.color.colorAlmostRed)
        mAlmostBluePaint.color = ContextCompat.getColor(context, R.color.colorAlmostBlue)

        mAlmostBlackTextPaint.color = ContextCompat.getColor(context, R.color.colorAlmostBlack)
        mGrayTextPaint.color = ContextCompat.getColor(context, R.color.colorGray)
        mRedTextPaint.color = ContextCompat.getColor(context, R.color.colorRed)
        mBlueTextPaint.color = ContextCompat.getColor(context, R.color.colorBlue)
        mRedBoldTextPaint.color = ContextCompat.getColor(context, R.color.colorRed)
        mBlueBoldTextPaint.color = ContextCompat.getColor(context, R.color.colorBlue)

        setTextPaintStyle(mAlmostBlackTextPaint, false)
        setTextPaintStyle(mGrayTextPaint, false)
        setTextPaintStyle(mRedTextPaint, false)
        setTextPaintStyle(mBlueTextPaint, false)
        setTextPaintStyle(mRedBoldTextPaint, true)
        setTextPaintStyle(mBlueBoldTextPaint, true)

        mMinimumWidth = mRedBoldTextPaint.measureText("8888")
    }

    fun setDataFromScheduledMatchItem(matchItem: MatchWithAllianceItem) {
        mR1 = matchItem.getTeamAt(0).toString()
        mR2 = matchItem.getTeamAt(1).toString()
        mR3 = matchItem.getTeamAt(2).toString()
        mB1 = matchItem.getTeamAt(3).toString()
        mB2 = matchItem.getTeamAt(4).toString()
        mB3 = matchItem.getTeamAt(5).toString()
        mFocusPosition = matchItem.focusPosition
        mShouldFocus = matchItem.shouldFocus()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = measuredWidth
        val h = measuredHeight
        canvas.drawRect(0f, kRadius.toFloat(), w.toFloat(), h / 2f, mAlmostRedPaint)
        canvas.drawRect(kRadius.toFloat(), 0f, (w - kRadius).toFloat(), kRadius.toFloat(), mAlmostRedPaint)
        canvas.drawCircle(kRadius.toFloat(), kRadius.toFloat(), kRadius.toFloat(), mAlmostRedPaint)
        canvas.drawCircle((w - kRadius).toFloat(), kRadius.toFloat(), kRadius.toFloat(), mAlmostRedPaint)
        canvas.drawRect(0f, h / 2f, w.toFloat(), (h - kRadius).toFloat(), mAlmostBluePaint)
        canvas.drawRect(
            kRadius.toFloat(),
            (h - kRadius).toFloat(),
            (w - kRadius).toFloat(),
            h.toFloat(),
            mAlmostBluePaint
        )
        canvas.drawCircle(kRadius.toFloat(), (h - kRadius).toFloat(), kRadius.toFloat(), mAlmostBluePaint)
        canvas.drawCircle((w - kRadius).toFloat(), (h - kRadius).toFloat(), kRadius.toFloat(), mAlmostBluePaint)
        canvas.drawLine(w / 3f, 0f, w / 3f, h.toFloat(), mGrayTextPaint)
        canvas.drawLine(w / 3f * 2, 0f, w / 3f * 2, h.toFloat(), mGrayTextPaint)
        canvas.drawLine(0f, h / 2f, w.toFloat(), h / 2f, mGrayTextPaint)
        val R1Paint = if (mShouldFocus)
            if (mFocusPosition == RobotPosition.RED1)
                mRedBoldTextPaint
            else
                mGrayTextPaint
        else
            mAlmostBlackTextPaint
        val R2Paint = if (mShouldFocus)
            if (mFocusPosition == RobotPosition.RED2)
                mRedBoldTextPaint
            else
                mGrayTextPaint
        else
            mAlmostBlackTextPaint
        val R3Paint = if (mShouldFocus)
            if (mFocusPosition == RobotPosition.RED3)
                mRedBoldTextPaint
            else
                mGrayTextPaint
        else
            mAlmostBlackTextPaint
        val B1Paint = if (mShouldFocus)
            if (mFocusPosition == RobotPosition.BLUE1)
                mBlueBoldTextPaint
            else
                mGrayTextPaint
        else
            mAlmostBlackTextPaint
        val B2Paint = if (mShouldFocus)
            if (mFocusPosition == RobotPosition.BLUE2)
                mBlueBoldTextPaint
            else
                mGrayTextPaint
        else
            mAlmostBlackTextPaint
        val B3Paint = if (mShouldFocus)
            if (mFocusPosition == RobotPosition.BLUE3)
                mBlueBoldTextPaint
            else
                mGrayTextPaint
        else
            mAlmostBlackTextPaint
        val fromBottom = 10
        canvas.drawText(
            mR1, (w / 3f - R1Paint
                .measureText(mR1)) / 2f, h / 2f - fromBottom, R1Paint
        )
        canvas.drawText(
            mR2, w / 3f + (w / 3f - R2Paint
                .measureText(mR2)) / 2f, h / 2f - fromBottom, R2Paint
        )
        canvas.drawText(
            mR3, w / 3f * 2 + (w / 3f - R3Paint
                .measureText(mR3)) / 2f, h / 2f - fromBottom, R3Paint
        )
        canvas.drawText(
            mB1, (w / 3f - B1Paint
                .measureText(mB1)) / 2f, (h - fromBottom).toFloat(), B1Paint
        )
        canvas.drawText(
            mB2, w / 3f + (w / 3f - B2Paint
                .measureText(mB2)) / 2f, (h - fromBottom).toFloat(), B2Paint
        )
        canvas.drawText(
            mB3, w / 3f * 2 + (w / 3f - B3Paint
                .measureText(mB3)) / 2f, (h - fromBottom).toFloat(), B3Paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension((mMinimumWidth * 3 + kPad * 6).toInt(), 84)
    }

    companion object {

        private val kPad = 24f
        private val kRadius = 16
    }
}
