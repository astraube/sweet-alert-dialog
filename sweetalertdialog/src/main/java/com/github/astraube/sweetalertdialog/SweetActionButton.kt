package com.github.astraube.sweetalertdialog

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import com.github.astraube.sweetalertdialog.extensions.isNotNullOrBlank
import com.github.astraube.sweetalertdialog.extensions.visible

/**
 * @author Andre Straube
 * Created on 12/08/2020.
 */
data class SweetActionButton constructor(
    val dialog: SweetAlertDialog,
    @IdRes val viewId: Int,
    var listener: SweetAlertDialog.OnSweetListener? = null
): View.OnClickListener {

    constructor(
        dialog: SweetAlertDialog,
        @IdRes viewId: Int,
        listener: SweetAlertDialog.OnSweetListener? = null,
        textButton: String? = null,
        @ColorRes @DrawableRes backgroundResId: Int? = null
    ) : this(dialog, viewId, listener) {
        this.text = textButton
        this.backgroundResource = backgroundResId
    }

    internal val buttonView: AppCompatButton = dialog.findViewById<View>(viewId) as AppCompatButton

    internal var text: String? = null
        set(value) {
            field = value
            if (field.isNotNullOrBlank()) {
                this.buttonView.text = field
            }
        }

    internal var backgroundResource: Int? = null
        set(@DrawableRes @ColorRes value) {
            field = value
            field?.let { bgId ->
                this.buttonView.setBackgroundResource(bgId)
            }
        }

    fun isVisible(visibility: Boolean) {
        this.buttonView.visible = visibility
    }

    fun setText(text: String? = null) {
        this.text = text
    }

    fun setText(@StringRes resId: Int? = null) {
        if (resId != null) {
            this.text = dialog.context.getString(resId)
        }
    }

    override fun onClick(v: View) {
        if (listener != null) {
            listener?.onClick(dialog)
        } else {
            dialog.dismissWithAnimation()
        }
    }

    init {
        this.buttonView.setOnClickListener(this)
    }
}