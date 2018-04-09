package lib.book.utils

import android.content.Context
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.dip

/**
 * Created by Administrator on 2018/4/9.
 */

class ControlUtils{
    companion object {
        val tbWidth = 40
        fun buildBtn(ctx:Context):Button{
            return Button(ctx).apply {

            }
        }
        fun buildTb(ctx: Context):TextView{
            return TextView(ctx).apply {
                width = dip(tbWidth)
            }
        }
    }
}