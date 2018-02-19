package sample.utils

import android.view.MotionEvent
import android.view.View

/**
 * Created by Administrator on 2018/2/18.
 */

class SubViewUtils {
    companion object {
        //是否命中（在view内）,首先要命中父控件
        fun hiting(view: View, event: MotionEvent, offset: Int = 0): Boolean {
            val pt = IntArray(2, { 0 })
            view.getLocationOnScreen(pt)
            val left = pt[0] - offset
            val top = pt[1] - offset
            val right = left + view.measuredWidth + offset
            val bottom = top + view.measuredHeight + offset
            val x = event.rawX
            val y = event.rawY
            if (x > left && x < right && y > top && y < bottom) {
                return true
            }
            return false
        }
    }
}