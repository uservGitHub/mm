package sample.common

import android.graphics.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import sample.utils.BmpUtils

/**
 * Created by work on 2018/1/24.
 * 主要接口：
 * endToEnd
 * enable()
 * setOnEndToEndListener
 * draw([a,b], canvas)
 */

class BackPage(val count:Int = 2, val side:Int = 200):AnkoLogger {
    override val loggerTag: String
        get() = "_BP"
    private var pages: List<Bitmap>
    private var canUse: Boolean
    init {
        canUse = false
        pages = List<Bitmap>(count, { index: Int ->
            BmpUtils.buildBmp(80, index)
        })
    }
    val T = count*side - 1
    //region    pixs: [beg,end] 或 [-MAX, +MAX]
    val beg: Int get() = if (endToEnd) Int.MIN_VALUE else 0
    val end: Int get() = if (endToEnd) Int.MAX_VALUE else count * side -1
    //endregion
    //region    能否响应endToEnd
    fun enable(){
        canUse = true
    }
    fun disable(){
        canUse = false
    }
    //endregion

    fun page(ind: Int) = pages[ind]

    var endToEnd = false
        set(value) {
            if (field == value) return
            field = value
            if (canUse) {
                onEndToEndListener?.let { it() }
            }
        }
    //region    onEndToEndChanged
    private var onEndToEndListener: (() -> Unit)? = null

    fun setOnEndToEndListener(listener: () -> Unit) {
        onEndToEndListener = listener
    }
    //endregion

    /**
     * pBeg为起点，对应canvas的canvasOffset点
     * [pBeg, pEnd] 选择内容
     * canvasOffsetPt 表示选择内容在canvas中的偏移起点，由外层调用canvas.transelate(pt)
     */
    fun draw(pBeg: Int, pEnd: Int, canvas: Canvas, canvasOffset: Point = Point(0, 0)) {
        if (pBeg < end && beg < pEnd) {
            val min = Math.max(pBeg, beg)
            val max = Math.min(pEnd, end)
            //region    兼容endToEnd
            val minInd = getInd(min)
            val maxInd = getInd(max)
            val list = listOf<Int>(pBeg,pEnd, beg,end, minInd, maxInd)
            info { list }
            val delta = getOffset(min)+
                    if (pBeg>0) 0 else pBeg
            canvas.translate(-delta.toFloat(), 0F)
            //region    draw clipped pages
            val pageRect = Rect(0,0,side,side)
            var canvasRect = Rect(pageRect)
            val paint = Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
            }
            val drawPage = { ind: Int ->
                canvas.drawBitmap(pages[ind], pageRect, canvasRect, paint)
                canvasRect.offset(side, 0)
            }
            if (minInd>maxInd){
                for (ind in minInd until count){
                    drawPage(ind)
                }
                for (ind in 0..maxInd){
                    drawPage(ind)
                }
            }else{
                for (ind in minInd..maxInd){
                    drawPage(ind)
                }
            }
            //endregion
            canvas.translate(delta.toFloat(), 0F)
            //endregion
        }
    }

    /**
     * ind 与 side 的关系 side = pages[ind]
     * side 与 区间的关系 side = [0,side-1]
     * x 正负都适用
     * ind 大于等于0
     * 不处理endToEnd
     */
    private inline fun getInd(x: Int): Int {
        // T = [0, count*side-1] 或者 [0, count*side)
        val T = count * side - 1
        val ind = x.rem(T) / side
        if (ind < 0) return ind + count
        return ind
    }

    /**
     * offset 与 side 的关系 offset 是在side上的截取起点
     * offset 大于等于0 从左侧开始
     * x 正负都适用
     * 不处理endToEnd
     */
    private inline fun getOffset(x: Int): Int {
        val offset = x.rem(side)
        if (offset < 0) return offset + side
        return offset
    }
}