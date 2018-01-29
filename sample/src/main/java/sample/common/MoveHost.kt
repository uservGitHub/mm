package sample.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by work on 2018/1/25.
 */

class FirstHost(ctx:Context):RelativeLayout(ctx),AnkoLogger {
    override val loggerTag: String
        get() = "_FH"
    private val dragPinchManager: DragPinchManager
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
            endToEnd = false
            enableEndToEnd()
            setOnEndToEndListener { reDraw() }
        }
        animationManager = HostAnimation(this, this::moveTo, this::movingEnd)
        dragPinchManager = DragPinchManager(animationManager,
                this::scrollEnd,this::currentPt,this::ptRange,this::moveOffset).apply {
            enable()
        }
    }
    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.WHITE)
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
        reDraw()
    }
    private inline fun moveOffset(dx:Float, dy:Float){
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
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
}

class MoveHost(ctx:Context):RelativeLayout(ctx),AnkoLogger{
    override val loggerTag: String
        get() = "_MH"
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
        moveDriver = MoveDelta(this,moveAnimation,this::moveOffset,this::getPosition,this::getRange)
        moveDriver.enable()
        shockX = 0F
        shockY = 0F
        visX = 0
        visY = 0
    }
    private inline fun moveOffset(deltaX:Float, deltaY:Float){
        shockX += deltaX
        shockY += deltaY
        visX = shockX.toInt()//.rem(backManager.T)
        visY = shockY.toInt()
        reDraw()
    }
    private inline fun getPosition()=Point(visX, visY)
    private inline fun getRange()=Point(backManager.beg, backManager.end-1)
    private inline fun moveTo(x:Int,y:Int){
        visX = x//.rem(backManager.T)
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
        if (isInEditMode) {
            return
        }
        moveAnimation.computeFling()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        info { "onSizeChanged" }
        super.onSizeChanged(w, h, oldw, oldh)
    }
}