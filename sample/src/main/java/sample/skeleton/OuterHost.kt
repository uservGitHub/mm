package sample.skeleton

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.*
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.common.BackCell
import sample.hander.SplitMent
import sample.hander.SplitView


/**
 * Created by work on 2018/2/14.
 */

class OuterHost(ctx:Context):RelativeLayout(ctx),AnkoLogger{
    override val loggerTag: String
        get() = "_OH"
    //private val host1: ScreenHost
    //private val host2: ScreenHost
    private val dragPinchManager: DragPinchManager
    private val backCell:BackCell
    private val splitMent:SplitMent

    private fun doubleClick(event: MotionEvent):Boolean{
        if (splitMent.showing){
            splitMent.hide()
        }else{
            splitMent.show()
        }
        info { event }
        return true
    }
    init {
        backCell = BackCell()
        //list = MutableList(1,{ ScreenHost(ctx, backCell).apply { hostId = "-first" } })
        val first = ScreenHost(ctx, backCell).apply { hostId = "-first" }
        dragPinchManager = DragPinchManager(first, context).apply {
            setDoublClickListener(this@OuterHost::doubleClick)
            enable()
        }
        splitMent = SplitView(ctx).apply {
            setupLayout(this@OuterHost, first,
                    {now: DragPinchRawDriver ->
                        ScreenHost(context, backCell).apply {
                            hostId = "-second"
                            dragPinchManager.addDriver(this)
                        }
                    },
                    {target: DragPinchRawDriver ->
                        dragPinchManager.removeDriver(target)
                        info { "closeTarget:${target.isFollow}" }
                    })
        }
    }
    /*init {
        backCell = BackCell()
        host1 = ScreenHost(ctx, backCell).apply { hostId = "-first" }
        host2 = ScreenHost(ctx, backCell).apply { hostId = "-second" }
        val midVerLine = LinearLayout(ctx).apply {
            addView(Button(ctx).apply{
                onClick {
                    host1.isFollow = !host1.isFollow
                    host2.isFollow = !host2.isFollow
                }
            })
        }

        dragPinchManager = DragPinchManager(listOf(host1, host2),context).apply {
            setDoublClickListener(this@OuterHost::doubleClick)
            enable()
        }
        try{
            val tvlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(0)
                weight = 1F
            }
            val midlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(24)
            }
            addView(host1, tvlp)
            addView(midVerLine, midlp)
            addView(host2, tvlp)

        }catch (e:Exception){
            info { e.toString() }
        }
    }*/
}