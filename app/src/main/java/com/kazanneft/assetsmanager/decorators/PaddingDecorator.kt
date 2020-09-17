package com.kazanneft.assetsmanager.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingDecorator(val mLeft: Int,
                       val mTop: Int,
                       val mRight: Int,
                       val mBottom: Int)
    : RecyclerView.ItemDecoration()
{

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        outRect.apply {
            left = mLeft
            top = mTop
            right = mRight
            bottom = mBottom
        }
    }

}