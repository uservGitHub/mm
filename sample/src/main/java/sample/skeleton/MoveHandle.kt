package sample.skeleton

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.os.Handler
import android.support.v7.widget.ViewUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.switch
import org.jetbrains.anko.textColor
import sample.utils.EditHand
import sample.utils.SubViewUtils

/**
 * Created by Administrator on 2018/2/16.
 */

interface MoveHandle{
    val showing: Boolean
    fun show()
    fun hide()
    fun hideDelayed()
    fun setupLayout(view:ViewGroup)
    fun destroyLayout()
    fun dump(x:Int, y:Int)
}
class EditMoveHandle(ctx:Context):FrameLayout(ctx),MoveHandle{
    private val dumpXY:TextView
    private val handle:Handler
    private val hideRunnable:Runnable
    private lateinit var host:ViewGroup
    init {
        dumpXY = TextView(ctx).apply {
            text = "没写字"
            backgroundColor = Color.BLUE
            textColor = Color.RED
        }
        this.visibility = View.INVISIBLE
        handle = Handler()
        hideRunnable = Runnable { hide() }
        //backgroundColor = Color.YELLOW

    }

    override fun setupLayout(view: ViewGroup) {
        host = view
        val tvlp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            setMargins(0,20,0,20)
        }
        addView(dumpXY, tvlp)
        val tvlpParent = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        }
        host.addView(this, tvlpParent)
    }

    override fun destroyLayout() {
        host.removeView(this)
    }

    override fun dump(x: Int, y: Int) {
        dumpXY.text = "($x,$y)"
    }

    override val showing: Boolean
        get() = this.visibility == View.VISIBLE

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.INVISIBLE
    }

    override fun hideDelayed() {
        handle.postDelayed(hideRunnable, 1000)
    }

    var currentPos = 0F
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN->{
                if (hitingDump(event)) {
                    currentPos = event.rawY - y
                    return true
                }
            }
            MotionEvent.ACTION_MOVE ->{
                y = event.rawY - currentPos
                return true
            }
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP->{
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    private fun hitingDump(event: MotionEvent) = SubViewUtils.hiting(dumpXY, event, 0)
}


class AdvMoveHanle(ctx: Context):RelativeLayout(ctx),MoveHandle,HitingHandle{
    override val lastPt = PointF(0F,0F)
    override var target = TextView(ctx).apply {
        text = "没写字"
        backgroundColor = Color.BLUE
        textColor = Color.RED
    }
    override val meView = this
    override fun hiting(event: MotionEvent) = SubViewUtils.hiting(target, event)


    private lateinit var host:ViewGroup

    override fun setupLayout(view: ViewGroup) {
        host = view
        val tvlp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }
        addView(target, tvlp)
        val tvlpParent = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        }
        host.addView(this, tvlpParent)
    }

    override fun destroyLayout() {
        host.removeView(this)
    }

    override fun dump(x: Int, y: Int) {
        target.text = "($x,$y)"
    }

    override val showing: Boolean
        get() = this.visibility == View.VISIBLE

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.INVISIBLE
    }

    override fun hideDelayed() {
        //handle.postDelayed(hideRunnable, 1000)
    }
    init {
        this.visibility = View.INVISIBLE
        //backgroundColor = Color.YELLOW
    }

    override fun onTouchEvent(event: MotionEvent) = super.hitingMove(event){super.onTouchEvent(event)}
}

class DefaultMoveHandle(ctx:Context):RelativeLayout(ctx),MoveHandle{
    private val dumpXY:TextView
    private val handle:Handler
    private val hideRunnable:Runnable
    private lateinit var host:ViewGroup
    init {
        dumpXY = TextView(ctx).apply {
            text = "没写字"
            backgroundColor = Color.BLUE
            textColor = Color.RED
        }
        this.visibility = View.INVISIBLE
        handle = Handler()
        hideRunnable = Runnable { hide() }
        backgroundColor = Color.YELLOW
    }

    override fun setupLayout(view: ViewGroup) {
        host = view
        val tvlp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }
        addView(dumpXY, tvlp)
        val tvlpParent = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        }
        host.addView(this, tvlpParent)
    }

    override fun destroyLayout() {
        host.removeView(this)
    }

    override fun dump(x: Int, y: Int) {
        dumpXY.text = "($x,$y)"
    }

    override val showing: Boolean
        get() = this.visibility == View.VISIBLE

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.INVISIBLE
    }

    override fun hideDelayed() {
        handle.postDelayed(hideRunnable, 1000)
    }

    var currentPos = 0F
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN->{
                if (hitingDump(event)) {
                    currentPos = event.rawY - y
                    return true
                }
            }
            MotionEvent.ACTION_MOVE ->{
                y = event.rawY - currentPos
                return true
            }
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP->{
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    private fun hitingDump(event: MotionEvent) = SubViewUtils.hiting(dumpXY, event)
}