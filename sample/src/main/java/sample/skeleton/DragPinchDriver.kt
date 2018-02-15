package sample.skeleton

import android.view.MotionEvent
import android.view.View

/**
 * Created by work on 2018/2/14.
 */

interface DrawPinchRawDriver {
    //偏移
    fun moveOffset(dx: Float, dy: Float)

    //加速移动
    fun moveVelocity(velocityX: Float,velocityY:Float)

    //停止惯性操作
    fun stopFling()

    //只做绑定用
    val host: View
    //是否起作用
    val canUse: Boolean get() = false
    //是否随动
    val isFollow: Boolean get() = false
    //是否是按下来源
    var isDownSource: Boolean

    //事件是否在host内
    fun hiting(event: MotionEvent): Boolean {
        if (!canUse) {
            return false
        }
        val pt = IntArray(2, { 0 })
        host.getLocationOnScreen(pt)
        val left = pt[0]
        val top = pt[1]
        val right = left + host.measuredWidth
        val bottom = top + host.measuredHeight
        val x = event.x
        val y = event.y
        if (x > left && x < right && y > top && y < bottom) {
            return true
        }

        return false
    }

    //单击操作
    fun clickAction(event: MotionEvent) = false

    //双击操作
    fun doubleClickAction(event: MotionEvent) = false

    //滚动结束
    fun scrollEndAction(event: MotionEvent):Unit

    //预处理
    fun preProcess()
}
