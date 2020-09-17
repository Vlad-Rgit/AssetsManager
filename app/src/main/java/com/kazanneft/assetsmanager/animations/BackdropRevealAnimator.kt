package com.kazanneft.assetsmanager.animations

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.kazanneft.assetsmanager.R
import com.kazanneft.assetsmanager.utils.getActionBarSize
import com.kazanneft.assetsmanager.utils.getNavigationButtonsHeight

/**
 * Animation to translate the frontLayer and reveal backLayer
 */
class BackdropRevealAnimator(
    frontLayer: View,
    activityHeight: Int) {

    var isRevealed = false
        private set

    private val animator: ObjectAnimator

    init {

        val windowManager = frontLayer.context.getSystemService(Context.WINDOW_SERVICE)
                as WindowManager

        val point = Point()

        windowManager.defaultDisplay.getSize(point)

        val actionBarHeight = getActionBarSize(frontLayer.context.theme)

        val navButtonsHeight = getNavigationButtonsHeight(frontLayer.context)

        val headerHeight = frontLayer.context
            .resources.getDimension(R.dimen.assets_front_header_hight)

        val target = point.y - actionBarHeight - navButtonsHeight - headerHeight


        animator = ObjectAnimator.ofFloat(
            frontLayer,
            "translationY",
            target
        ).apply {
            duration = 550
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun reveal() {
        animator.cancel()
        animator.start()
        isRevealed = true
    }

    fun unreveal(){
        animator.cancel()
        animator.reverse()
        isRevealed = false
    }


}