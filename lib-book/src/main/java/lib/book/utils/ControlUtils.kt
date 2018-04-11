package lib.book.utils

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent

/**
 * Created by Administrator on 2018/4/9.
 */

class ControlUtils {
    companion object {
        val btnWidthDip = 20
        val tbWidthDip = 40

        fun buildBtn(ctx: Context, isLeft: Boolean = true): Button {
            return Button(ctx).apply {
                minWidth = dip(0)
                minHeight = dip(0)
                layoutParams = ViewGroup.LayoutParams(dip(btnWidthDip), wrapContent)
                setPadding(0, 0, 0, 0)
            }
        }

        fun buildTb(ctx: Context, isBi: Boolean): TextView {
            return TextView(ctx).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                val width = if (isBi) 2 * tbWidthDip + 4 else tbWidthDip
                layoutParams = ViewGroup.LayoutParams(dip(width), wrapContent)
            }
        }
    }
}