Sweet Alert Dialog
===================
SweetAlert for Android, a beautiful and clever alert dialog

**Attention!!!**
This is a re-reading in Kotlin of the library [SweetAlert](https://github.com/pedant/sweet-alert-dialog)

Inspired by JavaScript [SweetAlert](http://tristanedwards.me/sweetalert)

## ScreenShot
![image](https://raw.githubusercontent.com/astraube/sweet-alert-dialog-kotlin/master/images/change_type.gif)

## Setup
The simplest way to use SweetAlertDialog is to add the library as aar dependency to your build.

## Usage

show material progress

    val pDialog: SweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
    pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
    pDialog.setTitleText("Loading")
    pDialog.setCancelable(false)
    pDialog.show()

![image](https://raw.githubusercontent.com/astraube/sweet-alert-dialog-kotlin/master/images/play_progress.gif)

You can customize progress bar dynamically with materialish-progress methods via **SweetAlertDialog.progressHelper**:
- resetCount()
- isSpinning()
- spin()
- stopSpinning()
- getProgress()
- setProgress(float progress)
- setInstantProgress(float progress)
- getCircleRadius()
- setCircleRadius(int circleRadius)
- getBarWidth()
- setBarWidth(int barWidth)
- getBarColor()
- setBarColor(int barColor)
- getRimWidth()
- setRimWidth(int rimWidth)
- getRimColor()
- setRimColor(int rimColor)
- getSpinSpeed()
- setSpinSpeed(float spinSpeed)

thanks to the project [materialish-progress](https://github.com/pnikosis/materialish-progress) and [@croccio](https://github.com/croccio) participation.

more usages about progress, please see the sample.

A basic message：

    SweetAlertDialog(this)
        .setTitleText("Here's a message!")
        .show()

A title with a text under：

    SweetAlertDialog(this)
        .setTitleText("Here's a message!")
        .setContentText("It's pretty, isn't it?")
        .show()

A error message：

    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
        .setTitleText("Oops...")
        .setContentText("Something went wrong!")
        .show()

A warning message：

    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        .setTitleText("Are you sure?")
        .setContentText("Won't be able to recover this file!")
        .setConfirmText("Yes,delete it!")
        .show()

A success message：

    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
        .setTitleText("Good job!")
        .setContentText("You clicked the button!")
        .show()

A message with a custom icon：

    SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        .setTitleText("Sweet!")
        .setContentText("Here's a custom image.")
        .setCustomImage(R.drawable.custom_img)
        .show()

Bind the listener to confirm button：

    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        .setTitleText("Are you sure?")
        .setContentText("Won't be able to recover this file!")
        .setConfirmText("Yes,delete it!")
        .setConfirmClickListener(object : SweetAlertDialog.OnSweetListener {
            override fun onClick(dialog: SweetAlertDialog) {
                sDialog.dismissWithAnimation()
            }
        })
        .show()

Show the cancel button and bind listener to it：

    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        .setTitleText("Are you sure?")
        .setContentText("Won't be able to recover this file!")
        .setCancelText("No,cancel plx!")
        .setConfirmText("Yes,delete it!")
        .showCancelButton(true)
        .setCancelClickListener(object : SweetAlertDialog.OnSweetListener {
            override fun onClick(dialog: SweetAlertDialog) {
                sDialog.cancel()
            }
        })
        .show()

**Change** the dialog style upon confirming：

    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        .setTitleText("Are you sure?")
        .setContentText("Won't be able to recover this file!")
        .setConfirmText("Yes,delete it!")
        .setConfirmClickListener(object : SweetAlertDialog.OnSweetListener {
            override fun onClick(dialog: SweetAlertDialog) {
                sDialog
                    .setTitleText("Deleted!")
                    .setContentText("Your imaginary file has been deleted!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            }
        })
        .show()

[more android tech shares: andrestraube.com.br](http://www.andrestraube.com.br)

## License

    The MIT License (MIT)

    Copyright (c) 2020 Andre Straube(http://andrestraube.com.br)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

