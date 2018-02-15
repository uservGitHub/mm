package sample.skeleton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.view.MotionEvent
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.common.BackCell
import sample.common.HostAnimation

/**
 * Created by work on 2018/2/14.
 */

/*
class Host(ctx: Context,val childId:String): RelativeLayout(ctx), DragPinchDriver,AnkoLogger {
    override val loggerTag: String
        get() = "_FH"
    private val animationManager: HostAnimation
    private val backManager: BackCell
    private var visX:Int
    private var visY:Int
    private var shockX:Float
    private var shockY:Float
    init {
        setWillNotDraw(false)
        visX = 0
        visY = 0
        shockX = 0F
        shockY = 0F
        backManager = BackCell().apply {
            //disabledEndToEnd(0,0)
        }
        animationManager = HostAnimation(this, this::moveTo, this::movingEnd)

        onClick {
            info { childId }
        }
    }
    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.YELLOW)
        //canvas.translate(0F, height.toFloat()/2)
        backManager.draw(canvas, visX, visY)
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        info { "onSizeChanged: ($w,$h)" }
        backManager.apply {
            visWidth = w
            visHeight = h
        }
    }
    override fun computeScroll() {
        super.computeScroll()
        if (isInEditMode) {
            return
        }
        animationManager.computeFling()
    }
    private inline fun reDraw() = invalidate()
    private inline fun moveTo(x: Int, y:Int){
        visX = x
        visY = y
        shockX = visX.toFloat()
        shockY = visY.toFloat()
        info { "moveTo:($visX,$visY)" }
        reDraw()
    }

    override fun clickConfirm(id:String,event: MotionEvent) {
        info { "clickConfirm: $id" }
    }
    override fun moveOffset(dx:Float, dy:Float){
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
        info { "moveOffset:$childId($visX,$visY)" }
        reDraw()
    }
    private inline fun movingEnd(){
        info { "movingEnd" }
    }
    private inline fun scrollEnd(){
        info { "scrollEnd" }
    }
    private inline fun currentPt() = Point(visX, visY)
    private inline fun ptRange() = backManager.range
    fun toggleEndToEnd(){
        if (backManager.endToEnd){
            val pt = backManager.disabledEndToEnd(visX, visY)
            visX = pt.x
            visY = pt.y
            shockX = visX.toFloat()
            shockY = visY.toFloat()
        }else{
            backManager.enabledEndToEnd()
        }
        reDraw()
    }
}*/
