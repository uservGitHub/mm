package sample.end

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.dip
import org.jetbrains.anko.info
import sample.common.BackCell

/**
 * Created by work on 2018/2/26.
 * 记录：
 * 1 override fun computeScroll()
 *  invalidate() 时自动调用，
 */

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

class DragPinchManager(ctx:Context):
        View.OnTouchListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener{

    private var scrolling = false
    private var scaling = false
    private var enabled = false
    private val hostList = mutableListOf<PinchDriver>()

    private val gestureDetector: GestureDetector
    private val scaleGestureDetetor:ScaleGestureDetector
    //返回值表示是否继续进行子项触发
    private var doubleClickListener: ((event:MotionEvent)->Boolean)? = null
    //返回值表示是否继续进行子项触发
    private var clickListener: ((event:MotionEvent)->Boolean)? = null


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
        //moveHandle.dump(x,y)
        //moveHandle.moveOffset(shockX, shockY)
        //moveHandle.upateVisPt(shockX, shockY)
        reDraw()
    }
    override fun moveOffset(dx: Float, dy: Float) {
        shockX += dx
        shockY += dy
        visX = shockX.toInt()
        visY = shockY.toInt()
        //info { "moveOffset:$hostId($visX,$visY)" }
        //moveHandle.moveOffset(shockX, shockY)
        //moveHandle.upateVisPt(shockX, shockY)
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

    }

    override fun stopMoving() {
        animationManager.stop()
        fling = false
        scroller.forceFinished(true)
    }

    override fun doubleClickAction(e: MotionEvent): Boolean {
        info { "doubleClick --> $tagId" }
        return true
    }

    override fun clickAction(e: MotionEvent): Boolean {
        info { "click --> $tagId" }
        return true
    }
}

class OutHost(ctx: Context):LinearLayout(ctx){
    private val dragPinchManager:DragPinchManager
    private val backCell:BackCell

    init {
        backCell = BackCell().apply {
            enabledEndToEnd()
        }
        dragPinchManager = DragPinchManager(ctx).apply {
            enable()
        }

        val first = ScreenHost(ctx, backCell).apply {
            isFollow = true
        }
        dragPinchManager.addDriver(first)

        val second = ScreenHost(ctx, backCell).apply {
            isFollow = true
        }
        dragPinchManager.addDriver(second)

        orientation = LinearLayout.HORIZONTAL
        addView(first, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT).apply {
            width = dip(0)
            weight = 1F
        })
        addView(second, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT).apply {
            width = dip(0)
            weight = 1F
        })
    }
}