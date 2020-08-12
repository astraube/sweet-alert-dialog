package com.github.astraube.sample

import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.ColorRes

/**
 * @author Andre Straube
 * Created on 11/08/2020.
 */
infix fun Context.color(@ColorRes id: Int) = when {
    isAtLeastMarshmallow() -> resources.getColor(id, null) else -> resources.getColor(id)
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