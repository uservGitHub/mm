package sample.skeleton

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import sample.common.HostAnimation

/**
 * Created by work on 2018/2/14.
 */
class DragPinchManager(val ctx:Context,
                       val hostList:List<Host>):
        View.OnTouchListener, GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener, AnkoLogger {
    override val loggerTag: String
        get() = "_DPM"
    private var scrolling = false
    private var enabled = false
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, this)
        hostList.forEach {
            it.setOnTouchListener(this)
        }
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        info { "confirmedSource: ${e}" }
        return false
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!enabled) {
            return false
        }
        var retVal = false
        //retVal = scaleGestureDetector.onTouchEvent(event)
        retVal = gestureDetector.onTouchEvent(event) || retVal
        if (event.action == MotionEvent.ACTION_UP) {
            if (scrolling) {
                scrolling = false
                onScrollEnd(event)
            }
        }
        return retVal
    }
    private fun onScrollEnd(event: MotionEvent) {
        //region    过界回滚
        if (true) {
            /*val pt = currentLeftTop()
            val frame = ptRange()
            val endX = if (pt.x < frame.left) frame.left
            else if (pt.x > frame.right) frame.right
            else pt.x
            val endY = if (pt.y < frame.top) frame.top
            else if (pt.y > frame.bottom) frame.bottom
            else pt.y
            if (pt.x != endX || pt.y != endY) {
                animation.startXYAnimation(pt.x, pt.y, endX, endY)
            }*/
        }
        //endregion
        //scrollEnd()
    }

    override fun onDown(e: MotionEvent): Boolean {
        //animation.stopFling()
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        scrolling = true
        //moveOffset(distanceX, distanceY)
        hostList.forEach {
            it.moveOffset(distanceX, distanceY)
        }
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
/*        val pt = currentLeftTop()
        val frame = ptRange()
        animation.startFlingAnimation(pt.x, pt.y, -velocityX.toInt(), -velocityY.toInt(),
                frame.left, frame.right, frame.top, frame.bottom)*/
        return true
    }

    override fun onShowPress(e: MotionEvent?) = Unit
    override fun onLongPress(e: MotionEvent?) = Unit
    override fun onSingleTapUp(e: MotionEvent?) = false


}