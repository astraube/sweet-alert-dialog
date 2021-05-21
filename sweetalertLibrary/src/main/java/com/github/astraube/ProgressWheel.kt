package com.github.astraube.sweetalertdialog

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.os.SystemClock
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import com.github.astraube.sweetalertdialog.extensions.toDp
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A Material style progress wheel, compatible up to 2.2.
 * Todd Davies' Progress Wheel https://github.com/Todd-Davies/ProgressWheel
 *
 * @author Nico Hormazábal
 *
 *
 * Licensed under the Apache License 2.0 license see:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
class ProgressWheel : View {
    
    private val TAG = ProgressWheel::class.java.simpleName
    
    private val barLength = 16
    private val barMaxLength = 270
    private val pauseGrowingTime: Long = 200

    /**
     * *********
     * DEFAULTS *
     * **********
     */
    //Sizes (with defaults in DP)
    private var circleRadius = 28
    private var barWidth = 4
    private var rimWidth = 4
    private var fillRadius = false
    private var timeStartGrowing = 0.0
    private var barSpinCycleTime = 460.0
    private var barExtraLength = 0f
    private var barGrowingFromFront = true
    private var pausedTimeWithoutGrowing: Long = 0

    //Colors (with defaults)
    private var barColor = -0x56000000
    private var rimColor = 0x00FFFFFF

    //Paints
    private val barPaint = Paint()
    private val rimPaint = Paint()

    //Rectangles
    private var circleBounds = RectF()

    //Animation
    //The amount of degrees per second
    private var spinSpeed = 230.0f
    private val spinSpeedDivider = 360.0f

    //private float spinSpeed = 120.0f;
    // The last time the spinner was animated
    private var lastTimeAnimated: Long = 0
    private var linearProgress = false
    private var mProgress = 0.0f
    private var mTargetProgress = 0.0f

    /**
     * Check if the wheel is currently spinning
     */
    var isSpinning = false
        private set
    private var callback: ProgressCallback? = null
    private var shouldAnimate = false

    /**
     * The constructor for the ProgressWheel
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel))
        setAnimationEnabled()
    }

    /**
     * The constructor for the ProgressWheel
     */
    constructor(context: Context?) : super(context) {
        setAnimationEnabled()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setAnimationEnabled() {
        val currentApiVersion = Build.VERSION.SDK_INT
        val animationValue: Float
        animationValue = if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE, 1f
            )
        } else {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE, 1f
            )
        }
        shouldAnimate = animationValue != 0f
    }

    //----------------------------------
    //Setting up stuff
    //----------------------------------
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidth = circleRadius + this.paddingLeft + this.paddingRight
        val viewHeight = circleRadius + this.paddingTop + this.paddingBottom
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // Measure Width
        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                viewWidth.coerceAtMost(widthSize)
            }
            else -> {
                //Be whatever you want
                viewWidth
            }
        }

        // Measure Height
        val height: Int = if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            viewHeight.coerceAtMost(heightSize)
        } else {
            //Be whatever you want
            viewHeight
        }
        setMeasuredDimension(width, height)
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupBounds(w, h)
        setupPaints()
        invalidate()
    }

    /**
     * Set the properties of the paints we're using to
     * draw the progress wheel
     */
    private fun setupPaints() {
        barPaint.color = barColor
        barPaint.isAntiAlias = true
        barPaint.style = Paint.Style.STROKE
        barPaint.strokeWidth = barWidth.toFloat()
        rimPaint.color = rimColor
        rimPaint.isAntiAlias = true
        rimPaint.style = Paint.Style.STROKE
        rimPaint.strokeWidth = rimWidth.toFloat()
    }

    /**
     * Set the bounds of the component
     */
    private fun setupBounds(layout_width: Int, layout_height: Int) {
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        circleBounds = if (!fillRadius) {
            // Width should equal to Height, find the min value to setup the circle
            val minValue =
                (layout_width - paddingLeft - paddingRight).coerceAtMost(layout_height - paddingBottom - paddingTop)
            val circleDiameter = minValue.coerceAtMost(circleRadius * 2 - barWidth * 2)

            // Calc the Offset if needed for centering the wheel in the available space
            val xOffset =
                (layout_width - paddingLeft - paddingRight - circleDiameter) / 2 + paddingLeft
            val yOffset =
                (layout_height - paddingTop - paddingBottom - circleDiameter) / 2 + paddingTop
            RectF(
                (xOffset + barWidth).toFloat(),
                (yOffset + barWidth).toFloat(),
                (xOffset + circleDiameter - barWidth).toFloat(),
                (yOffset + circleDiameter - barWidth).toFloat()
            )
        } else {
            RectF(
                (paddingLeft + barWidth).toFloat(),
                (paddingTop + barWidth).toFloat(),
                (layout_width - paddingRight - barWidth).toFloat(),
                (layout_height - paddingBottom - barWidth).toFloat()
            )
        }
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private fun parseAttributes(a: TypedArray) {
        // We transform the default values from DIP to pixels
        val metrics = context.resources.displayMetrics
        barWidth = context.toDp(barWidth, metrics).toInt()
        rimWidth = context.toDp(rimWidth, metrics).toInt()
        circleRadius = context.toDp(circleRadius, metrics).toInt()
        circleRadius = a.getDimension(R.styleable.ProgressWheel_progressCircleRadius, circleRadius.toFloat()).toInt()
        fillRadius = a.getBoolean(R.styleable.ProgressWheel_progressFillRadius, false)
        barWidth = a.getDimension(R.styleable.ProgressWheel_progressBarWidth, barWidth.toFloat()).toInt()
        rimWidth = a.getDimension(R.styleable.ProgressWheel_progressRimWidth, rimWidth.toFloat()).toInt()
        val baseSpinSpeed = a.getFloat(R.styleable.ProgressWheel_progressSpinSpeed, spinSpeed / spinSpeedDivider)
        spinSpeed = baseSpinSpeed * 360
        barSpinCycleTime = a.getInt(R.styleable.ProgressWheel_progressBarSpinCycleTime, barSpinCycleTime.toInt()).toDouble()
        barColor = a.getColor(R.styleable.ProgressWheel_progressBarColor, barColor)
        rimColor = a.getColor(R.styleable.ProgressWheel_progressRimColor, rimColor)
        linearProgress = a.getBoolean(R.styleable.ProgressWheel_progressLinearProgress, false)
        if (a.getBoolean(R.styleable.ProgressWheel_progressProgressIndeterminate, false)) {
            spin()
        }
        // Recycle
        a.recycle()
    }

    fun setCallback(progressCallback: ProgressCallback?) {
        callback = progressCallback
        if (!isSpinning) {
            runCallback()
        }
    }

    //----------------------------------
    //Animation stuff
    //----------------------------------
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(circleBounds, 360f, 360f, false, rimPaint)
        var mustInvalidate = false
        if (!shouldAnimate) {
            return
        }
        if (isSpinning) {
            //Draw the spinning bar
            mustInvalidate = true
            val deltaTime = SystemClock.uptimeMillis() - lastTimeAnimated
            val deltaNormalized = deltaTime * spinSpeed / 1000.0f
            updateBarLength(deltaTime)
            mProgress += deltaNormalized
            if (mProgress > 360) {
                mProgress -= 360f

                // A full turn has been completed
                // we run the callback with -1 in case we want to
                // do something, like changing the color
                runCallback(-1.0f)
            }
            lastTimeAnimated = SystemClock.uptimeMillis()
            var from = mProgress - 90
            var length = barLength + barExtraLength
            if (isInEditMode) {
                from = 0f
                length = 135f
            }
            canvas.drawArc(circleBounds, from, length, false, barPaint)
        } else {
            val oldProgress = mProgress
            if (mProgress != mTargetProgress) {
                //We smoothly increase the progress bar
                mustInvalidate = true
                val deltaTime =
                    (SystemClock.uptimeMillis() - lastTimeAnimated).toFloat() / 1000
                val deltaNormalized = deltaTime * spinSpeed
                mProgress = Math.min(mProgress + deltaNormalized, mTargetProgress)
                lastTimeAnimated = SystemClock.uptimeMillis()
            }
            if (oldProgress != mProgress) {
                runCallback()
            }
            var offset = 0.0f
            var progress = mProgress
            if (!linearProgress) {
                val factor1 = 1.0f
                val factor2 = 2.0f
                offset = (factor1 - (factor1 - mProgress / spinSpeedDivider.toDouble()).pow(factor2 * factor2.toDouble())).toFloat() * spinSpeedDivider
                progress = (factor1 - (factor1 - mProgress / spinSpeedDivider.toDouble()).pow(factor2.toDouble())).toFloat() * spinSpeedDivider
            }
            if (isInEditMode) {
                progress = 360f
            }
            canvas.drawArc(circleBounds, offset - 90, progress, false, barPaint)
        }
        if (mustInvalidate) {
            invalidate()
        }
    }

    override fun onVisibilityChanged(
        changedView: View,
        visibility: Int
    ) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            lastTimeAnimated = SystemClock.uptimeMillis()
        }
    }

    private fun updateBarLength(deltaTimeInMilliSeconds: Long) {
        if (pausedTimeWithoutGrowing >= pauseGrowingTime) {
            timeStartGrowing += deltaTimeInMilliSeconds.toDouble()
            if (timeStartGrowing > barSpinCycleTime) {
                // We completed a size change cycle
                // (growing or shrinking)
                timeStartGrowing -= barSpinCycleTime
                //if(barGrowingFromFront) {
                pausedTimeWithoutGrowing = 0
                //}
                barGrowingFromFront = !barGrowingFromFront
            }
            val distance =
                Math.cos((timeStartGrowing / barSpinCycleTime + 1) * Math.PI).toFloat() / 2 + 0.5f
            val destLength = (barMaxLength - barLength).toFloat()
            if (barGrowingFromFront) {
                barExtraLength = distance * destLength
            } else {
                val newLength = destLength * (1 - distance)
                mProgress += barExtraLength - newLength
                barExtraLength = newLength
            }
        } else {
            pausedTimeWithoutGrowing += deltaTimeInMilliSeconds
        }
    }

    /**
     * Reset the count (in increment mode)
     */
    fun resetCount() {
        mProgress = 0.0f
        mTargetProgress = 0.0f
        invalidate()
    }

    /**
     * Turn off spin mode
     */
    fun stopSpinning() {
        isSpinning = false
        mProgress = 0.0f
        mTargetProgress = 0.0f
        invalidate()
    }

    /**
     * Puts the view on spin mode
     */
    fun spin() {
        lastTimeAnimated = SystemClock.uptimeMillis()
        isSpinning = true
        invalidate()
    }

    private fun runCallback(progress: Float) {
        if (callback != null) {
            callback!!.onProgressUpdate(progress)
        }
    }

    private fun runCallback() {
        if (callback != null) {
            val normalizedProgress =
                (mProgress * 100 / spinSpeedDivider).roundToInt().toFloat() / 100
            callback!!.onProgressUpdate(normalizedProgress)
        }
    }

    /**
     * Set the progress to a specific value,
     * the bar will be set instantly to that value
     *
     * @param progress the progress between 0 and 1
     */
    fun setInstantProgress(progress: Float) {
        var value: Float = progress
        if (isSpinning) {
            mProgress = 0.0f
            isSpinning = false
        }
        val factor1 = 1.0f
        if (value > factor1) {
            value -= factor1
        } else if (value < 0) {
            value = 0f
        }
        if (value == mTargetProgress) {
            return
        }
        mTargetProgress = (value * spinSpeedDivider).coerceAtMost(spinSpeedDivider)
        mProgress = mTargetProgress
        lastTimeAnimated = SystemClock.uptimeMillis()
        invalidate()
    }

    // Great way to save a view's state http://stackoverflow.com/a/7089687/1991053
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = WheelSavedState(superState)

        // We save everything that can be changed at runtime
        ss.mProgress = mProgress
        ss.mTargetProgress = mTargetProgress
        ss.isSpinning = isSpinning
        ss.spinSpeed = spinSpeed
        ss.barWidth = barWidth
        ss.barColor = barColor
        ss.rimWidth = rimWidth
        ss.rimColor = rimColor
        ss.circleRadius = circleRadius
        ss.linearProgress = linearProgress
        ss.fillRadius = fillRadius
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is WheelSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mProgress = state.mProgress
        mTargetProgress = state.mTargetProgress
        isSpinning = state.isSpinning
        spinSpeed = state.spinSpeed
        barWidth = state.barWidth
        barColor = state.barColor
        rimWidth = state.rimWidth
        rimColor = state.rimColor
        circleRadius = state.circleRadius
        linearProgress = state.linearProgress
        fillRadius = state.fillRadius
        lastTimeAnimated = SystemClock.uptimeMillis()
    }

    /**
     * @return the current progress between 0.0 and 1.0,
     *  If we are currently in the right position
     *  we set again the last time animated so the
     *  animation starts smooth from here
     * Set the progress to a specific value,
     * the bar will smoothly animate until that value
     *
     * @param progress the progress between 0 and 1
     */
    var progress: Float = mProgress
        get() = (if (isSpinning) -1f else mProgress / spinSpeedDivider)
        set(progress) {
            field = progress
            if (isSpinning) {
                mProgress = 0.0f
                isSpinning = false
                runCallback()
            }
            val factor1 = 1.0f
            if (field > factor1) {
                field -= factor1
            } else if (field < 0) {
                field = 0f
            }
            if (field == mTargetProgress) {
                return
            }

            // If we are currently in the right position
            // we set again the last time animated so the
            // animation starts smooth from here
            if (mProgress == mTargetProgress) {
                lastTimeAnimated = SystemClock.uptimeMillis()
            }
            mTargetProgress = (field * spinSpeedDivider).coerceAtMost(spinSpeedDivider)
            invalidate()
        }
    //----------------------------------
    //Getters + setters
    //----------------------------------

    /**
     * Sets the determinate progress mode
     *
     * @param isLinear if the progress should increase linearly
     */
    fun setLinearProgress(isLinear: Boolean) {
        linearProgress = isLinear
        if (!isSpinning) {
            invalidate()
        }
    }

    /**
     * @return the radius of the wheel in pixels
     */
    fun getCircleRadius(): Int {
        return circleRadius
    }

    /**
     * Sets the radius of the wheel
     *
     * @param circleRadius the expected radius, in pixels
     */
    fun setCircleRadius(circleRadius: Int) {
        this.circleRadius = circleRadius
        if (!isSpinning) {
            invalidate()
        }
    }

    /**
     * @return the width of the spinning bar
     */
    fun getBarWidth(): Int {
        return barWidth
    }

    /**
     * Sets the width of the spinning bar
     *
     * @param barWidth the spinning bar width in pixels
     */
    fun setBarWidth(barWidth: Int) {
        this.barWidth = barWidth
        if (!isSpinning) {
            invalidate()
        }
    }

    /**
     * @return the color of the spinning bar
     */
    fun getBarColor(): Int {
        return barColor
    }

    /**
     * Sets the color of the spinning bar
     *
     * @param barColor The spinning bar color
     */
    fun setBarColor(barColor: Int) {
        this.barColor = barColor
        setupPaints()
        if (!isSpinning) {
            invalidate()
        }
    }

    /**
     * @return the color of the wheel's contour
     */
    fun getRimColor(): Int {
        return rimColor
    }

    /**
     * Sets the color of the wheel's contour
     *
     * @param rimColor the color for the wheel
     */
    fun setRimColor(rimColor: Int) {
        this.rimColor = rimColor
        setupPaints()
        if (!isSpinning) {
            invalidate()
        }
    }

    /**
     * @return the base spinning speed, in full circle turns per second
     * (1.0 equals on full turn in one second), this value also is applied for
     * the smoothness when setting a progress
     */
    fun getSpinSpeed(): Float {
        return spinSpeed / spinSpeedDivider
    }

    /**
     * Sets the base spinning speed, in full circle turns per second
     * (1.0 equals on full turn in one second), this value also is applied for
     * the smoothness when setting a progress
     *
     * @param spinSpeed the desired base speed in full turns per second
     */
    fun setSpinSpeed(spinSpeed: Float) {
        this.spinSpeed = spinSpeed * spinSpeedDivider
    }

    /**
     * @return the width of the wheel's contour in pixels
     */
    fun getRimWidth(): Int {
        return rimWidth
    }

    /**
     * Sets the width of the wheel's contour
     *
     * @param rimWidth the width in pixels
     */
    fun setRimWidth(rimWidth: Int) {
        this.rimWidth = rimWidth
        if (!isSpinning) {
            invalidate()
        }
    }

    interface ProgressCallback {
        /**
         * Method to call when the progress reaches a value
         * in order to avoid float precision issues, the progress
         * is rounded to a float with two decimals.
         *
         * In indeterminate mode, the callback is called each time
         * the wheel completes an animation cycle, with, the progress value is -1.0f
         *
         * @param progress a double value between 0.00 and 1.00 both included
         */
        fun onProgressUpdate(progress: Float)
    }

    internal class WheelSavedState : BaseSavedState {
        var mProgress: Float = 0f
        var mTargetProgress: Float = 0f
        var isSpinning: Boolean = false
        var spinSpeed: Float = 0f
        var barWidth: Int = 0
        var barColor: Int = 0
        var rimWidth: Int = 0
        var rimColor: Int = 0
        var circleRadius: Int = 0
        var linearProgress: Boolean = false
        var fillRadius: Boolean = false

        constructor(superState: Parcelable?) : super(superState) {}

        private constructor(superState: Parcel) : super(superState) {
            mProgress = superState.readFloat()
            mTargetProgress = superState.readFloat()
            isSpinning = superState.readByte().toInt() != 0
            spinSpeed = superState.readFloat()
            barWidth = superState.readInt()
            barColor = superState.readInt()
            rimWidth = superState.readInt()
            rimColor = superState.readInt()
            circleRadius = superState.readInt()
            linearProgress = superState.readByte().toInt() != 0
            fillRadius = superState.readByte().toInt() != 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(mProgress)
            out.writeFloat(mTargetProgress)
            out.writeByte((if (isSpinning) 1 else 0).toByte())
            out.writeFloat(spinSpeed)
            out.writeInt(barWidth)
            out.writeInt(barColor)
            out.writeInt(rimWidth)
            out.writeInt(rimColor)
            out.writeInt(circleRadius)
            out.writeByte((if (linearProgress) 1 else 0).toByte())
            out.writeByte((if (fillRadius) 1 else 0).toByte())
        }

        companion object {
            //required field that makes Parcelables from a Parcel
            @JvmField val CREATOR: Parcelable.Creator<WheelSavedState> =
                object : Parcelable.Creator<WheelSavedState> {
                    override fun createFromParcel(value: Parcel): WheelSavedState? {
                        return WheelSavedState(value)
                    }

                    override fun newArray(size: Int): Array<WheelSavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }
}