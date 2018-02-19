package sample.skeleton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import sample.common.BackCell
import sample.common.HostAnimation

/**
 * Created by Administrator on 2018/2/15.
 */

class ScreenHost(ctx:Context,val backCell: BackCell):
        RelativeLayout(ctx),DragPinchRawDriver,AnkoLogger {
    override val loggerTag: String
        get() = "_FH"

    var hostId: Any = "None"
    private val animationManager: ScreenAnimation
    //private val moveHandle: AdvMoveHanle
    //private val moveHandle: EditMoveHandle
    private val moveHandle:DefaultEditFrame
    private var visX: Int
    private var visY: Int
    private var shockX: Float
    private var shockY: Float

    init {
        setWillNotDraw(false)
        visX = 0
        visY = 0
        shockX = 0F
        shockY = 0F
        animationManager = ScreenAnimation(this)

        /*moveHandle = EditMoveHandle(ctx).apply {
            setupLayout(this@ScreenHost)
        }*/
        /*moveHandle = AdvMoveHanle(ctx).apply {
            setupLayout(this@ScreenHost)
        }*/

        moveHandle = DefaultEditFrame(ctx,{e: MoveHander ->
            info { e.lastPt }
        }).apply {
            setupLayout(this@ScreenHost, Rect(100,100,400,600))
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.YELLOW)
        //canvas.translate(0F, height.toFloat()/2)
        backCell.draw(canvas, visX, visY)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        info { "onSizeChanged: ($w,$h)" }
        backCell.apply {
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
    override fun moveTo(x: Int, y: Int) {
        visX = x
        visY = y
        shockX = visX.toFloat()
        shockY = visY.toFloat()
        //info { "moveTo:($visX,$visY)" }
        //moveHandle.dump(x,y)
        moveHandle.moveOffset(shockX, shockY)
        reDraw()
    }

    override fun moveOffset(dx: Float, dy: Float) {
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
        //info { "moveOffset:$hostId($visX,$visY)" }
        moveHandle.moveOffset(shockX, shockY)
        reDraw()
    }

    override fun moveVelocity(velocityX: Float, velocityY: Float) {
        val rect = backCell.range
        animationManager.startFlingAnimation(visX, visY,
                -velocityX.toInt(), -velocityY.toInt(),
                rect.left, rect.right, rect.top, rect.bottom)
    }

    override fun stopFling() {
        animationManager.stopFling()
    }

    override val host: View
        get() = this

    override var canUse: Boolean = true

    override var isFollow: Boolean = false

    override var isDownSource: Boolean = false

    override fun preProcess() {
        info { "$hostId,PreProcess" }
    }

    override fun doubleClickAction(event: MotionEvent): Boolean {
        info { "$hostId,doubleClick" }
        return true
    }

    override fun clickAction(event: MotionEvent): Boolean {
        if (moveHandle.showing) moveHandle.hide()
        else moveHandle.show()
        //moveHandle.show()
        return true
    }

    //惯性操作被中断或结束（做一些其他操作）
    override fun flingEndAction() {
        info { "$hostId,flingEnd" }
        //moveHandle.hideDelayed()
        //比如预处理
        //...
        //比如影藏某些显示
        //...
    }

    override fun scrollEndAction(event: MotionEvent) {
        info { "$hostId,scrollEnd" }
    }


    /*override fun hiting(event: MotionEvent): Boolean {
        return super.hiting(event)
    }*/
}