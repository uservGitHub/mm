package sample.common

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.info

/**
 * Created by work on 2018/1/24.
 */

class MoveDelta(val host:View): View.OnTouchListener, GestureDetector.OnGestureListener{
    private var scrolling = false
    private var enabled = false
    private val gestureDetector: GestureDetector
    init {
        gestureDetector = GestureDetector(host.context, this)
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
        hideHandle()
    }
    private fun hideHandle(){

    }

    override fun onDown(e: MotionEvent): Boolean {
        animationManager.stopFling()
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
        info { "not implemented = onShowPress" }
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        scrolling = true
        host.moveOffset(distanceX, distanceY)
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        info { "==onFling" }
        animationManager.startFlingAnimation(500,500,velocityX.toInt(),velocityY.toInt(),
                0,1000,0,1000)
        return true
    }
}