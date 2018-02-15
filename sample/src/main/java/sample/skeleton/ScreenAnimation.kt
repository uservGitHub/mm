package sample.skeleton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by Administrator on 2018/2/15.
 */

/**
 * 使用指南：
 * 1 惯性滑动startFlingAnimation是从host中驱动的（获取scroller）
 * 2 其他动画采用startXXAnimation，调用初始化中的代理
 * 3 动画不能相容，后者优先（中断已有的动画）
 */
class ScreenAnimation(val driver: DragPinchRawDriver): AnkoLogger {
    override val loggerTag: String
        get() = "_SA"
    private var animation: ValueAnimator? = null
    private var flinging = false
    private var baseX = 0F
    private var baseY = 0F
    private var deltaX = 0F
    private var deltaY = 0F
    private val scroller: OverScroller
    init {
        scroller = OverScroller(driver.host.context)
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
        val list = arrayListOf<Int>(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
        info { list }
        scroller.fling(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
    }
    internal fun computeFling(){
        if(scroller.computeScrollOffset()){
            driver.moveTo(scroller.currX, scroller.currY)
            //pdfView.loadPageByOffset()
        }else if (flinging){
            //fling finished
            flinging = false
            driver.flingEndAction()
            //pdfView.loadPages();
            //hideHandle()
        }
    }
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
            driver.moveTo(x.toInt(), y.toInt())
            //host.moveOffset(offset, 0F)
            //pdfView.loadPageByOffset();
        }

        override fun onAnimationCancel(animation: Animator?) {
            //pdfView.loadPages();
            info { "CancelXY()" }
            driver.flingEndAction()
        }

        override fun onAnimationEnd(animation: Animator?) {
            //pdfView.loadPages();
            info { "EndXY()" }
            driver.flingEndAction()
        }
    }
}