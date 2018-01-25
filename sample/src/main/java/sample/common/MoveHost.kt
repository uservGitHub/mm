package sample.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.widget.RelativeLayout

/**
 * Created by work on 2018/1/25.
 */

class MoveHost(ctx:Context):RelativeLayout(ctx){
    private val moveDriver: MoveDelta
    private val moveAnimation: DeltaAnimation
    private val backManager: BackPage
    private var shockX:Float
    private var shockY:Float
    private var visX:Int
    private var visY:Int
    init {
        setWillNotDraw(false)
        backManager = BackPage().apply {
            endToEnd = true
            enable()
            setOnEndToEndListener { this@MoveHost.reDraw() }
        }
        moveAnimation = DeltaAnimation(this,this::moveOffset,this::moveTo)
        moveDriver = MoveDelta(this,moveAnimation,this::moveOffset,this::getPosition)
        moveDriver.enable()
        shockX = 0F
        shockY = 0F
        visX = 0
        visY = 0
    }
    private inline fun moveOffset(deltaX:Float, deltaY:Float){
        shockX += deltaX
        shockY += deltaY
        visX = shockX.toInt()
        visY = shockY.toInt()
        reDraw()
    }
    private inline fun getPosition()=Point(visX, visY)
    private inline fun moveTo(x:Int,y:Int){
        visX = x
        visY = y
        shockX = visX.toFloat()
        shockY = visY.toFloat()
        reDraw()
    }
    fun reDraw() = invalidate()

    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.WHITE)
        canvas.translate(0F, height.toFloat()/2)
        backManager.draw(visX, visX+width,canvas)
    }

    override fun computeScroll() {
        super.computeScroll()
        moveAnimation.computeFling()
    }
}