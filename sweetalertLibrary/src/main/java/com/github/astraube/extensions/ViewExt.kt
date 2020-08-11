package com.github.astraube.extensions

import android.view.View
import androidx.annotation.ColorRes


inline var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

infix fun View.color(@ColorRes id: Int) = when {
    isAtLeastMarshmallow() -> resources.getColor(id, null)else -> resources.getColor(id)
}