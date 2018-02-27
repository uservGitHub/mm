package sample.end

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.common.BackCell
import sample.utils.BmpUtils

/**
 * Created by work on 2018/2/26.
 * 记录：
 * 1 override fun computeScroll()
 *  invalidate() 时自动调用，
 */

class Verbose{
    companion object {
        private const val TAG = "_ALL"
        fun info(any: Any){
            Log.v(TAG, "$any")
        }
    }
}

//region    AnimationManager
/**
 * startXYAnimation(xFrom, yFrom, xTo, yTo) =>不断刷新，最后结束动画
 * stop() =>取消动画，
 */
class AnimationManager(val moveTo:(x:Float, y:Float)->Unit,
                       val moveEnd:(()->Unit)?=null) {

    private var animation: ValueAnimator? = null

    private var baseX = 0F
    private var baseY = 0F
    private var deltaX = 0F
    private var deltaY = 0F

    fun stop() {
        animation?.apply {
            cancel()
        }
        animation = null
    }

    fun startXYAnimation(xFrom: Float, yFrom: Float, xTo: Float, yTo: Float) {
        stop()
        animation = ValueAnimator.ofFloat(0F, 1F)
        baseX = xFrom
        baseY = yFrom
        deltaX = (xTo - xFrom)
        deltaY = (yTo - yFrom)

        animation?.apply {
            val xyAnimation = XYAnimation()
            interpolator = DecelerateInterpolator()
            addUpdateListener(xyAnimation)
            addListener(xyAnimation)
            setDuration(400)
            start()
        }
    }

    inner class XYAnimation() : AnimatorListenerAdapter(), ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val offset = animation.getAnimatedValue() as Float
            val x = baseX + offset * deltaX
            val y = baseY + offset * deltaY
            moveTo(x, y)
        }

        override fun onAnimationCancel(animation: Animator?) {
            moveEnd?.invoke()
        }

        override fun onAnimationEnd(animation: Animator?) {
            moveEnd?.invoke()
        }
    }
}
//endregion

//region    PinchDriver
interface PinchDriver{
    val host:View
    var isDownSource:Boolean
    var isFollow:Boolean
    val canUse:Boolean
    fun scrollEndAction(e:MotionEvent)
    fun hiting(event: MotionEvent): Boolean {
        if (!canUse) {
            return false
        }
        val pt = IntArray(2, { 0 })
        host.getLocationOnScreen(pt)
        val left = pt[0]
        val top = pt[1]
        val right = left + host.measuredWidth
        val bottom = top + host.measuredHeight
        val x = event.rawX
        val y = event.rawY
        if (x > left && x < right && y > top && y < bottom) {
            return true
        }

        return false
    }
    fun stopMoving()
    fun moveOffset(dx:Float, dy:Float)
    fun moveVelocity(velocityX:Float, velocityY:Float)
    fun doubleClickAction(e: MotionEvent):Boolean
    fun clickAction(e: MotionEvent):Boolean
}
//endregion

//region    DragPinchManager
class DragPinchManager(ctx:Context):
        View.OnTouchListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener{

    private var scrolling = false
    private var scaling = false
    private var enabled = false
    private val hostList = mutableListOf<PinchDriver>()

    private val gestureDetector: GestureDetector
    private val scaleGestureDetetor:ScaleGestureDetector
    //返回值表示是否继续进行子项触发(rawX屏幕坐标，x子控件内的坐标)
    private var doubleClickListener: ((subEvent:MotionEvent)->Boolean)? = null
    //返回值表示是否继续进行子项触发
    private var clickListener: ((subEvent:MotionEvent)->Boolean)? = null


    init {
        gestureDetector = GestureDetector(ctx,this)
        scaleGestureDetetor = ScaleGestureDetector(ctx, this)
    }

    fun addDriver(driver:PinchDriver){
        driver.host.setOnTouchListener(this)
        hostList.add(driver)
    }
    fun removeDriver(driver: PinchDriver){
        driver.host.setOnTouchListener(null)
        hostList.remove(driver)
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }
    fun setDoubleClickListener(listener:(event:MotionEvent)->Boolean){
        doubleClickListener = listener
    }
    fun setClickListener(listener:(event:MotionEvent)->Boolean){
        clickListener = listener
    }

    //region    OnDoubleTapListener
    override fun onDoubleTap(e: MotionEvent): Boolean {
        //如果存在外层事件，优先处理外层事件
        doubleClickListener?.let {
            if(it.invoke(e)){
                return true
            }
        }

        hostList.forEach {
            if (it.hiting(e)){
                it.doubleClickAction(e)
                return true
            }
        }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?) = false

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        //如果存在外层事件，优先处理外层事件
        clickListener?.let {
            if(it.invoke(e)){
                return true
            }
        }
        hostList.forEach {
            if (it.hiting(e)){
                it.clickAction(e)
                return true
            }
        }
        return true
    }
    //endregion

    //region    OnScaleGestureListener
    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!enabled) {
            return false
        }
        var retVal = false
        retVal = scaleGestureDetetor.onTouchEvent(event)
        retVal = gestureDetector.onTouchEvent(event) || retVal
        if (event.action == MotionEvent.ACTION_UP) {
            if (scrolling) {
                scrolling = false
                hostList.forEach {
                    if (it.isDownSource) {
                        it.scrollEndAction(event)
                    } else {
                        if (it.isFollow) {
                            it.scrollEndAction(event)
                        }
                    }
                }
            }
        }
        return retVal
    }

    override fun onDown(e: MotionEvent): Boolean {
        var hasHit = false
        hostList.forEach {
            if (it.hiting(e)){
                if (it.canUse) {
                    it.isDownSource = true
                    it.stopMoving()
                }
            }else{
                it.isDownSource = false
                if (it.canUse && it.isFollow){
                    it.stopMoving()
                }
            }
        }
        return hasHit
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        scrolling = true
        hostList.forEach {
            if (it.isDownSource) {
                it.moveOffset(distanceX, distanceY)
            } else {
                if (it.isFollow) {
                    it.moveOffset(distanceX, distanceY)
                }
            }
        }
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        hostList.forEach {
            if (it.isDownSource) {
                it.moveVelocity(velocityX, velocityY)
            } else {
                if (it.isFollow) {
                    it.moveVelocity(velocityX, velocityY)
                }
            }
        }
        return true
    }

    override fun onShowPress(e: MotionEvent?) = Unit
    override fun onLongPress(e: MotionEvent?) = Unit
    override fun onSingleTapUp(e: MotionEvent?) = false

}
//endregion

//region    六种Hander
enum class HanderType {
    Left, Top, Right, Bottom, LTCorner, RBCorner
}
//endregion

data class Size(val width: Int, val height: Int)

//插件接口
interface AttachMent{
    val showing: Boolean
    fun show()
    fun hide()
    //定位插件起始位置(在父画布中的位置)
    fun locate(x:Float, y:Float)
    //是否包含点(点是否在插件内,插件可能是隐藏状态)
    fun hasPt(x:Float, y:Float):Boolean
    //更新可视点(起点)
    fun upateVisPt(x:Float, y:Float)
    //fun hideDelayed()
    //表示作为一个单独的层安装在view上,frameRect和view属于同坐标系
    fun setupLayout(view: ViewGroup, frameSize:Size)
    //从安装的层上删除
    fun destroyLayout()
}

//根据Hander事件动态生成Hander控制器

//region    HanderView
class HanderView(ctx:Context):FrameLayout(ctx),AttachMent,AnkoLogger {
    override val loggerTag: String
        get() = "_HV"
    companion object {
        //返回尺寸布局
        private inline fun sizeLayout(width: Int, height: Int) = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            this.width = width
            this.height = height
        }

        //重置view的尺寸
        private inline fun View.resize(width: Int, height: Int) {
            this.layoutParams = layoutParams.apply {
                this.width = width
                this.height = height
            }
        }

        private inline fun strokePaint(colorInt: Int, thickness:Float) = Paint().apply {
            color = colorInt
            style  = Paint.Style.STROKE
            strokeWidth = thickness
            strokeCap = Paint.Cap.BUTT
            strokeJoin = Paint.Join.MITER
        }

        private inline fun fillPaint(colorInt: Int) = Paint().apply {
            color = colorInt
            style = Paint.Style.FILL
        }
        private const val SelectColor = Color.RED
        private const val DefaultColor = Color.BLACK
        private const val FillColor = 0x50888888
        /*private inline fun handPath(rect1: RectF,rect2:RectF) = Path().apply {
            addRect(rect1, Path.Direction.CCW)
            addRect(rect2, Path.Direction.CCW)
        }
        private const val Thickness = 40F
        private const val Length = 100F
        private const val HandColor = Color.BLACK
        private val HandPaint = Paint()*/
    }

    //region    Hander
    private val ptLTCorner = PointF(0F, 0F)
    private var hitingLTCorner = false
    private val ptRBCorner = PointF(0F, 0F)
    private var hitingRBCorner = false

    private val sideLength = dip(50).toFloat()
    private val lineLength = dip(100).toFloat()
    private val lineWidth = dip(2).toFloat()
    private val lastPt = PointF(0F, 0F)
    private fun initHandPt(){
        ptLTCorner.apply {
            x =0F
            y = 0F
        }
        ptRBCorner.apply {
            x = size.width.toFloat()
            y = size.height.toFloat()
        }
    }
    //这种内联函数不好
    /*private inline fun testHit(x:Float, y:Float, offset: Float, minX:Float, maxX:Float, minY:Float, maxY:Float):Boolean {
        return x > minX-offset && x < maxX+offset && y > minY-offset && y < maxY+offset
    }*/

    private inline fun hitAndSaveLTCorner(event: MotionEvent, offset: Float):Boolean{
        val x = event.x
        val y = event.y
        //hitingLTCorner = testHit(x, y, offset, ptLTCorner.x, ptLTCorner.y, ptLTCorner.x+sideLength, ptLTCorner.y+sideLength)
        hitingLTCorner = x > ptLTCorner.x-offset+sideLength && x < ptLTCorner.x+2*sideLength+offset && y > ptLTCorner.y-offset+sideLength && y < ptLTCorner.y+2*sideLength+offset
        if (hitingLTCorner){
            lastPt.x = x
            lastPt.y = y
        }
        return hitingLTCorner
    }
    private inline fun hitAndSaveRBCorner(event: MotionEvent, offset: Float):Boolean{
        val x = event.x
        val y = event.y
        hitingRBCorner = x > ptRBCorner.x-offset-2*sideLength && x < ptRBCorner.x+offset-sideLength && y > ptRBCorner.y-offset-2*sideLength && y < ptRBCorner.y+offset-sideLength
        if (hitingRBCorner){
            lastPt.x = x
            lastPt.y = y
        }
        return hitingRBCorner
    }
    //修改上下界 [1, size-2] 更符合像素范围
    private inline fun updateLTCorner(event: MotionEvent):Boolean{
        var changed = false
        val dx = event.x - lastPt.x
        val dy = event.y - lastPt.y
        val minX = 1F
        val maxX = (size.width-2).toFloat() - lineLength
        val minY = 1F
        val maxY = (size.height-2).toFloat()-lineLength

        //region    update x
        if (dx>0 && ptLTCorner.x<maxX){
            changed = true
            ptLTCorner.x+=dx
            if (ptLTCorner.x>maxX){
                ptLTCorner.x = maxX
            }
        }else if (dx< 0 && ptLTCorner.x>minX) {
            changed = true
            ptLTCorner.x += dx
            if (ptLTCorner.x < minX) {
                ptLTCorner.x = minX
            }
        }
        lastPt.x = event.x
        //endregion

        //region    update y
        if (dy>0 && ptLTCorner.y<maxY){
            changed = true
            ptLTCorner.y+=dy
            if (ptLTCorner.y>maxY){
                ptLTCorner.y = maxY
            }
        }else if (dy< 0 && ptLTCorner.y>minY) {
            changed = true
            ptLTCorner.y += dy
            if (ptLTCorner.y < minY) {
                ptLTCorner.y = minY
            }
        }
        lastPt.y = event.y
        //endregion

        return changed
    }
    private inline fun updateRBCorner(event: MotionEvent):Boolean{
        var changed = false
        val dx = event.x - lastPt.x
        val dy = event.y - lastPt.y
        val minX = lineLength
        val maxX = (size.width-2).toFloat()
        val minY = lineLength
        val maxY = (size.height-2).toFloat()

        //region    update x
        if (dx>0 && ptRBCorner.x<maxX){
            changed = true
            ptRBCorner.x+=dx
            if (ptRBCorner.x>maxX){
                ptRBCorner.x = maxX
            }
        }else if (dx< 0 && ptRBCorner.x>minX) {
            changed = true
            ptRBCorner.x += dx
            if (ptRBCorner.x < minX) {
                ptRBCorner.x = minX
            }
        }
        lastPt.x = event.x
        //endregion

        //region    update y
        if (dy>0 && ptRBCorner.y<maxY){
            changed = true
            ptRBCorner.y+=dy
            if (ptRBCorner.y>maxY){
                ptRBCorner.y = maxY
            }
        }else if (dy< 0 && ptRBCorner.y>minY) {
            changed = true
            ptRBCorner.y += dy
            if (ptRBCorner.y < minY) {
                ptRBCorner.y = minY
            }
        }
        lastPt.y = event.y
        //endregion

        return changed
    }
    //修改画线 一通到底
    private inline fun drawLTCorner(canvas: Canvas, pt: PointF, selected:Boolean, endPt:PointF) {
        val halfWidth = lineWidth / 2
        val linePaint = strokePaint(if (selected) SelectColor else DefaultColor, lineWidth)

        var x = pt.x
        var y = pt.y + halfWidth
        //canvas.drawLine(x, y, x + lineLength, y, linePaint)
        canvas.drawLine(x, y, endPt.x, y, linePaint)
        x = pt.x + halfWidth
        y = pt.y
        //canvas.drawLine(x, y, x, y + lineLength, linePaint)
        canvas.drawLine(x, y, x, endPt.y, linePaint)
        val rectF = RectF(0F,0F,sideLength,sideLength)
        rectF.offsetTo(pt.x+lineWidth+sideLength, pt.y+lineWidth+sideLength)
        canvas.drawRect(rectF, fillPaint(FillColor))
    }
    private inline fun drawRBCorner(canvas: Canvas, pt: PointF, selected:Boolean, begPt:PointF) {
        val halfWidth = lineWidth / 2
        val linePaint = strokePaint(if (selected) SelectColor else DefaultColor, lineWidth)

        var x = pt.x
        var y = pt.y - halfWidth
        //canvas.drawLine(x, y, x - lineLength, y, linePaint)
        canvas.drawLine(x, y, begPt.x, y, linePaint)
        x = pt.x - halfWidth
        y = pt.y
        //canvas.drawLine(x, y, x, y - lineLength, linePaint)
        canvas.drawLine(x, y, x, begPt.y, linePaint)
        val rectF = RectF(0F,0F,sideLength,sideLength)
        //rectF.offsetTo(pt.x-lineWidth-sideLength, pt.y-lineWidth-sideLength)
        rectF.offsetTo(pt.x-lineWidth-2*sideLength, pt.y-lineWidth-2*sideLength)
        canvas.drawRect(rectF, fillPaint(FillColor))
    }

    //endregion

    //region    HanderChanged

    /*private var onLeftListener: ((canvasX: Float) -> Unit)? = null
    fun setOnLeftListener(listener: (canvasX: Float) -> Unit) {
        onLeftListener = listener
    }

    private var onRightListener: ((canvasX: Float) -> Unit)? = null
    fun setOnRightListener(listener: (canvasX: Float) -> Unit) {
        onRightListener = listener
    }

    private var onTopListener: ((canvasY: Float) -> Unit)? = null
    fun setOnTopListener(listener: (canvasY: Float) -> Unit) {
        onTopListener = listener
    }

    private var onBottomListener: ((canvasY: Float) -> Unit)? = null
    fun setOnBottomListener(listener: (canvasY: Float) -> Unit) {
        onBottomListener = listener
    }*/

    private var onLTCornerListener: ((canvasX: Float,canvasY: Float) -> Unit)? = null
    fun setOnLTCornerListener(listener: (canvasX: Float,canvasY: Float) -> Unit) {
        onLTCornerListener = listener
    }

    private var onRBCornerListener: ((canvasX: Float,canvasY: Float) -> Unit)? = null
    fun setOnRBCornerListener(listener: (canvasX: Float,canvasY: Float) -> Unit) {
        onRBCornerListener = listener
    }
    //endregion

    private lateinit var host: ViewGroup
    private lateinit var size: Size
    private var isSetup = false
    private val originPt = PointF(0F, 0F)
    //private var testET:EditText? = null

    init {
        setWillNotDraw(false)
        //backgroundColor = Color.LTGRAY
        this.visibility = View.INVISIBLE

        /*testET = EditText(ctx).apply {
            left = 200
            top = 200
        }*/
        /*testET = EditText(ctx)
        addView(testET)*/
    }

    //region    控件接口
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //info { "getChildCount()=$childCount" }
        if (childCount==1) {
            val child = getChildAt(0)
            info { "childSize:(${child.x},${child.y})(${child.width},${child.height})" }
            val specWidth = ViewGroup.getChildMeasureSpec(0, 0, 300)
            val specHeight = ViewGroup.getChildMeasureSpec(0, 0, 50)
            child.measure(specWidth, specHeight)
        }
        setMeasuredDimension(size.width, size.height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 1) {
            val child = getChildAt(0)
            val x = 200
            val y = 200
            child.layout(x, y, x + 300, y + 50)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isSetup || isInEditMode) {
            return
        }
        //canvas.drawColor(Color.LTGRAY)

        //info { "size=($width,$height)" }
        val frameRect = Rect(0, 0, size.width, size.height).apply {
            //offset(originPt.x.toInt(), originPt.y.toInt())
        }
        //描边宽度是指定宽度的一半
        //BmpUtils.drawRectFrame(canvas, frameRect, Color.BLUE, 10F)

        if (onLTCornerListener != null){
            drawLTCorner(canvas, ptLTCorner, hitingLTCorner, ptRBCorner)
        }
        if (onRBCornerListener != null){
            drawRBCorner(canvas, ptRBCorner, hitingRBCorner, ptLTCorner)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN->{
                hitingLTCorner = false
                hitingRBCorner = false

                if (onLTCornerListener != null){
                    if(hitAndSaveLTCorner(event, 10F)){
                        invalidate()
                        return true
                    }
                }
                if (onRBCornerListener != null){
                    if(hitAndSaveRBCorner(event, 10F)){
                        invalidate()
                        return true
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                //info { "move, $hitingLTCorner" }
                if (onLTCornerListener != null && hitingLTCorner){
                    if(updateLTCorner(event)){
                        invalidate()
                        val l = (ptLTCorner.x+sideLength).toInt()+20
                        val t = (ptLTCorner.y+sideLength).toInt()+20
                        val r = (ptRBCorner.x - sideLength).toInt()-20
                        val b = (ptRBCorner.y - sideLength).toInt()-20
                        //testET?.layout(l, t, r, b)
                    }
                    return true
                }
                if (onRBCornerListener != null){
                    if(updateRBCorner(event)){
                        invalidate()
                    }
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP->{
                //info { "MovingEnd, $event" }
                if (onLTCornerListener != null && hitingLTCorner){
                    onLTCornerListener!!.invoke(ptLTCorner.x+originPt.x,ptLTCorner.y+originPt.y)
                }
                if (onRBCornerListener != null && hitingRBCorner){
                    onRBCornerListener!!.invoke(ptRBCorner.x+originPt.x,ptRBCorner.y+originPt.y)
                }

                hitingLTCorner = false
                hitingRBCorner = false
                invalidate()
                return true
            }
        }

        return super.onTouchEvent(event)
    }
    //endregion

    //region    AttachMent 接口
    override fun locate(x: Float, y: Float) {
        originPt.apply {
            this.x = x
            this.y = y
        }
        apply {
            this.x = originPt.x
            this.y = originPt.y
        }
    }

    override fun hasPt(x: Float, y: Float):Boolean {
        return x > originPt.x && x < originPt.x + size.width &&
                y > originPt.y && y < originPt.y + size.height
    }
    override fun upateVisPt(x: Float, y: Float) {
        apply {
            this.x = originPt.x - x
            this.y = originPt.y - y
        }
    }
    override fun setupLayout(view: ViewGroup, frameSize: Size) {
        if (isSetup){
            size = frameSize
            this.resize(size.width, size.height)
        }else {
            isSetup = true
            host = view
            size = frameSize

            host.addView(this, sizeLayout(size.width, size.height))
        }
        initHandPt()
    }
    override fun destroyLayout() {
        if (isSetup) {
            host.removeView(this)
            isSetup = false
        }
    }
    override val showing: Boolean
        get() = this.visibility == View.VISIBLE

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.INVISIBLE
    }
    //endregion
}
//endregion

//region    ScreenHost
class ScreenHost(ctx: Context,val backCell: BackCell):
        RelativeLayout(ctx),PinchDriver,AnkoLogger{
    companion object {
        private var createIndex = 0
    }
    override val loggerTag: String
        get() = "_SH"

    private val animationManager: AnimationManager
    private var visX:Int
    private var visY:Int
    private var shockX:Float
    private var shockY:Float
    private val scroller: OverScroller
    private var fling = false
    private val tagId:String
    private val moveHandle: AttachMent

    init {
        createIndex++
        tagId = "ID:$createIndex"
        setWillNotDraw(false)
        scroller = OverScroller(context)
        visX = 0
        visY = 0
        shockX = 0F
        shockY = 0F
        animationManager = AnimationManager(this::moveTo)
        moveHandle = HanderView(ctx).apply {
            setupLayout(this@ScreenHost, Size(1200,1200))
            locate(100F, 100F)
            setOnLTCornerListener { canvasX, canvasY ->
                Verbose.info( "LTCorner:($canvasX,$canvasY)" )
            }
            setOnRBCornerListener { canvasX, canvasY ->
                Verbose.info("RBCorner:($canvasX,$canvasY)")
            }
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
        backCell.apply {
            visWidth = w
            visHeight = h
        }
    }

    override fun computeScroll() {
        //info { "computeScroll()" }
        super.computeScroll()
        if (isInEditMode) {
            return
        }

        if(scroller.computeScrollOffset()){
            //info { "scroller(${scroller.currX},${scroller.currY})" }
            moveTo(scroller.currX.toFloat(), scroller.currY.toFloat())
            //pdfView.loadPageByOffset()
        }else if (fling){//animationManager.flinging
            //fling finished
            //animationManager.flinging = false
            fling = false
            //info { "velocityEnd" }
            //movingEnd()
            //pdfView.loadPages();
            //hideHandle()
        }else{
            //info { "computeScroll()" }
        }
    }

    private inline fun reDraw() = invalidate()
    private fun moveTo(x: Float, y: Float) {
        shockX = x
        shockY = y
        visX = shockX.toInt()
        visY = shockY.toInt()
        //info { "moveTo:($visX,$visY)" }
        moveHandle.upateVisPt(shockX, shockY)
        reDraw()
    }
    override fun moveOffset(dx: Float, dy: Float) {
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
        //info { "moveOffset:$hostId($visX,$visY)" }
        moveHandle.upateVisPt(shockX, shockY)
        reDraw()
    }

    override fun moveVelocity(velocityX: Float, velocityY: Float) {
        fling = true

        val frame = backCell.range
        scroller.fling(visX, visY, -velocityX.toInt(), -velocityY.toInt(),
                frame.left, frame.right, frame.top, frame.bottom)
    }

    override val host: View
        get() = this
    override var isDownSource: Boolean = false
    override var isFollow: Boolean = false
    override var canUse: Boolean = true

    override fun scrollEndAction(e: MotionEvent) {
        //测试 动画
        val targetPt = PointF(-width.toFloat()/2,-height.toFloat()/2)
        if(shockX<targetPt.x || shockY<targetPt.y) {
            animationManager.startXYAnimation(shockX, shockY, targetPt.x, targetPt.y)
        }
    }

    override fun stopMoving() {
        animationManager.stop()
        fling = false
        scroller.forceFinished(true)
    }

    override fun doubleClickAction(e: MotionEvent): Boolean {
        //info { "doubleClick --> $tagId" }
        Verbose.info("DbClick --> $tagId")
        return true
    }

    override fun clickAction(e: MotionEvent): Boolean {
        //info { "click --> $tagId" }
        Verbose.info("Click --> $tagId")
        if (moveHandle.hasPt(e.x+shockX,e.y+shockY)){
            if (moveHandle.showing) moveHandle.hide()
            else moveHandle.show()
        }
        return true
    }
}
//endregion

class OutHost(ctx: Context):RelativeLayout(ctx){
    private val dragPinchManager:DragPinchManager
    private val backCell:BackCell
    private lateinit var linearLayout:LinearLayout
    private lateinit var splitLine:TextView

    init {
        backCell = BackCell().apply {
            //测试 首尾相连
            //enabledEndToEnd()
        }
        dragPinchManager = DragPinchManager(ctx).apply {
            enable()
            //测试 外层事件
            setClickListener { event: MotionEvent ->
                //仍可以向子控件传递
                Verbose.info("outClick:(${event.rawX.toInt()},${event.y.toInt()}) CanNext")
                false
            }
            setDoubleClickListener { event: MotionEvent ->
                //不可以向子控件传递
                Verbose.info("outDbClick:(${event.x.toInt()},${event.y.toInt()}) NotNext")
                true
            }
        }

        val first = ScreenHost(ctx, backCell).apply {
            isFollow = true
        }
        dragPinchManager.addDriver(first)

        val second = ScreenHost(ctx, backCell).apply {
            isFollow = true
            moveOffset(this@OutHost.width.toFloat()/2, 0F)
        }
        dragPinchManager.addDriver(second)

        splitLine = TextView(ctx).apply {
            backgroundColor = Color.BLACK
            onClick {
                val oldOrientation = linearLayout.orientation
                //先断开
                linearLayout.removeView(first)
                linearLayout.removeView(splitLine)
                linearLayout.removeView(second)
                //再连接
                if (oldOrientation == LinearLayout.HORIZONTAL){
                    Verbose.info("改为水平布局")
                    linearLayout.apply {
                        orientation = LinearLayout.VERTICAL
                        addView(first, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            height = dip(0)
                            weight = 1F
                        })
                        addView(splitLine, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            height = dip(5)
                        })
                        addView(second, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            height = dip(0)
                            weight = 1F
                        })
                    }
                }else{
                    Verbose.info("改为垂直布局")
                    linearLayout.apply {
                        orientation = LinearLayout.HORIZONTAL
                        addView(first, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            width = dip(0)
                            weight = 1F
                        })
                        addView(splitLine, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            width = dip(5)
                        })
                        addView(second, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            width = dip(0)
                            weight = 1F
                        })
                    }
                }
            }
        }

        linearLayout = LinearLayout(ctx).apply{
            orientation = LinearLayout.HORIZONTAL
            addView(first, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(0)
                weight = 1F
            })
            addView(splitLine, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(5)
            })
            addView(second, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(0)
                weight = 1F
            })
        }

        addView(linearLayout, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
    }
}

