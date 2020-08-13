package com.github.astraube.sweetalertdialog.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.view.children


inline var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

infix fun View.color(@ColorRes id: Int) = when {
    isAtLeastMarshmallow() -> resources.getColor(id, null)else -> resources.getColor(id)
}

fun View.setBgRes(@DrawableRes @ColorRes backgroundResId: Int? = null) {
    backgroundResId?.let { bgId ->
        this.setBackgroundResource(bgId)
    }
}

fun View.getAllViews(): List<View> {
    if (this !is ViewGroup || childCount == 0) return listOf(this)

    return children
        .toList()
        .flatMap { it.getAllViews() }
        .plus(this as View)
}