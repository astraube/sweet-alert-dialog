package com.github.astraube.sweetalertdialog.extensions

import android.widget.TextView
import androidx.core.text.HtmlCompat


/**
 * Insere uma String com formatacao HTML num TextView
 */
internal infix fun TextView.textHtml(body: String) {
    this.text = if (isAtLeastNougat()) {
        HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_COMPACT)
    } else {
        // method deprecated at API 24.
        @Suppress("DEPRECATION")
        android.text.Html.fromHtml(body)
    }
}

/**
 * Insere uma String com formatacao HTML num TextView
 *
 * titleTag: H1, H2, H3, H4, H5, H6
 * contentTag: p, span
 */
internal fun TextView.textHtml(title: String, content: String, titleTag: String = "h2", contentTag: String = "span") {
    val body = "<$titleTag>$title</$titleTag><$contentTag>${content}</$contentTag>"
    this.textHtml(body = body)
}