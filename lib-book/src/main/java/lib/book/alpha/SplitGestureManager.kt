package lib.book.alpha

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import lib.book.belta.BaseView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by work on 2018/4/19.
 * 二分手势管理
 */

class SplitGestureManager(ctx:Context,val driver:BaseView):
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        AnkoLogger{
    override val loggerTag: String
        get() = "_SGM"
    private val gestureDetector: GestureDetector
    init {
        //ctx 和 view 可以不重合
        gestureDetector = GestureDetector(ctx, this)
        driver.view.setOnTouchListener(this)
    }

    //region    OnDoubleTapListener
    override fun onDoubleTap(e: MotionEvent): Boolean {
        //不再扩散
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?) = false

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        //info { "(${e.x},${e.y},${driver.view.width})" }
        if (e.x<driver.view.width/2){
            driver.leftClick()
        }else{
            driver.rightClick()
        }
        return true
    }
    //endregion

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (driver.locked) {
            return false
        }
        var retVal = false
        //retVal = scaleGestureDetetor.onTouchEvent(event)
        retVal = gestureDetector.onTouchEvent(event) || retVal
        if (event.action == MotionEvent.ACTION_UP) {
            //结束
            //info { "End" }
        }
        return retVal
    }
    override fun onDown(e: MotionEvent): Boolean {
        //info { "Beg" }
        return true
    }

    override fun onShowPress(e: MotionEvent?) = Unit
    override fun onLongPress(e: MotionEvent?) = Unit
    override fun onSingleTapUp(e: MotionEvent?) = false
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float) = true
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) = true
}
