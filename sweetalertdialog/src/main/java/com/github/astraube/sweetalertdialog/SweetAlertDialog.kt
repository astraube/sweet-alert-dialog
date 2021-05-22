package com.github.astraube.sweetalertdialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.Transformation
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.github.astraube.sweetalertdialog.OptAnimationLoader.loadAnimation
import com.github.astraube.sweetalertdialog.SweetAlertType.*
import com.github.astraube.sweetalertdialog.databinding.SweetAlertDialogBinding
import com.github.astraube.sweetalertdialog.databinding.SweetDialogProgressBinding
import com.github.astraube.sweetalertdialog.internal.extensions.*

class SweetAlertDialog constructor(
    context: Context,
    val builderInfo: Builder
) : Dialog(context, R.style.SweetAlert_Dialog) {


    interface OnSweetListener {
        fun onClick(dialog: SweetAlertDialog)
    }

    constructor(context: Context): this(
        context,
        Builder(context)
    )
    constructor(context: Context, type: SweetAlertType): this(
        context,
        Builder(context).type(type)
    )

    private var mDialogContainer: ViewGroup? = null
    private var mDialogBg: Int? = null

    private val mModalInAnim: AnimationSet?
    private val mModalOutAnim: AnimationSet?
    private val mOverlayOutAnim: Animation
    private val mErrorInAnim: Animation?
    private val mErrorXInAnim: AnimationSet?
    private val mSuccessLayoutAnimSet: AnimationSet?
    private val mSuccessBowAnim: Animation?
    private var mTitleTextView: AppCompatTextView? = null
    private var mContentTextView: AppCompatTextView? = null
    private var mErrorFrame: FrameLayout? = null
    private var mSuccessFrame: FrameLayout? = null
    private var mProgressFrame: FrameLayout? = null
    private var mSuccessTick: SuccessTickView? = null
    private var mErrorX: ImageView? = null
    private var mSuccessLeftMask: View? = null
    private var mSuccessRightMask: View? = null
    private var mCloseButton: ImageButton? = null
    private var mCustomImage: ImageView? = null
    private var mCustomViewContainer: FrameLayout? = null
    private var mWarningFrame: FrameLayout? = null
    private var mCloseFromCancel = false
    private var mConfirmAction: SweetActionButton? = null
    private var mCancelAction: SweetActionButton? = null
    val progressHelper: ProgressHelper

    data class Builder(
        val context: Context,
        var type: SweetAlertType = NORMAL_TYPE
    ) {

        var isCancelable: Boolean = true
        var isCanceledOnTouchOutside: Boolean = true
        var isShowCloseButton: Boolean = false
        var isShowContentText: Boolean = false
        var isShowCancel: Boolean = false // cancel button

        var title: String? = null
            private set
        var content: String? = null
            private set
        var customImgDrawable: Drawable? = null
            private set
        var customView: View? = null
            private set
        @LayoutRes
        var customViewResource: Int? = null
            private set
        var confirmText: String? = null
            private set
        @ColorRes @DrawableRes
        var confirmBgResource: Int? = null
            private set
        var confirmListener: OnSweetListener? = null
            private set
        var isShowConfirm: Boolean = true
            private set
        var cancelText: String? = null
            private set
        @ColorRes @DrawableRes
        var cancelBgResource: Int? = null
            private set
        var cancelListener: OnSweetListener? = null
            private set

        fun type(type: SweetAlertType) = apply { this.type = type }
        fun title(text: String? = null) = apply { this.title = text }
        fun title(@StringRes resId: Int) = apply { this.title(context.getString(resId)) }
        fun content(text: String? = null) = apply { this.content = text }
        fun content(@StringRes resId: Int) = apply { this.content(context.getString(resId)) }
        fun isShowContentText(visibility: Boolean) = apply { this.isShowContentText = visibility }
        fun customImgDrawable(drawable: Drawable? = null) = apply { this.customImgDrawable = drawable }
        fun customImgDrawable(@DrawableRes resId: Int) = apply { this.customImgDrawable = ContextCompat.getDrawable(context, resId) }
        fun customView(view: View) = apply { this.customView = view }
        fun customView(@LayoutRes resId: Int) = apply { this.customViewResource = resId }

        fun confirmText(text: String? = null) = apply {
            this.confirmText = text
            if (text.isNotNullOrBlank())
                this.isShowConfirm =true
        }

        fun confirmText(@StringRes resId: Int) = apply { this.confirmText(context.getString(resId)) }
        fun confirmBackground(@ColorRes @DrawableRes resId: Int? = null) = apply { this.confirmBgResource = resId }
        fun confirmListener(listener: OnSweetListener?) = apply { this.confirmListener = listener }
        fun cancelText(text: String? = null) = apply {
            this.cancelText = text
            if (text.isNotNullOrBlank())
                this.isShowCancel = true
        }
        fun cancelText(@StringRes resId: Int) = apply { this.cancelText(context.getString(resId)) }
        fun cancelBackground(@ColorRes @DrawableRes resId: Int? = null) = apply { this.cancelBgResource = resId }
        fun cancelListener(listener: OnSweetListener? = null) = apply { this.cancelListener = listener }

        fun isCancelable(visibility: Boolean) = apply { this.isCancelable = visibility }
        fun isCanceledOnTouchOutside(visibility: Boolean) = apply { this.isCanceledOnTouchOutside = visibility }
        fun isShowCloseButton(visibility: Boolean) = apply { this.isShowCloseButton = visibility }
        fun isShowCancel(visibility: Boolean) = apply { this.isShowCancel = visibility }
        fun isShowConfirm(visibility: Boolean) = apply { this.isShowConfirm = visibility }

        fun build() = SweetAlertDialog(context, this)
        fun buildShow() = build().apply { this.show() }
    }

    private lateinit var rootView: View
    //private lateinit var binding: SweetAlertDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = SweetAlertDialogBinding.inflate(layoutInflater).also {
//            setContentView(it.root)
//        }
//
//        mDialogContainer = binding.dialogContainer
//        mTitleTextView = binding.tvTitle
//        mContentTextView = binding.tvContent


        setContentView(R.layout.sweet_alert_dialog)

        window?.run {
            rootView = this.decorView.findViewById(android.R.id.content)
        }

        mDialogContainer = findViewById<View>(R.id.dialogContainer) as ViewGroup
        mTitleTextView = findViewById<View>(R.id.tvTitle) as AppCompatTextView
        mContentTextView = findViewById<View>(R.id.tvContent) as AppCompatTextView
        mErrorFrame = findViewById<View>(R.id.errorFrame) as FrameLayout
        mErrorX = mErrorFrame!!.findViewById<View>(R.id.errorImg) as ImageView
        mSuccessFrame = findViewById<View>(R.id.successFrame) as FrameLayout
        mProgressFrame = findViewById<View>(R.id.progressDialog) as FrameLayout
        mSuccessTick = mSuccessFrame!!.findViewById<View>(R.id.successTick) as SuccessTickView
        mSuccessLeftMask = mSuccessFrame!!.findViewById(R.id.maskLeft)
        mSuccessRightMask = mSuccessFrame!!.findViewById(R.id.maskRight)
        mCloseButton = findViewById<View>(R.id.closeButton) as ImageButton
        mCustomImage = findViewById<View>(R.id.customImage) as ImageView
        mCustomViewContainer = findViewById<View>(R.id.customView) as FrameLayout
        mWarningFrame = findViewById<View>(R.id.warningFrame) as FrameLayout
        progressHelper.progressWheel = findViewById<View>(R.id.progressWheel) as ProgressWheel

        mConfirmAction = SweetActionButton(this, R.id.btnConfirm, builderInfo.confirmListener, builderInfo.confirmText, builderInfo.confirmBgResource).also {
            it.isVisible(builderInfo.isShowConfirm)
        }
        mCancelAction = SweetActionButton(this, R.id.btnCancel, builderInfo.cancelListener, builderInfo.cancelText, builderInfo.cancelBgResource).also {
            it.isVisible(builderInfo.isShowCancel)
        }
        setDialogBackground(mDialogBg)
        setTitleText(builderInfo.title)
        setContentText(builderInfo.content)
        changeAlertType(builderInfo.type, true)
    }

    private fun restore() {
        mCustomImage!!.visibility = View.GONE
        mErrorFrame!!.visibility = View.GONE
        mSuccessFrame!!.visibility = View.GONE
        mWarningFrame!!.visibility = View.GONE
        mProgressFrame!!.visibility = View.GONE
        mCustomViewContainer!!.visibility = View.GONE
        mConfirmAction?.isVisible(true)
        mConfirmAction?.backgroundResource = R.drawable.sweet_button_blue_background
        mErrorFrame!!.clearAnimation()
        mErrorX!!.clearAnimation()
        mSuccessTick!!.clearAnimation()
        mSuccessLeftMask!!.clearAnimation()
        mSuccessRightMask!!.clearAnimation()
    }

    private fun playAnimation() {
        if (builderInfo.type == ERROR_TYPE) {
            mErrorFrame!!.startAnimation(mErrorInAnim)
            mErrorX!!.startAnimation(mErrorXInAnim)
        } else if (builderInfo.type == SUCCESS_TYPE) {
            mSuccessTick!!.startTickAnim()
            mSuccessRightMask!!.startAnimation(mSuccessBowAnim)
        }
    }

    private fun changeAlertType(alertType: SweetAlertType, fromCreate: Boolean) {
        builderInfo.type = alertType
        // call after created views
        if (window != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore()
            }
            when (alertType) {
                ERROR_TYPE -> {
                    mErrorFrame!!.visibility = View.VISIBLE
                }
                SUCCESS_TYPE -> {
                    mSuccessFrame!!.visibility = View.VISIBLE
                    // initial rotate layout of success mask
                    mSuccessLeftMask!!.startAnimation(mSuccessLayoutAnimSet!!.animations[0])
                    mSuccessRightMask!!.startAnimation(mSuccessLayoutAnimSet.animations[1])
                }
                WARNING_TYPE -> {
                    if (mConfirmAction?.backgroundResource == null)
                        setConfirmBackground(R.drawable.sweet_button_red_background)

                    mWarningFrame!!.visibility = View.VISIBLE
                }
                PROGRESS_TYPE -> {
                    mProgressFrame!!.visibility = View.VISIBLE
                    mConfirmAction?.isVisible(false)
                }
                PROGRESS_TYPE_BINDING_TESTE_1 -> {
                    rootView.let {
                        SweetDialogProgressBinding.inflate(
                            LayoutInflater.from(this.context), (it as ViewGroup), true
                        )
                    }
                }
                PROGRESS_TYPE_BINDING_TESTE_2 -> {
                    rootView.let {
                        (it as ViewGroup).removeAllViews()

                        SweetDialogProgressBinding.inflate(
                            LayoutInflater.from(this.context), it, true
                        )
                    }
                }
                CUSTOM_IMAGE_TYPE -> setCustomImage(builderInfo.customImgDrawable)
                CUSTOM_VIEW_TYPE -> {
                    if (builderInfo.customView != null) {
                        setCustomView(builderInfo.customView!!)
                    } else if (builderInfo.customViewResource != null) {
                        setCustomView(builderInfo.customViewResource!!)
                    }
                }
                else -> {}
            }
            if (!fromCreate) {
                playAnimation()
            }
        }
    }

    fun changeAlertType(alertType: SweetAlertType) {
        changeAlertType(alertType, false)
    }

    fun setDialogBackground(@DrawableRes @ColorRes backgroundResId: Int? = null): SweetAlertDialog {
        return apply {
            mDialogBg = backgroundResId
            mDialogContainer?.let {
                it.setBgRes(backgroundResId)
            }
        }
    }

    override fun show() {
        super.show()
        this.setCloseButton(builderInfo.isShowCloseButton)
    }

    override fun setCancelable(isCancelable: Boolean) {
        super.setCancelable(isCancelable)
        builderInfo.isCancelable = isCancelable
    }

    fun setCancelableAndCloseButton(isCancelable: Boolean, isShowCloseButton: Boolean): SweetAlertDialog = setCloseButton(isShowCloseButton).apply {
        this.setCancelable(isCancelable)
    }

    fun setCloseButton(isShowCloseButton: Boolean): SweetAlertDialog = apply {
        builderInfo.isShowCloseButton = isShowCloseButton
        mCloseButton?.let {
            if (isShowCloseButton) {
                it.visibility = View.VISIBLE
                it.setOnClickListener {
                    this.dismissWithAnimation()
                }
            } else {
                it.visibility = View.GONE
            }
        }
    }

    fun setTitleText(text: String?): SweetAlertDialog = apply {
        builderInfo.title(text)
        mTitleTextView?.let {
            if (text.isNotNullOrBlank()) {
                it.text = text
            }
        }
    }

    fun setTitleText(@StringRes resId: Int): SweetAlertDialog = apply {
        this.setTitleText(this.context.getString(resId))
    }

    fun setCustomImage(drawable: Drawable?): SweetAlertDialog = apply {
        builderInfo.customImgDrawable(drawable)
        mCustomImage?.let { imgView ->
            builderInfo.customImgDrawable?.let {
                imgView.visibility = View.VISIBLE
                imgView.setImageDrawable(it)
            }
        }
    }

    fun setCustomImage(@DrawableRes resId: Int): SweetAlertDialog = apply {
        setCustomImage(ContextCompat.getDrawable(context, resId))
    }

    fun getCustomView(): View? {
        return builderInfo.customView
    }
    fun setCustomView(@LayoutRes resId: Int): SweetAlertDialog = apply {
        builderInfo.customView(resId)
        mCustomViewContainer?.let { container ->
            setCustomView(context.inflate(resId, container))
        }
    }
    fun setCustomView(view: View): SweetAlertDialog = apply {
        builderInfo.customView(view)
        mCustomViewContainer?.let { container ->
            container.visible = true
            container.addView(view)
        }
    }

    fun setContentText(text: String?): SweetAlertDialog = apply {
        builderInfo.content(text)
        mContentTextView?.let {
            if (text.isNotNullOrBlank()) {
                showContentText(true)
                it.text = text
            }
        }
    }
    fun setContentText(@StringRes resId: Int): SweetAlertDialog = apply {
        this.setContentText(this.context.getString(resId))
    }
    fun showContentText(isShow: Boolean): SweetAlertDialog = apply {
        builderInfo.isShowContentText(isShow)
        mContentTextView?.let {
            it.visible = isShow
        }
    }

    fun showCancelButton(isShow: Boolean): SweetAlertDialog = apply {
        this.builderInfo.isShowCancel(isShow)
        this.mCancelAction?.isVisible(isShow)
    }
    fun setCancelBackground(@DrawableRes @ColorRes backgroundResId: Int? = null): SweetAlertDialog = apply {
        this.builderInfo.cancelBackground(backgroundResId)
        this.mCancelAction?.backgroundResource = backgroundResId
    }
    fun setCancelText(text: String?): SweetAlertDialog = apply {
        this.builderInfo.cancelText(text)
        this.mCancelAction?.text = text
        if (text.isNotNullOrBlank()) {
            this.showCancelButton(true)
        }
    }
    fun setCancelText(@StringRes resId: Int): SweetAlertDialog = apply {
        this.setCancelText(this.context.getString(resId))
    }
    fun setCancelClickListener(listener: OnSweetListener?): SweetAlertDialog = apply {
        this.builderInfo.cancelListener(listener)
        this.mCancelAction?.listener = listener
    }

    fun showConfirmButton(isShow: Boolean): SweetAlertDialog = apply {
        this.builderInfo.isShowConfirm(isShow)
        this.mConfirmAction?.isVisible(isShow)
    }
    fun setConfirmBackground(@DrawableRes @ColorRes backgroundResId: Int? = null): SweetAlertDialog = apply {
        this.builderInfo.confirmBackground(backgroundResId)
        this.mConfirmAction?.backgroundResource = backgroundResId
    }
    fun setConfirmText(text: String?): SweetAlertDialog = apply {
        this.builderInfo.confirmText(text)
        this.mConfirmAction?.text = text
        if (text.isNotNullOrBlank()) {
            this.showConfirmButton(true)
        }
    }
    fun setConfirmText(@StringRes resId: Int): SweetAlertDialog = apply {
        this.setConfirmText(this.context.getString(resId))
    }
    fun setConfirmClickListener(listener: OnSweetListener?): SweetAlertDialog = apply {
        this.builderInfo.confirmListener(listener)
        this.mConfirmAction?.listener = listener
    }

    override fun onStart() {
        rootView.startAnimation(mModalInAnim)
        playAnimation()
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    override fun cancel() {
        dismissWithAnimation(true)
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    fun dismissWithAnimation() {
        dismissWithAnimation(false)
    }

    private fun dismissWithAnimation(fromCancel: Boolean) {
        mCloseFromCancel = fromCancel
        mConfirmAction?.buttonView?.startAnimation(mOverlayOutAnim)
        rootView.startAnimation(mModalOutAnim)
    }

    init {
        setCancelable(builderInfo.isCancelable)
        setCanceledOnTouchOutside(builderInfo.isCanceledOnTouchOutside)
        setCloseButton(builderInfo.isShowCloseButton)

        progressHelper = ProgressHelper(context)
        mErrorInAnim = loadAnimation(getContext(), R.anim.sweet_error_frame_in)
        mErrorXInAnim = loadAnimation(getContext(), R.anim.sweet_error_x_in) as AnimationSet?

        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set
        if (isAtLeastGingerbread()) {
            val childAnims = mErrorXInAnim!!.animations
            var idx = 0
            while (idx < childAnims.size) {
                if (childAnims[idx] is AlphaAnimation) {
                    break
                }
                idx++
            }
            if (idx < childAnims.size) {
                childAnims.removeAt(idx)
            }
        }
        mSuccessBowAnim = loadAnimation(getContext(), R.anim.sweet_success_bow_roate)
        mSuccessLayoutAnimSet = loadAnimation(getContext(), R.anim.sweet_success_mask_layout) as AnimationSet
        mModalInAnim = loadAnimation(getContext(), R.anim.sweet_modal_in) as AnimationSet
        mModalOutAnim = loadAnimation(getContext(), R.anim.sweet_modal_out) as AnimationSet
        mModalOutAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                rootView.let {
                   it. visibility = View.GONE
                    it.post {
                        if (mCloseFromCancel) {
                            super@SweetAlertDialog.cancel()
                        } else {
                            super@SweetAlertDialog.dismiss()
                        }
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        // dialog overlay fade out
        mOverlayOutAnim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                window?.let {
                    val wlp = it.attributes
                    wlp.alpha = 1 - interpolatedTime
                    it.attributes = wlp
                }
            }
        }
        mOverlayOutAnim.setDuration(120)
    }
}