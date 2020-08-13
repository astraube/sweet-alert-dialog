package com.github.astraube.sample

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Andre Straube
 * Created on 12/08/2020.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class NumberTextWatcherFormatTest {

    @Test
    fun phoneNumber_test() {
        val phoneNumberMask = NumberTextWatcher.MASK_PHONE_NUMBER
        val phoneNumberTextWatcher = NumberTextWatcher(phoneNumberMask)
        println("-------------> Mask: : $phoneNumberMask")

        val input = StringBuilder()
        val expectedResult = "(012) 34567-8901"
        var result = ""
        println("-------------> expectedResult: ${expectedResult}")

        // mimic typing 10 digits
        for (i in 0 until 12) {
            input.append(i)
            result = mimicTextInput(phoneNumberTextWatcher, result, i.toString()) ?: ""
        }
        println("-------------> input: ${input.toString()}")
        println("-------------> result: ${result}")

        Assert.assertEquals(input.toString(), "0123456789")
        Assert.assertEquals(result, expectedResult)
    }

    @Test
    fun creditCard_test() {
        val creditCardNumberMask = NumberTextWatcher.MASK_CREDIT_CARD
        val creditCardNumberTextWatcher = NumberTextWatcher(creditCardNumberMask)
        println("-------------> Mask: : $creditCardNumberMask")

        val input = StringBuilder()
        val expectedResult = "0123 4567 8901 2345"
        var result = ""
        println("-------------> expectedResult: ${expectedResult}")

        // mimic typing 16 digits
        for (i in 0 until 16) {
            val value = i % 10
            input.append(value)
            result = mimicTextInput(creditCardNumberTextWatcher, result, value.toString()) ?: ""
        }
        println("-------------> input: ${input.toString()}")
        println("-------------> result: ${result}")

        Assert.assertEquals(input.toString(), "0123456789012345")
        Assert.assertEquals(result, expectedResult)
    }

    @Test
    fun dateUs_test() {
        val dateMask = NumberTextWatcher.MASK_AAAA_MM_DD
        val dateTextWatcher = NumberTextWatcher(dateMask)
        println("-------------> Mask: : $dateMask")

        val input = "20200504"
        val expectedResult = "2020/05/04"
        val initialInputValue = ""
        println("-------------> expectedResult: ${expectedResult}")

        val result = mimicTextInput(dateTextWatcher, initialInputValue, input)
        println("-------------> input: ${input.toString()}")
        println("-------------> result: ${result}")

        Assert.assertEquals(result, expectedResult)
    }

    @Test
    fun time_test() {
        val timeMask = NumberTextWatcher.MASK_HH_MM
        val timeTextWatcher = NumberTextWatcher(timeMask)
        println("-------------> Mask: : $timeMask")

        val input = "2020"
        val expectedResult = "20:20"
        val initialInputValue = ""
        println("-------------> expectedResult: ${expectedResult}")

        val result = mimicTextInput(timeTextWatcher, initialInputValue, input)
        println("-------------> input: ${input.toString()}")
        println("-------------> result: ${result}")

        Assert.assertEquals(result, expectedResult)
    }

    @Test
    fun creditCardExpirationDate_test() {
        val creditCardExpirationDateMask = NumberTextWatcher.MASK_MM_DD
        val creditCardExpirationDateTextWatcher = NumberTextWatcher(creditCardExpirationDateMask)
        println("-------------> Mask: : $creditCardExpirationDateMask")

        val input = "1121"
        val expectedResult = "11/21"
        val initialInputValue = ""
        println("-------------> expectedResult: ${expectedResult}")

        val result = mimicTextInput(creditCardExpirationDateTextWatcher, initialInputValue, input)
        println("-------------> input: ${input.toString()}")
        println("-------------> result: ${result}")

        Assert.assertEquals(result, expectedResult)
    }

    private fun mimicTextInput(textWatcher: TextWatcher, initialInputValue: String, input: String): String? {
        textWatcher.beforeTextChanged(initialInputValue, initialInputValue.length, initialInputValue.length, input.length + initialInputValue.length)
        val newText = initialInputValue + input
        textWatcher.onTextChanged(newText, 1, newText.length - 1, 1)
        val editable: Editable = SpannableStringBuilder(newText)
        textWatcher.afterTextChanged(editable)
        return editable.toString()
    }
}