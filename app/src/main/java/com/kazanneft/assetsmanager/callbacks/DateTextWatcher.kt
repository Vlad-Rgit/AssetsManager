package com.kazanneft.assetsmanager.callbacks

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.kazanneft.assetsmanager.utils.tryParseDate

class DateTextWatcher
    (val textLayout: TextInputLayout): TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            if(tryParseDate(it) != null){
                textLayout.error = null
            }
        }
    }

}