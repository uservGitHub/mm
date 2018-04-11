package lib.book.alpha

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import lib.book.utils.ControlUtils
import org.jetbrains.anko.dip
import org.jetbrains.anko.wrapContent

/**
 * Created by work on 2018/4/11.
 */

class RingValueView(ctx:Context, isBi:Boolean = false) {
    private val btnLeft: Button
    private val btnRight: Button
    private val tbValue: TextView

    val group:LinearLayout

    init {
        group = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        btnLeft = ControlUtils.buildBtn(ctx).also {
            it.text = "【"
            it.setOnClickListener {
                click?.invoke()
                updateText()
            }
            group.addView(it)

        }
        tbValue = ControlUtils.buildTb(ctx, isBi).also {
            it.text = ""
            group.addView(it)
        }
        btnRight = ControlUtils.buildBtn(ctx).also {
            it.text = "】"
            it.setOnClickListener {
                anotherClick?.invoke()
                updateText()
            }
            group.addView(it)
        }
    }

    var click: (() -> Unit)? = null
        private set
    var anotherClick: (() -> Unit)? = null
        private set
    var fetchText: (() -> String)? = null
        private set

    fun setOnFetchText(f: () -> String) {
        fetchText = f
    }

    fun setOnClick(f: () -> Unit) {
        click = f
    }

    fun setOnAnotherClick(f: () -> Unit) {
        anotherClick = f
    }

    fun updateText() {
        tbValue.text = fetchText?.invoke() ?: "null"
    }

   /* private fun holdClick() {
        //操作
        click?.invoke()
        //显示
        updateText()
    }

    private fun holdAnotherClick() {
        //操作
        anotherClick?.invoke()
        //显示
        updateText()
    }*/
}