package sample.skeleton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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
    private val animationManager: HostAnimation
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
        animationManager = HostAnimation(this, this::moveTo, this::movingEnd)
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
    private inline fun moveTo(x: Int, y: Int) {
        visX = x
        visY = y
        shockX = visX.toFloat()
        shockY = visY.toFloat()
        info { "moveTo:($visX,$visY)" }
        reDraw()
    }

    private inline fun movingEnd() {
        info { "movingEnd" }
    }

    override fun scrollEndAction(event: MotionEvent) {
        info { "scrollEnd" }
    }

    override fun moveOffset(dx: Float, dy: Float) {
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
        info { "moveOffset:$hostId($visX,$visY)" }
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

    override var isDownSource: Boolean = false

    override fun preProcess() {
        info { "PreProcess" }
    }

    override fun doubleClickAction(event: MotionEvent): Boolean {
        info { event }
        info { "${super.hiting(event)},$hostId,($width,$height)" }
        return true
    }



    /*override fun hiting(event: MotionEvent): Boolean {
        return super.hiting(event)
    }*/
}