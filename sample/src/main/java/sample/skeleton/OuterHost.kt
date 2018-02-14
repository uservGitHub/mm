package sample.skeleton

import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.*
import android.view.ViewGroup



/**
 * Created by work on 2018/2/14.
 */

class OuterHost(ctx:Context):LinearLayout(ctx),AnkoLogger {
    private val host1: Host
    private val host2: Host
    private val dragPinchManager: DragPinchManager

    init {
        host1 = Host(ctx, "one")
        host2 = Host(ctx, "two")
        dragPinchManager = DragPinchManager(ctx, listOf(host1, host2)).apply {
            enable()
        }
        try{
            val tvlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT).apply {
                width = dip(0)
                weight = 1F
            }
            addView(host1, tvlp)
            addView(host2, tvlp)

        }catch (e:Exception){
            info { e.toString() }
        }

    }


}