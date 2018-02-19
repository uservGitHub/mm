package sample.skeleton

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View

/**
 * Created by Administrator on 2018/2/18.
 */
interface HitingHandle{
    val lastPt:PointF
    val target:View
    val meView:View
    fun hiting(event: MotionEvent):Boolean
    fun hitingMove(event: MotionEvent, superEvent:(e:MotionEvent)->Boolean):Boolean{
        when(event.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN->{
                if (hiting(event)) {
                    //lastPt.x = event.rawX - target.x
                    lastPt.y = event.rawY - meView.y
                    return true
                }
            }
            MotionEvent.ACTION_MOVE ->{
                //target.x = event.rawX - lastPt.x
                meView.y = event.rawY - lastPt.y
                return true
            }
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP->{
                return true
            }
        }
        return superEvent(event)
    }
}