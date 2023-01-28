package com.codecoy.ensicocasino

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.text.DecimalFormat
import java.text.NumberFormat

class DigitCardFormatWatcher(
    val et_filed: EditText,
    var format: DecimalFormat = DecimalFormat()
) : TextWatcher {

    var processed = ""

    init {
        format.isDecimalSeparatorAlwaysShown = false
    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(editable: Editable?) {

        val initial = editable.toString()

        if (et_filed == null) return
        if (initial.isEmpty()) return
        val cleanString = initial.replace(",", "")


        processed = if(cleanString.endsWith('.'))
            (format.format(cleanString.toDouble())).plus('.')
        else
            (format.format(cleanString.toDouble()))

        //Remove the listener

        //Remove the listener
        et_filed.removeTextChangedListener(this)

        //Assign processed text

        //Assign processed text
        et_filed.setText(processed)

        try {
            et_filed.setSelection(processed.length)
        } catch (e: Exception) {
            // TODO: handle exception
        }

        //Give back the listener

        //Give back the listener
        et_filed.addTextChangedListener(this)

    }
}