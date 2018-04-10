package lib.book.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.dip

/**
 * Created by Administrator on 2018/4/9.
 */

class ControlUtils{
    companion object {
        val tbWidth = 60
        val btnWidth = 20
        fun buildBtn(ctx:Context):Button{
            return Button(ctx).apply {
                width = dip(btnWidth)
                setPadding(dip(4),dip(3),dip(4),dip(3))
                minWidth = dip(0)
            }
        }
        fun buildTb(ctx: Context):TextView{
            return TextView(ctx).apply {
                width = dip(tbWidth)
            }
        }
        fun buildBiTb(ctx: Context):TextView{
            return TextView(ctx).apply {
                width = dip(2*tbWidth)
            }
        }
    }
}