package sample.common

import android.graphics.Point
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

class MoveDelta(val host: ViewGroup, val animation:DeltaAnimation,
                val moveOffset:(deltaX:Float,deltaY:Float)->Unit,
                val position:()->Point):
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
        info { "onScroll Beg:${position()}" }
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
        animation.startFlingAnimation(curPt.x,curPt.y,5000,velocityY.toInt(),
                0,50000,0,0)
        return true
    }
}