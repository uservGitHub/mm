package lib.book.alpha

import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import lib.book.utils.ControlUtils
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Administrator on 2018/4/9.
 */

class ValueRange(container: ViewGroup, val list:List<Any>){
    private val btnLeft:Button
    private val btnRight:Button
    private val tbValue:TextView
    var index = -1
        private set
    private inline fun validFromIndex():Int {
        if (index < 0) index += list.size
        return index.rem(list.size)
    }
    private fun updateValue(){
        tbValue.text = list.get(index).toString()
    }
    init {
        index = 0
        val ctx = container.context
        btnLeft = ControlUtils.buildBtn(ctx).apply {
            text = "【"
            onClick {
                index--
                index = validFromIndex()
                updateValue()
            }
        }
        btnRight = ControlUtils.buildBtn(ctx).apply {
            text = "】"
            onClick {
                index++
                index = validFromIndex()
                updateValue()
            }
        }
        tbValue = ControlUtils.buildTb(ctx).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        container.addView(btnLeft)
        container.addView(tbValue)
        container.addView(btnRight)

        updateValue()
    }
}