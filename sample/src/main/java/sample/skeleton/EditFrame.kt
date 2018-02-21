package sample.skeleton

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.shapes.RectShape
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import org.jetbrains.anko.backgroundColor
import sample.utils.EditHand

/**
 * Created by Administrator on 2018/2/19.
 */

//编辑框，显示在指定的矩形内，可以移动4个边；
//移动完毕，触发想要的执行函数
interface EditFrame{
    val showing: Boolean
    fun show()
    fun hide()
    fun moveOffset(dx:Float, dy:Float)
    //fun hideDelayed()
    //表示作为一个单独的层安装在view上,frameRect和view属于同坐标系
    fun setupLayout(view: ViewGroup, frameRect: Rect)
    //从安装的层上删除
    fun destroyLayout()
}

class DefaultEditFrame(ctx:Context, val action:(e:MoveHander)->Unit):FrameLayout(ctx),EditFrame{
    //region    自身包含的自控件
    private lateinit var editHanders:List<MoveHander>
    //可以是复合控件
    //private lateinit var editViews:List<View>
    private lateinit var editLeftView:View
    private lateinit var editTopView:View
    //endregion
    private lateinit var host:ViewGroup
    private lateinit var initRect:Rect
    private lateinit var origin: PointF
    private val stroke = 10

    init {
        //使用时被覆盖
        initRect = Rect(0, 0, 100, 100)

        this.visibility = View.INVISIBLE
        backgroundColor = Color.LTGRAY

        //region    自身的布局
        editLeftView = TextView(ctx).apply {
            backgroundColor = Color.GREEN
        }
        val leftLayout =  LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            height = initRect.height()
            width = stroke
            //x = initRect.left.toFloat()
            //y = initRect.top.toFloat()
        }
        editTopView = TextView(ctx).apply {
            backgroundColor = Color.BLUE
        }
        val topLayout =  LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            height = stroke
            width = initRect.width()
            //x = initRect.left.toFloat()
            //y = initRect.top.toFloat()
        }
        addView(editLeftView, leftLayout)
        addView(editTopView, topLayout)
        //endregion

        editHanders = listOf(MoveHander(EditHand.Left, initRect, editLeftView, editLeftView),
                MoveHander(EditHand.Top, initRect, editTopView, editTopView))
    }

    private fun updateHanders(){
        val leftLayout = editLeftView.layoutParams.apply {
            width = stroke
            height = initRect.height()
            //x = 0F
            //y = 0F
        }
        val topLayout = editTopView.layoutParams.apply {
            height = stroke
            width = initRect.width()
            //x = 0F
            //y = 0F
        }
        editLeftView.layoutParams = leftLayout
        editTopView.layoutParams = topLayout

        this.x = origin.x
        this.y = origin.y
    }

    private fun updatePt(x:Float, y:Float){
        this.x = origin.x -x
        this.y = origin.y -y
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN->{
                editHanders.forEach {
                    if( it.hitAndSave(event, 0)){
                        return true
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                editHanders.forEach {
                    if( it.saveAndMove(event)){
                        return true
                    }
                }
            }
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP->{
                editHanders.forEach {
                    val that = it
                    if( it.moveEnd({action(it)})){
                        return true
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    override fun setupLayout(view: ViewGroup, frameRect: Rect) {
        host = view
        initRect.set(0, 0, frameRect.width(), frameRect.bottom)
        origin = PointF(frameRect.left.toFloat(), frameRect.top.toFloat())

        //父控件中的安装布局
        val thisLayout = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            width = initRect.width()
            height = initRect.height()
            //x = origin.x
            //y = origin.y
        }
        host.addView(this, thisLayout)

        updateHanders()
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

    override fun moveOffset(dx: Float, dy: Float) {
        updatePt(dx, dy)
    }
}