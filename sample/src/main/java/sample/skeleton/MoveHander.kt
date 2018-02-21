package sample.skeleton

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import sample.utils.EditDir
import sample.utils.EditHand
import sample.utils.EditXY
import sample.utils.SubViewUtils

/**
 * Created by Administrator on 2018/2/19.
 */

class MoveHander(val handType:EditHand, val initRect: Rect, val hitView:View, val moveView: View):AnkoLogger {
    override val loggerTag: String
        get() = "_MH"
    //moveView中的位置，moveView包含hitView
    val lastPt: PointF
    //var editLock: Boolean = false //未使用，默认可以编辑
    private var hiting = false

    val delta = 20

    init {
        //默认取中心点
        lastPt = PointF(initRect.exactCenterX(), initRect.exactCenterX())

        //是否初始化
        val isInit = true

        //初始化时有偏移
        val offset = if (isInit) delta else 0
        val rect = initRect //输入范围用Rect表示，按需选用

        updateLastPt(offset, rect)
    }

    //region    upate View or LastPt
    private inline fun updateLastPt(offset: Int, rect: Rect) {
        when (handType) {
        //region    各种情况（按需选用）
            EditHand.Left -> {
                lastPt.x = (rect.left + offset).toFloat()
            }
            EditHand.Top -> {
                lastPt.y = (rect.top + offset).toFloat()
            }
            EditHand.Right -> {
                lastPt.x = (rect.right - offset).toFloat()
            }
            EditHand.Bottom -> {
                lastPt.y = (rect.bottom - offset).toFloat()
            }
            EditHand.LTCorner -> {
                lastPt.x = (rect.left + offset).toFloat()
                lastPt.y = (rect.top + offset).toFloat()
            }
            EditHand.RBCorner -> {
                lastPt.x = (rect.right - offset).toFloat()
                lastPt.y = (rect.bottom - offset).toFloat()
            }
        //endregion
        }
    }
    private inline fun updateView(event: MotionEvent, view: View) {
        when (handType) {
        //region    各种情况（按需选用）
            EditHand.Left, EditHand.Right -> {
                //不能超限
                var x = event.rawX - lastPt.x
                view.x = if (x>=0) x else 0F

            }
            EditHand.Top, EditHand.Bottom -> {
                view.y = event.rawY - lastPt.y
            }
            EditHand.LTCorner, EditHand.RBCorner -> {
                view.x = event.rawX - lastPt.x
                view.y = event.rawY - lastPt.y
            }
        //endregion
        }
    }
    private inline fun updateLastPt(event: MotionEvent, view: View) {
        when (handType) {
        //region    各种情况（按需选用）
            EditHand.Left, EditHand.Right -> {
                lastPt.x = event.rawX - view.x
            }
            EditHand.Top, EditHand.Bottom -> {
                lastPt.y = event.rawY - view.y
            }
            EditHand.LTCorner, EditHand.RBCorner -> {
                lastPt.x = event.rawX - view.x
                lastPt.y = event.rawY - view.y
            }
        //endregion
        }
    }
    //endregion

    //浮点矩形的值都是[0F,1F],然后再结合实际Size大小进行计算
    fun loadConfig(rectF: RectF) {
        //region Size,起点,两个横标,两个纵标
        //提取Size
        val width = initRect.width()
        val height = initRect.height()
        //起点
        val x = initRect.left
        val y = initRect.top
        //实际横坐标（相对宽）
        val w1 = (width * rectF.left).toInt()
        val w2 = (width * rectF.right).toInt()
        //实际纵坐标（相对高）
        val h1 = (height * rectF.top).toInt()
        val h2 = (height * rectF.bottom).toInt()
        //endregion

        val rect = Rect(x + w1, y + h1, x + w2, y + h2)
        updateLastPt(0, rect)
    }

    //选中并记录
    fun hitAndSave(event: MotionEvent, offset: Int): Boolean {
        hiting = SubViewUtils.hiting(hitView, event, offset)
        if (hiting) {
            updateLastPt(event, moveView)
        }
        return hiting
    }

    //记录并移动
    fun saveAndMove(event: MotionEvent):Boolean {
        if (hiting) {
            updateView(event, moveView)
        }
        return hiting
    }

    //移动完毕
    fun moveEnd(action:(obj:MoveHander)->Unit):Boolean{
        if (hiting){
            action(this)
            hiting = false
            return true
        }
        return false
    }
}