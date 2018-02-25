package sample.hander

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.skeleton.DragPinchRawDriver
import sample.utils.BmpUtils

/**
 * Created by Administrator on 2018/2/20.
 * hander 所有的文件，hander包
 */

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

    private val sideLength = 40F
    private val lineLength = 100F
    private val lineWidth = 3F
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
        hitingLTCorner = x > ptLTCorner.x-offset && x < ptLTCorner.x+sideLength+offset && y > ptLTCorner.y-offset && y < ptLTCorner.y+sideLength+offset
        if (hitingLTCorner){
            lastPt.x = x
            lastPt.y = y
        }
        return hitingLTCorner
    }
    private inline fun hitAndSaveRBCorner(event: MotionEvent, offset: Float):Boolean{
        val x = event.x
        val y = event.y
        hitingRBCorner = x > ptRBCorner.x-offset-sideLength && x < ptRBCorner.x+offset && y > ptRBCorner.y-offset-sideLength && y < ptRBCorner.y+offset
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
        rectF.offsetTo(pt.x+lineWidth, pt.y+lineWidth)
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
        rectF.offsetTo(pt.x-lineWidth-sideLength, pt.y-lineWidth-sideLength)
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
    private val testET:EditText

    init {
        setWillNotDraw(false)
        //backgroundColor = Color.LTGRAY
        this.visibility = View.INVISIBLE

        /*testET = EditText(ctx).apply {
            left = 200
            top = 200
        }*/
        testET = EditText(ctx)
        addView(testET)
    }

    //region    控件接口
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //info { "getChildCount()=$childCount" }
        val child = getChildAt(0)
        info { "childSize:(${child.x},${child.y})(${child.width},${child.height})" }
        val specWidth = ViewGroup.getChildMeasureSpec(0,0,300)
        val specHeight = ViewGroup.getChildMeasureSpec(0,0,50)
        child.measure(specWidth, specHeight)
        setMeasuredDimension(size.width, size.height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val child = getChildAt(0)
        val x = 200
        val y = 200
        child.layout(x,y,x+300, y+50)
    }

    override fun onDraw(canvas: Canvas) {
        if (!isSetup || isInEditMode) {
            return
        }
        //info { "size=($width,$height)" }
        val frameRect = Rect(0, 0, size.width, size.height).apply {
            //offset(originPt.x.toInt(), originPt.y.toInt())
        }
        //描边宽度是指定宽度的一半
        BmpUtils.drawRectFrame(canvas, frameRect, Color.BLUE, 10F)

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
                        testET.layout(l, t, r, b)
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

//分割插件：
//一般的插件外，增加分裂事件（完全复制，可关闭）
//如何分裂（横纵），关闭哪一个，随动哪一个；及呈现由实现来决定
//布局控制也由实现来控制
interface SplitMent{

/*    //是否分割成两部分
    val isTwo: Boolean
    //第一屏是否随动
    val isFollowFirst: Boolean
    //第二屏是否随动
    val isFollowSecond: Boolean
    //是否水平分割
    val isHor: Boolean
    //添加第一个
    fun addFirst(firstView: DragPinchRawDriver, secondView:DragPinchRawDriver)
    //分割(水平/垂直 分割)
    fun split(horizontal:Boolean, action:(driver:DragPinchRawDriver)->Unit)
    //是否第一屏
    fun close(isFirst: Boolean, action:(driver:DragPinchRawDriver)->Unit)
    //是否随动屏
    fun follow(isFirst: Boolean, isFollow:Boolean)*/

    //分裂，给定now，产出新的（如何复制）
    //val onSplitListener:(now:DragPinchRawDriver)->DragPinchRawDriver
    //要关闭的那个，可以做一些资源释放的工作
    //val onCloseListener:(target:DragPinchRawDriver)->Unit

    val showing: Boolean
    fun show()
    fun hide()
    //fun hideDelayed()
    fun setupLayout(view:ViewGroup, now: DragPinchRawDriver,
                    onSplitListener:(now:DragPinchRawDriver)->DragPinchRawDriver,
                    onCloseListener:(target:DragPinchRawDriver)->Unit)

    fun destroyLayout()
}
class SplitView(ctx: Context):RelativeLayout(ctx),SplitMent{
    private lateinit var host:ViewGroup
    private var firstContent:DragPinchRawDriver? = null
    private var secondContent:DragPinchRawDriver? = null
    private lateinit var splitAction:(now:DragPinchRawDriver)->DragPinchRawDriver
    private lateinit var closeAction:(target:DragPinchRawDriver)->Unit
    private lateinit var container:LinearLayout

    init {
        visibility = View.INVISIBLE
    }

    override fun destroyLayout() {
        host.removeView(this)
    }
    override val showing: Boolean
        get() = this.visibility == View.VISIBLE

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.INVISIBLE
    }

    override fun setupLayout(view: ViewGroup, now: DragPinchRawDriver,
                             onSplitListener:(now:DragPinchRawDriver)->DragPinchRawDriver,
                             onCloseListener:(target:DragPinchRawDriver)->Unit) {
        host = view
        firstContent = now

        splitAction = onSplitListener
        closeAction = onCloseListener

        //region    悬浮控制面板
        val id1 = 1
        val id2 = 2
        val id3 = 3
        val id4 = 4
        val id5 = 5
        val id6 = 6

        val splitButton = Button(context).apply {
            text = "垂直分裂"
            id = id1
            onClick {
                secondContent = splitAction.invoke(firstContent!!)
                container.apply {
                    removeView(firstContent!!.host)
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    addView(firstContent!!.host, verSplitLayout())
                    addView(secondContent!!.host, verSplitLayout())
                }
            }
        }
        val btnLp = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
        }
        this.addView(splitButton, btnLp)
        val followSetFirst = CheckBox(context).apply {
            id = id2
            text = "屏1随动"
            isChecked = false
            onCheckedChange { buttonView, isChecked ->
                firstContent?.follow(isChecked)
            }
        }
        val followFirstLp = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.RIGHT_OF, splitButton.id)
        }
        this.addView(followSetFirst, followFirstLp)
        val followSetSecond = CheckBox(context).apply {
            id = id3
            text = "屏2随动"
            isChecked = false
            onCheckedChange { buttonView, isChecked ->
                secondContent?.follow(isChecked)
            }
        }
        val followSecondLp = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.RIGHT_OF, followSetFirst.id)
        }
        this.addView(followSetSecond, followSecondLp)

        this.addView(Button(context).apply {
            text = "关闭屏1"
            id = id4
            onClick {
                /*secondContent = splitAction.invoke(firstContent!!)
                container.addView(secondContent!!.host, horSplitLayout())*/
                firstContent?.let{
                    closeAction(it)
                    container.removeView(it.host)
                    //替代
                    firstContent = secondContent
                    secondContent = null
                }

            }
        }
                , RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.RIGHT_OF, id3)
        })

        this.addView(Button(context).apply {
            text = "关闭屏2"
            id = id5
            onClick {
                secondContent?.let {
                    closeAction(it)
                    container.removeView(it.host)
                    secondContent = null
                }
            }
        }
                , RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.RIGHT_OF, id4)
        })
        this.addView(Button(context).apply {
            text = "垂直分裂"
            id = id6
            onClick {
                secondContent = splitAction.invoke(firstContent!!)
                container.apply {
                    removeView(firstContent!!.host)
                    orientation = android.widget.LinearLayout.VERTICAL
                    addView(firstContent!!.host, horSplitLayout())
                    addView(secondContent!!.host, horSplitLayout())
                }
            }
        }
                , RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.RIGHT_OF, id5)
        })
        //endregion

        setupOne(firstContent!!)
        //val lp = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val lp = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0,0,0,0)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
        host.addView(this, lp)
    }

    private fun setupOne(now: DragPinchRawDriver){
        val layout = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT).apply {
            setMargins(0,0,0,0)
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_START)
            addRule(RelativeLayout.ALIGN_PARENT_END)
        }
        container = LinearLayout(context)
        host.addView(container, layout)
        container.apply {
            orientation = LinearLayout.VERTICAL
            addView(now.host, horSplitLayout())
        }
    }


    private inline fun verSplitLayout() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT).apply {
        width = dip(0)
        weight = 1F

    }
    private inline fun horSplitLayout() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT).apply {
        height = dip(0)
        weight = 1F
    }
}

/*class SplitView1(ctx: Context):RelativeLayout(ctx),SplitMent{
    //在外层已经创建好了，不需要动态进行初始化
    private lateinit var first:DragPinchRawDriver
    private lateinit var second:DragPinchRawDriver
    private lateinit var host:ViewGroup

    override val showing: Boolean
        get() = this.visibility == View.VISIBLE

    override var isTwo: Boolean = false
        private set

    override var isHor: Boolean = false
        private set

    override var isFollowFirst: Boolean = false
        private set

    override var isFollowSecond: Boolean = false
        private set

    override fun addFirst(firstView: DragPinchRawDriver) {
        first = firstView
        //布局
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT).apply {
            width = dip(0)
            weight = 1F
        }
        host.addView(first.host, lp)
    }

    override fun split(horizontal: Boolean, secondView: DragPinchRawDriver) {
        if (isTwo){
            return
        }
        second = secondView
        isTwo = true

        if (horizontal){
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(0)
                weight = 1F
            }
            first.host.layoutParams = lp
            host.addView(second.host, lp)
        }else{
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                height = dip(0)
                weight = 1F
            }
            first.host.layoutParams = lp
            host.addView(second.host, lp)
        }
    }

    override fun close(isFirst: Boolean) {
        if (!isTwo){
            return
        }

        if (isFirst){
            host.removeView(first.host)
            first = second
        }else{
            host.removeView(second.host)
        }
        isTwo = false
    }

    override fun follow(isFirst: Boolean, isFollow: Boolean) {
        if (!isTwo){
            return
        }
        if (isFirst){
            first.follow(isFollow)
        }else{
            second.follow(isFollow)
        }
    }
}*/
