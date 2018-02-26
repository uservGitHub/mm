package sample.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by work on 2018/1/24.
 */

/**
 * 使用指南：
 * 1 惯性滑动startFlingAnimation是从host中驱动的（获取scroller）
 * 2 其他动画采用startXXAnimation，调用初始化中的代理
 * 3 动画不能相容，后者优先（中断已有的动画）
 */
class HostAnimation(val host:View,
                    val moveTo: (x: Int, y: Int) -> Unit,
                    val velocity: (startX:Int,startY:Int,velocityX:Int,velocityY:Int,minX:Int,maxX:Int,minY:Int,maxY:Int)->Unit,
                    val movingEnd:()->Unit):AnkoLogger{
    override val loggerTag: String
        get() = "_HA"
    private var animation: ValueAnimator? = null
    private var flinging = false
    private var baseX = 0F
    private var baseY = 0F
    private var deltaX = 0F
    private var deltaY = 0F
    private val scroller: OverScroller
    init {
        scroller = OverScroller(host.context)
    }
    fun stopAll(){
        animation?.apply {
            cancel()
        }
        animation = null
        stopFling()
    }
    fun stopFling(){
        flinging = false
        scroller.forceFinished(true)
    }
    fun startXYAnimation(xFrom:Int, yFrom:Int, xTo:Int, yTo:Int){
        stopAll()
        animation = ValueAnimator.ofFloat(0F, 1F)
        baseX = xFrom.toFloat()
        baseY = yFrom.toFloat()
        deltaX = (xTo-xFrom).toFloat()
        deltaY = (yTo-yFrom).toFloat()
        val xyAnimation = XYAnimation()
        animation?.apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener(xyAnimation)
            addListener(xyAnimation)
            setDuration(400)
            start()
        }
    }
    fun startFlingAnimation(startX:Int,startY:Int,velocityX:Int,velocityY:Int,minX:Int,maxX:Int,minY:Int,maxY:Int){
        info { "startFlingAnimation" }
        stopAll()
        flinging = true
        velocity(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)

        /*val list = arrayListOf<Int>(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
        info { list }
        scroller.fling(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
        info { "fling" }*/
    }
    /*internal fun computeFling(){
        if(scroller.computeScrollOffset()){
            moveTo(scroller.currX, scroller.currY)
            //pdfView.loadPageByOffset()
        }else if (flinging){
            //fling finished
            flinging = false
            movingEnd()
            //pdfView.loadPages();
            //hideHandle()
        }
    }*/
    private fun hideHandle() {
        /*if (pdfView.getScrollHandle() != null) {
            pdfView.getScrollHandle().hideDelayed()
        }*/
    }
    inner class XYAnimation(): AnimatorListenerAdapter(), ValueAnimator.AnimatorUpdateListener{
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val offset = animation.getAnimatedValue() as Float
            val x = baseX+offset*deltaX
            val y = baseY+offset*deltaY
            info { "moveTo($x,$y)" }
            moveTo(x.toInt(), y.toInt())
            //host.moveOffset(offset, 0F)
            //pdfView.loadPageByOffset();
        }

        override fun onAnimationCancel(animation: Animator?) {
            //pdfView.loadPages();
            info { "CancelXY()" }
            movingEnd()
        }

        override fun onAnimationEnd(animation: Animator?) {
            //pdfView.loadPages();
            info { "EndXY()" }
            movingEnd()
        }
    }
}

class DeltaAnimation(val host:View,
                     val moveOffset:(deltaX:Float,deltaY:Float)->Unit,
                     val moveTo:(x:Int, y:Int)->Unit):AnkoLogger{
    override val loggerTag: String
        get() = "_DA"
    private var animation: ValueAnimator? = null
    private var flinging = false
    private val scroller: OverScroller
    init {
        scroller = OverScroller(host.context)
    }
    fun stopAll(){
        animation?.apply {
            cancel()
        }
        animation = null
        stopFling()
    }
    fun stopFling(){
        flinging = false
        scroller.forceFinished(true)
    }
    fun startXAnimation(xFrom:Float, xTo:Float){
        info { "xFrom,xTo=($xFrom,$xTo)" }
        stopAll()
        animation = ValueAnimator.ofFloat(xFrom, xTo)
        val xAnimation = XAnimation()
        animation?.apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener(xAnimation)
            addListener(xAnimation)
            setDuration(400)
            start()
        }

    }
    fun startYAnimation(yFrom:Float, yTo:Float){
        stopAll()
        animation = ValueAnimator.ofFloat(yFrom, yTo)
        val yAnimation = YAnimation()
        animation?.apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener(yAnimation)
            addListener(yAnimation)
            setDuration(400)
            start()
        }
    }
    fun startFlingAnimation(startX:Int,startY:Int,velocityX:Int,velocityY:Int,minX:Int,maxX:Int,minY:Int,maxY:Int){
        info { "startFlingAnimation" }
        stopAll()
        flinging = true
        val list = arrayListOf<Int>(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
        info { list }
        scroller.fling(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
    }
    internal fun computeFling(){
        info { "scroller(${scroller.currX},${scroller.currY}" }
        if(scroller.computeScrollOffset()){
            moveTo(scroller.currX, scroller.currY)
            info { "moveTo" }
            //host.moveOffset(0F,0F)
            //pdfView.loadPageByOffset()
        }else if (flinging){
            //fling finished
            info { "flinging" }
            flinging = false
            //pdfView.loadPages();
            hideHandle()
        }else{
            info { "other" }
        }
    }
    private fun hideHandle() {
        /*if (pdfView.getScrollHandle() != null) {
            pdfView.getScrollHandle().hideDelayed()
        }*/
    }
    inner class XAnimation(): AnimatorListenerAdapter(), ValueAnimator.AnimatorUpdateListener{
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val offset = animation.getAnimatedValue() as Float
            info { "moveOffsetX=($offset)" }
            //moveOffset(offset, 0F)
            moveTo(offset.toInt(), 0)
            //host.moveOffset(offset, 0F)
            //pdfView.loadPageByOffset();
        }

        override fun onAnimationCancel(animation: Animator?) {
            //pdfView.loadPages();
            info { "moveCancelX()" }
        }

        override fun onAnimationEnd(animation: Animator?) {
            //pdfView.loadPages();
            info { "moveEndX" }
        }
    }
    inner class YAnimation(): AnimatorListenerAdapter(), ValueAnimator.AnimatorUpdateListener{
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val offset = animation.getAnimatedValue() as Float
            moveOffset(0F, offset)
            //host.moveOffset(0F, offset)
            //pdfView.loadPageByOffset();
        }

        override fun onAnimationCancel(animation: Animator?) {
            //pdfView.loadPages();
        }

        override fun onAnimationEnd(animation: Animator?) {
            //pdfView.loadPages();
        }
    }
}