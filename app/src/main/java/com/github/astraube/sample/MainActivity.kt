package com.github.astraube.sample

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.astraube.SweetAlertDialog
import com.github.astraube.SweetAlertType
import com.github.astraube.extensions.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var i = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        custom_view.setOnClickListener(this)
        basic_test.setOnClickListener(this)
        under_text_test.setOnClickListener(this)
        error_text_test.setOnClickListener(this)
        success_text_test.setOnClickListener(this)
        warning_confirm_test.setOnClickListener(this)
        warning_cancel_test.setOnClickListener(this)
        custom_img_test.setOnClickListener(this)
        progress_dialog.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.custom_view -> {
                val sad = SweetAlertDialog.Builder(this, SweetAlertType.CUSTOM_VIEW_TYPE)
                    .title("Custom View Dialog")
                    .customView(R.layout.custom_view_dialog)
                    .buildShow()

                sad.getCustomView()?.findViewById<TextView>(R.id.textView)?.let {
                    it.text = "Hi, i'm custom view dialog!"
                }
            }
            R.id.basic_test -> {
                // default title "Here's a message!"
                val sad = SweetAlertDialog(this)
                sad.setCancelable(true)
                sad.setCanceledOnTouchOutside(true)
                sad.show()
            }
            R.id.under_text_test -> SweetAlertDialog(this)
                .setContentText("It's pretty, isn't it?")
                .setDialogBackground(R.drawable.sample_sweet_dialog_background)
                .show()
            R.id.error_text_test -> SweetAlertDialog.Builder(this)
                .title("Oops...")
                .content("Something went wrong!")
                .buildShow()
            R.id.success_text_test -> SweetAlertDialog(this, SweetAlertType.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("You clicked the button!")
                .show()
            R.id.warning_confirm_test -> SweetAlertDialog(this, SweetAlertType.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(object : SweetAlertDialog.OnSweetListener {
                    override fun onClick(dialog: SweetAlertDialog) {
                        dialog.setTitleText("Deleted!")
                            .setContentText("Your imaginary file has been deleted!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertType.SUCCESS_TYPE)
                    }
                })
                .show()
            R.id.warning_cancel_test -> SweetAlertDialog(this, SweetAlertType.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setCancelText("No,cancel plx!")
                .setConfirmText("Yes,delete it!")
                .setConfirmBackground(R.drawable.sample_sweet_button_background)
                .setCancelBackground(R.drawable.sample_sweet_button_2_background)
                .setCancelClickListener(object : SweetAlertDialog.OnSweetListener {
                    override fun onClick(dialog: SweetAlertDialog) {
                        dialog.setTitleText("Cancelled!")
                            .setContentText("Your imaginary file is safe :)")
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertType.ERROR_TYPE)

                        // or you can new a SweetAlertDialog to show
                        /* dialog.dismiss();
                            SweetAlertDialog(SampleActivity.this, SweetAlertType.ERROR_TYPE)
                                    .setTitleText("Cancelled!")
                                    .setContentText("Your imaginary file is safe :)")
                                    .setConfirmText("OK")
                                    .show();*/
                    }
                })
                .setConfirmClickListener(object : SweetAlertDialog.OnSweetListener {
                    override fun onClick(dialog: SweetAlertDialog) {
                        dialog.setTitleText("Deleted!")
                            .setContentText("Your imaginary file has been deleted!")
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertType.SUCCESS_TYPE)
                    }
                })
                .show()
            R.id.custom_img_test -> SweetAlertDialog(this, SweetAlertType.CUSTOM_IMAGE_TYPE)
                .setTitleText("Sweet!")
                .setContentText("Here's a custom image.")
                .setCustomImage(R.drawable.custom_img)
                .show()
            R.id.progress_dialog -> {
                val pDialog = SweetAlertDialog(this, SweetAlertType.PROGRESS_TYPE)
                pDialog.setTitleText("Loading")
                pDialog.show()
                pDialog.setCancelable(false)

                object : CountDownTimer(800 * 7, 800) {
                    override fun onTick(millisUntilFinished: Long) {
                        // you can change the progress bar color by ProgressHelper every 800 millis
                        i++
                        when (i) {
                            0 -> pDialog.progressHelper.barColor =
                                color(R.color.blue_btn_bg_color)
                            1 -> pDialog.progressHelper.barColor =
                                color(R.color.material_deep_teal_50)
                            2 -> pDialog.progressHelper.barColor =
                                color(R.color.success_stroke_color)
                            3 -> pDialog.progressHelper.barColor =
                                color(R.color.material_deep_teal_20)
                            4 -> pDialog.progressHelper.barColor =
                                color(R.color.material_blue_grey_80)
                            5 -> pDialog.progressHelper.barColor =
                                color(R.color.warning_stroke_color)
                            6 -> pDialog.progressHelper.barColor =
                                color(R.color.success_stroke_color)
                        }
                    }

                    override fun onFinish() {
                        i = -1
                        pDialog.setTitleText("Success!")
                            .setConfirmText("OK")
                            .changeAlertType(SweetAlertType.SUCCESS_TYPE)
                    }
                }.start()
            }
        }
    }
}