package com.github.astraube.sweetalertdialog.internal.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes

/**
 * @author Andre Straube
 * Created on 11/08/2020.
 */
internal fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()
internal fun String?.isNotNullOrBlank() = !this.isNullOrBlank()

internal fun Context.inflate(@LayoutRes res: Int, parent: ViewGroup? = null): View {
    return LayoutInflater.from(this).inflate(res, parent, false)
}

internal fun Context.inflate(@LayoutRes res: Int, parent: ViewGroup? = null, attachToRoot: Boolean): View {
    return LayoutInflater.from(this).inflate(res, parent, attachToRoot)
}

@Suppress("DEPRECATION")
internal infix fun Context.color(@ColorRes id: Int) = when {
    isAtLeastMarshmallow() -> resources.getColor(id, null) else -> resources.getColor(id)
}

internal fun Context.toDp(value: Int, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
internal fun Context.toDp(value: Float, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)

internal fun Context.toSp(value: Int, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics)
internal fun Context.toSp(value: Float, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics)

internal fun Context.toPt(value: Int, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_PT, value, metrics)
internal fun Context.toPt(value: Float, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_PT, value, metrics)

internal fun Context.toIn(value: Int, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_IN, value, metrics)
internal fun Context.toIn(value: Float, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_IN, value, metrics)

internal fun Context.toMm(value: Int, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_MM, value, metrics)
internal fun Context.toMm(value: Float, metrics: DisplayMetrics = this.resources.displayMetrics): Float = this.valueToDimension(TypedValue.COMPLEX_UNIT_MM, value, metrics)

/**
 * Converts an unpacked complex data value holding a dimension to its final floating
 * point value. The two parameters <var>unit</var> and <var>value</var>
 * are as in {@link #TypedValue.TYPE_DIMENSION}.
 *
 * @param unit The unit to convert from.
 * @param value The value to apply the unit to.
 * @param metrics Current display metrics to use in the conversion --
 *                supplies display density and scaling information.
 * @return The complex floating point value multiplied by the appropriate
 * metrics depending on its unit.
 */
internal fun Context.valueToDimension(unit: Int, value: Float, metrics: DisplayMetrics = this.resources.displayMetrics): Float {
    return if (unit in TypedValue.COMPLEX_UNIT_PX..TypedValue.COMPLEX_UNIT_MM) {
        TypedValue.applyDimension(
            unit,
            value,
            metrics
        )
    } else value
}
internal fun Context.valueToDimension(unit: Int, value: Int, metrics: DisplayMetrics = this.resources.displayMetrics): Float {
    return this.valueToDimension(unit, value.toFloat(), metrics)
}

/**
 * Delay to execute a task
 */
internal fun postDelayed(delayMillis: Long, task: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(task, delayMillis)
}

/**
 * Check Version API Android
 */
internal fun isAtLeastLollipop(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
}
internal fun isAtLeastMarshmallow(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
}
internal fun isAtLeastNougat(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
}

@SuppressLint("ObsoleteSdkInt")
internal fun isAtLeastGingerbread(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1)
}