package sample.common

import android.graphics.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import sample.utils.BmpUtils


/**
 * BackCell 背景格子
 * 特点：方块、N个顺序排列、左上为(0,0)
 *
 */
class BackCell(val countX:Int=13,val countY:Int=23,val side: Int=200):AnkoLogger {
    override val loggerTag: String
        get() = "_BC"
    private var cells: List<List<Bitmap>>
    var endToEnd: Boolean
        private set
    init {
        endToEnd = false
        //region    先横向后纵向(内层为横向)
        cells = List<List<Bitmap>>(countY, {index: Int ->
            List<Bitmap>(countX, { subIndex:Int ->
                BmpUtils.buildBmp(side, "$subIndex|$index")
            })
        })
        //endregion
    }
    //纵向周期（坐标点）
    internal val TY = countY*side
    //横向周期（坐标点）
    internal val TX = countX*side
    fun enabledEndToEnd(){
        endToEnd = true
    }
    fun disabledEndToEnd(visX:Int, visY:Int):Point{
        if (endToEnd){
            endToEnd = false
            val visPt=Point()
            var temp = visX.rem(TX)
            if (temp<0) temp+=TX
            visPt.x = temp

            temp = visY.rem(TY)
            if (temp<0) temp+=TY
            visPt.y = temp
            return visPt
        }
        return Point(0,0)
    }

    fun draw(canvas:Canvas, visX:Int, visY:Int) {
        //region    不应该出现的情况,打印跳出
        if (visHeight == 0 || visWidth == 0) {
            info { "Error:(visWidth,visHeight)" }
            return
        }
        if (visX > endX || visX+visWidth<begX) {
            info { "Error:(begX,endX)" }
            return
        }
        if (visY >endY|| visY+visHeight<begY) {
            info { "Error:(begY,endY)" }
            return
        }
        //endregion
        val minIndX = getIndX(Math.max(visX, begX))
        val minIndY = getIndY(Math.max(visY, begY))
        calcVisX(visX, minIndX)
        calcVisY(visY, minIndY)

        info { "visXY:($visX,$visY)" }
        info { "firstPt:($firstVisLeftOffsetX,$firstVisTopOffsetY)" }
        info { "minInd:($minIndX,$minIndY)" }
        info { "visCountXY:($visCountX,$visCountY)" }
        canvas.apply {
            //小于0表示：第一个Cell内的左端偏移，大于于0表示：第一个Cell跳过的偏移
            translate(firstVisLeftOffsetX, firstVisTopOffsetY)

            val cellRect = Rect(0, 0, side, side)
            val rect = Rect(cellRect)
            val paint = Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
            }
            var yInd = 0
            while (yInd < visCountY) {
                //横向渲染
                rect.offsetTo(0, yInd * side)
                //region    绘制一行
                var xInd = 0
                while (xInd < visCountX) {
                    drawBitmap(cells[(yInd+minIndY).rem(countY)][(xInd+minIndX).rem(countX)], cellRect, rect, paint)
                    xInd++
                    rect.offset(side, 0)
                }
                //endregion
                yInd++
            }

            //抵消最开始的操作
            translate(-firstVisLeftOffsetX, -firstVisTopOffsetY)
        }
    }

    //region    visibleSize
    var visWidth: Int = 0
    var visHeight: Int = 0
    //endregion
    //region    range
    val begX: Int get() = if (endToEnd) Int.MIN_VALUE else 0
    val begY:Int get() = if (endToEnd) Int.MIN_VALUE else 0
    val endX: Int get() = if (endToEnd) Int.MAX_VALUE else TX -1
    val endY: Int get() = if (endToEnd) Int.MAX_VALUE else TY -1
    val range: Rect get() = Rect(begX,begY,endX-0,endY-0)
    //endregion
    //region    getInd
    private inline fun getIndX(x: Int): Int {
        // T = [0, count*side-1] 或者 [0, count*side),这个更好
        var temp = x.rem(TX)
        if (temp < 0) temp += TX
        return temp / side
    }
    private inline fun getIndY(y: Int): Int {
        // T = [0, count*side-1] 或者 [0, count*side),这个更好
        var temp = y.rem(TY)
        if (temp < 0) temp += TY
        return temp / side
    }
    //endregion
    //region    visible cells
    private var firstVisLeftOffsetX = 0F
    private var firstVisTopOffsetY = 0F
    private var visCountX = 0
    private var visCountY = 0
    //计算以x开始跨度visWidth,覆盖到的Cell数量及起始Cell的偏移
    private inline fun calcVisX(x: Int, ind: Int) {
        if (x <= begX) {
            //region    包含跳格子（终点有无格子的情况）
            firstVisLeftOffsetX = (begX - x).toFloat()
            val width = visWidth + x
            var factor = width / side //要累计
            var facDelta = width.rem(side)   //要累减
            if (facDelta > 0) factor++
            visCountX = Math.min(factor, countX)
            //endregion
            return
        }

        val delta = x.rem(side) //可能为负
        var factor = visWidth / side  //要累计
        var facDelta = visWidth.rem(side)   //要累减

        firstVisLeftOffsetX = -(if (delta < 0) side + delta else delta).toFloat()

        //整数倍左边x存在
        if (delta != 0) {
            factor++
            //facDelta累减
            if (delta < 0) facDelta += delta
            else facDelta += delta - side
        }

        //整数倍右边x存在
        if (facDelta > 0) factor++

        if (!endToEnd && (factor + ind > countX)) {
            //region    终点有空格子
            visCountX = countX - ind
            //endregion
            return
        }
        visCountX = factor
    }
    //计算以y开始跨度visHeight,覆盖到的Cell数量及起始Cell的偏移
    private inline fun calcVisY(y: Int, ind: Int){
        if (y <= begY) {
            //region    包含跳格子（终点有无格子的情况）
            firstVisTopOffsetY = (begY - y).toFloat()
            val height = visHeight + y
            var factor = height / side //要累计
            var facDelta = height.rem(side)   //要累减
            if (facDelta > 0) factor++
            visCountY = Math.min(factor, countY)
            //endregion
            return
        }

        val delta = y.rem(side) //可能为负
        var factor = visHeight / side  //要累计
        var facDelta = visHeight.rem(side)   //要累减

        firstVisTopOffsetY = -(if (delta < 0) side + delta else delta).toFloat()

        //整数倍左边x存在
        if (delta != 0) {
            factor++
            //facDelta累减
            if (delta < 0) facDelta += delta
            else facDelta += delta - side
        }

        //整数倍右边x存在
        if (facDelta > 0) factor++

        if (!endToEnd && (factor + ind > countY)) {
            //region    终点有空格子
            visCountY = countY - ind
            //endregion
            return
        }
        visCountY = factor
    }
    //endregion
}

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