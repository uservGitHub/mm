package sample.common

import android.content.Context
import android.graphics.*
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import sample.utils.BmpUtils
import sample.utils.OcrUtils

/**
 * Created by work on 2018/1/25.
 */

class TextHost(ctx: Context):RelativeLayout(ctx),AnkoLogger {
    override val loggerTag: String
        get() = "_TH"
    var textBmp: Bitmap
        private set
    val textList: List<String>
    fun nextText() {
        textIndex++
        textIndex = textIndex.rem(textList.size)
        textBmp = BmpUtils.simPdfpage(600, 800, toLines(textList[textIndex]))
        invalidate()
    }
    fun checkLine(){

    }
    private var textIndex = 0
    private inline fun toLines(str:String) = str.split("\n".toRegex())
    init {
        setWillNotDraw(false)
        textList = listOf("第一行字\n第二行字\n第三行字", "123abc只有一行")
        textBmp = BmpUtils.simPdfpage(600, 800, toLines(textList[textIndex]))
    }

    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.LTGRAY)
        canvas.drawBitmap(textBmp,
                Rect(0, 0, textBmp.width, textBmp.height),
                Rect(0, 0, textBmp.width, textBmp.height),
                Paint())
        BmpUtils.drawLineFrame(textBmp, canvas)
    }
}

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
            //disabledEndToEnd(0,0)
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
        info { "moveTo:($visX,$visY)" }
        reDraw()
    }
    internal inline fun moveOffset(dx:Float, dy:Float){
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
        info { "moveOffset:($visX,$visY)" }
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

    //region    外部接口
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
    fun resetBoundary(flag:Boolean){
        dragPinchManager.autoResetBoundary(flag)
        reDraw()
    }
    //endregion
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