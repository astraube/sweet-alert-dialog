package com.github.astraube.extensions

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes

/**
 * @author Andre Straube
 * Created on 11/08/2020.
 */
/*
fun <T, R> T?.notNull(block: (it: T) -> R) {
    if (this != null) block(this)
}

inline fun <T, R> whenNotNull(input: T?, block: (T)->R): R? {
    return input?.let(block)
}
*/
fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()
fun String?.isNotNullOrBlank() = !this.isNullOrBlank()

infix fun Context.color(@ColorRes id: Int) = when {
    isAtLeastMarshmallow() -> resources.getColor(id, null) else -> resources.getColor(id)
}

fun Context.dpToPx(dp: Float): Float = this.valueToPixels(TypedValue.COMPLEX_UNIT_DIP, dp)
fun Context.dpToSp(dp: Float): Float = this.valueToPixels(TypedValue.COMPLEX_UNIT_SP, dp)
fun Context.dpToPt(dp: Float): Float = this.valueToPixels(TypedValue.COMPLEX_UNIT_PT, dp)
fun Context.dpToIn(dp: Float): Float = this.valueToPixels(TypedValue.COMPLEX_UNIT_IN, dp)
fun Context.dpToMm(dp: Float): Float = this.valueToPixels(TypedValue.COMPLEX_UNIT_MM, dp)

/**
 * Convenience method to convert a value in the given dimension to pixels.
 * @param value
 * @param dimen
 * @return
 */
fun Context.valueToPixels(dimen: Int, value: Float): Float {
    return if (dimen in TypedValue.COMPLEX_UNIT_PX..TypedValue.COMPLEX_UNIT_MM) {
        TypedValue.applyDimension(
            dimen,
            value,
            this.resources.displayMetrics
        )
    } else value
}

/**
 * Delay to execute a task
 */
fun postDelayed(delayMillis: Long, task: () -> Unit) {
    Handler().postDelayed(task, delayMillis)
}

/**
 * Check Version API Android
 */
fun isAtLeastLollipop(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
}
fun isAtLeastMarshmallow(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
}
fun isAtLeastNougat(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
}