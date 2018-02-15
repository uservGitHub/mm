package sample.skeleton

import android.content.Context
import android.view.View
import org.jetbrains.anko.*
import android.view.ViewGroup
import android.widget.*
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
        val midVerLine = LinearLayout(ctx).apply {
            addView(Button(ctx).apply{
                onClick {
                    host1.isFollow = !host1.isFollow
                    host2.isFollow = !host2.isFollow
                }
            })
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