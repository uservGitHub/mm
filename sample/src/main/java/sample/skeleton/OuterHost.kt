package sample.skeleton

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.*
import android.view.ViewGroup
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.common.BackCell


/**
 * Created by work on 2018/2/14.
 */

class OuterHost(ctx:Context):LinearLayout(ctx),AnkoLogger {
    private val host1: ScreenHost
    private val host2: ScreenHost
    private val dragPinchManager: DragPinchManager
    private val backCell:BackCell

    init {
        backCell = BackCell()
        host1 = ScreenHost(ctx, backCell).apply { hostId = "-first" }
        host2 = ScreenHost(ctx, backCell).apply { hostId = "-second" }
        val midVerLine = Button(ctx).apply {
            onClick {

            }
        }
        dragPinchManager = DragPinchManager(listOf(host1, host2),ctx).apply {
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

    }


}