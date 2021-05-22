package com.github.astraube.sweetalertdialog

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * Created by @author André Straube on 22/05/2021
 */
class ProgressWheelSavedState : View.BaseSavedState {
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

    constructor() : this(null) {}
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
        @JvmField val CREATOR: Parcelable.Creator<ProgressWheelSavedState> =
            object : Parcelable.Creator<ProgressWheelSavedState> {
                override fun createFromParcel(value: Parcel): ProgressWheelSavedState {
                    return ProgressWheelSavedState(value)
                }

                override fun newArray(size: Int): Array<ProgressWheelSavedState?> {
                    return arrayOfNulls(size)
                }
            }
    }
}