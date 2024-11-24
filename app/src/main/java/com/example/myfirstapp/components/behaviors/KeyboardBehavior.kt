package com.example.myfirstapp.components.behaviors

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class KeyboardBehavior(
    private val rootView: View,
    private val targetView: View
) {

    fun setupKeyboardListener() {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                adjustViewHeight(screenHeight - keypadHeight)
            } else {
                // Keyboard is hidden
                resetViewHeight()
            }
        }
    }

    private fun adjustViewHeight(newHeight: Int) {
        val params = targetView.layoutParams as FrameLayout.LayoutParams
        params.height = newHeight
        targetView.layoutParams = params
    }

    private fun resetViewHeight() {
        val params = targetView.layoutParams as FrameLayout.LayoutParams
        params.height = FrameLayout.LayoutParams.MATCH_PARENT
        targetView.layoutParams = params
    }
}
