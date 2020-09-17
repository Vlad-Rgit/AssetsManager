package com.kazanneft.assetsmanager.bindings

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.kazanneft.assetsmanager.models.Employee



@BindingAdapter("items")
fun <T> setEntries(spinner: Spinner, items: List<T>?) {

    if (items == null) {
        return
    }

    val adapter = ArrayAdapter(
        spinner.context, android.R.layout.simple_spinner_item, items
    )

    adapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item
    )

    spinner.adapter = adapter
}


@BindingAdapter("selectedItem")
fun <T> setSelectedItem(spinner: Spinner, item: T) {

    if (spinner.adapter == null)
        return

    var index = 0

    for (i in 0 until spinner.adapter.count) {
        if (spinner.adapter.getItem(i) == item) {
            index = i
        }
    }

    spinner.setSelection(index)
}


@BindingAdapter(value = ["selectedAttrChanged", "selectionChanged"], requireAll = false)
fun setListeners(spinner: Spinner, listener: InverseBindingListener, listener2: Runnable?) {

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            listener.onChange()
            listener2?.run()
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            listener.onChange()
            listener2?.run()
        }
    }
}

@InverseBindingAdapter(attribute = "selectedItem", event = "selectedAttrChanged")
fun <T> getSelectedItem(spinner: Spinner): T {
    return spinner.selectedItem as T
}
