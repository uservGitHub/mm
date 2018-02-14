package sample.skeleton

import android.view.MotionEvent

/**
 * Created by work on 2018/2/14.
 */

interface DragPinchDriver{
    fun moveOffset(dx:Float, dy:Float)
    fun clickConfirm(id:String,event: MotionEvent)
}