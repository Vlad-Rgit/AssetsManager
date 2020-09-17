package com.kazanneft.assetsmanager.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import com.kazanneft.assetsmanager.animations.BackdropRevealAnimator

fun getActionBarSize(theme: Resources.Theme): Int {

    val tv = TypedValue()

    if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {

        return TypedValue
            .complexToDimensionPixelSize(tv.data, theme.resources.displayMetrics)
    }

    return 0
}


fun getNavigationButtonsHeight(context: Context): Int {
    val resources: Resources = context.getResources()
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}