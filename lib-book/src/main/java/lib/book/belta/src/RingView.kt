package lib.book.belta

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import lib.book.alpha.SplitGestureManager
import lib.book.utils.DrawUtils
import lib.book.utils.DrawUtils.Companion.drawDefaultText

class RingView(ctx:Context):View(ctx),BaseView {
    private val gestureManager:SplitGestureManager
    init {
        gestureManager = SplitGestureManager(ctx, this)
    }
    private fun onClick(v: View) {
        if (locked){
            return
        }
        Toast.makeText(context,"${width}px\n${height}px",Toast.LENGTH_SHORT).show()
        //invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val text = fetchFun?.invoke() ?: "null"
        drawDefaultText(canvas, text)
    }
    override var locked: Boolean = false
    override val view: View
        get() = this

    private inline fun refresh(){
        val text = fetchFun?.invoke() ?: "null"
        //println("refresh:\t$text")
    }
    internal inline fun leftClick(){
        leftClick?.let {
            it.invoke()
            updateView()
        }
    }
    internal inline fun rightClick(){
        rightClick?.let {
            it.invoke()
            updateView()
        }
    }
    private var leftClick: (() -> Unit)? = null
    private var rightClick: (() -> Unit)? = null
    private var fetchFun: (() -> String)? = null
    override fun initEvents(left: () -> Unit, right: () -> Unit, fetch: () -> String) {
        leftClick = left
        rightClick = right
        fetchFun = fetch
    }
    override fun updateView() {
        refresh()
    }
}