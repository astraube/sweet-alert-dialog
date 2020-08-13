package com.github.astraube.sweetalertdialog.extensions

import android.text.Html
import android.widget.TextView


/**
 * Insere uma String com formatacao HTML num TextView
 */
infix fun TextView.textHtml(body: String) {
    if (isAtLeastNougat()) {
        this.text = Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT);
    } else {
        this.text = Html.fromHtml(body);
    }
}

/**
 * Insere uma String com formatacao HTML num TextView
 *
 * titleTag: H1, H2, H3, H4, H5, H6
 * contentTag: p, span
 */
fun TextView.textHtml(title: String, content: String, titleTag: String = "h2", contentTag: String = "span") {
    val body = "<$titleTag>$title</$titleTag><$contentTag>${content}</$contentTag>"
    if (isAtLeastNougat()) {
        this.text = Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT);
    } else {
        this.text = Html.fromHtml(body);
    }
}