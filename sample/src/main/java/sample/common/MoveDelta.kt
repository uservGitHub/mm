package sample.common

import android.graphics.Point
import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by work on 2018/1/24.
 */

class DragPinchManager(val animation: HostAnimation,
                       val scrollEnd: ()->Unit,
                       val currentLeftTop: ()->Point,
                       val ptRange: ()->Rect,
                       val moveOffset: (deltaX: Float, deltaY: Float) -> Unit):
        View.OnTouchListener, GestureDetector.OnGestureListener,AnkoLogger {
    override val loggerTag: String
        get() = "_DPM"
    private var scrolling = false
    private var enabled = false
    private var canAutoReseting = false
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(animation.host.context, this)
        animation.host.setOnTouchListener(this)
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    fun autoResetBoundary(flag:Boolean){
        canAutoReseting = flag
        if (canAutoReseting){
            resetBoundary()
        }
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
    //边界复位
    private fun resetBoundary(){
        if (scrolling){
            return
        }

        val pt = currentLeftTop()
        val frame = ptRange()
        val endX = if (pt.x < frame.left) frame.left
        else if (pt.x > frame.right) frame.right
        else pt.x
        val endY = if (pt.y < frame.top) frame.top
        else if (pt.y > frame.bottom) frame.bottom
        else pt.y
        if (pt.x != endX || pt.y != endY) {
            animation.startXYAnimation(pt.x, pt.y, endX, endY)
        }
    }
    private fun onScrollEnd(event: MotionEvent) {
        //region    过界回滚
        if (canAutoReseting) {
            resetBoundary()
        }
        //endregion
        scrollEnd()
    }

    override fun onDown(e: MotionEvent): Boolean {
        animation.stopFling()
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        scrolling = true
        moveOffset(distanceX, distanceY)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val pt = currentLeftTop()
        val frame = ptRange()
        animation.startFlingAnimation(pt.x, pt.y, -velocityX.toInt(), -velocityY.toInt(),
                frame.left, frame.right, frame.top, frame.bottom)
        return true
    }

    override fun onShowPress(e: MotionEvent?) = Unit
    override fun onLongPress(e: MotionEvent?) = Unit
    override fun onSingleTapUp(e: MotionEvent?) = false


}

class MoveDelta(val host: ViewGroup, val animation:DeltaAnimation,
                val moveOffset:(deltaX:Float,deltaY:Float)->Unit,
                val position:()->Point,
                val range:()->Point):
        View.OnTouchListener, GestureDetector.OnGestureListener,AnkoLogger{
    override val loggerTag: String
        get() = "_SiMMr"
    private var scrolling = false
    private var enabled = false
    private val gestureDetector: GestureDetector
    init {
        gestureDetector = GestureDetector(host.context, this)
        host.setOnTouchListener(this)
    }
    fun enable(){
        enabled = true
    }
    fun disable(){
        enabled = false
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if(!enabled){
            return false
        }
        var retVal = false
        //retVal = scaleGestureDetector.onTouchEvent(event)
        retVal = gestureDetector.onTouchEvent(event) || retVal
        if(event.action == MotionEvent.ACTION_UP){
            if (scrolling){
                scrolling = false
                onScrollEnd(event)
            }
        }
        return retVal
    }
    private fun onScrollEnd(event: MotionEvent){
        info { "onScrollEnd" }
        val pt = position()
        if (pt.x < 0){
            info { pt.x.toFloat() }
            val end = 0F
            animation.startXAnimation(pt.x.toFloat(), end)
        }
        hideHandle()
    }
    private fun hideHandle(){

    }

    override fun onDown(e: MotionEvent): Boolean {
        animation.stopFling()
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
        //info { "not implemented = onShowPress" }
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        scrolling = true
        //info { "onScroll Beg:${position()}" }
        moveOffset(distanceX, distanceY)
        info{ "onScroll End:${position()}" }
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        info { "==onFling" }
        val curPt = position()
        /*animation.startFlingAnimation(curPt.x,curPt.y,velocityX.toInt(),velocityY.toInt(),
                0,0,0,0)*/
        val pt = range()
        animation.startFlingAnimation(curPt.x,curPt.y,-velocityX.toInt(),0,
                pt.x,pt.y,0,0)
        return true
    }
}