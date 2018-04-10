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

class BiValueRange(container: ViewGroup, val list1:List<Any>, val list2:List<Any>){
    private val btnLeft: Button
    private val btnRight: Button
    private val tbValue: TextView
    var index1 = -1
        private set
    var index2 = -1
        private set
    private inline fun validFromIndex1():Int {
        if (index1 < 0) index1 += list1.size
        return index1.rem(list1.size)
    }
    private inline fun validFromIndex2():Int {
        if (index2 < 0) index2 += list2.size
        return index2.rem(list2.size)
    }
    private fun updateValue(){
        tbValue.text = "${list1.get(index1)},${list2.get(index2)}"
    }
    init {
        index1 = 0
        index2 = 0
        val ctx = container.context
        btnLeft = ControlUtils.buildBtn(ctx).apply {
            text = "【"
            onClick {
                index1--
                index2--
                index1 = validFromIndex1()
                index2 = validFromIndex2()
                updateValue()
            }
        }
        btnRight = ControlUtils.buildBtn(ctx).apply {
            text = "】"
            onClick {
                index1++
                index2++
                index1 = validFromIndex1()
                index2 = validFromIndex2()
                updateValue()
            }
        }
        tbValue = ControlUtils.buildBiTb(ctx).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        container.addView(btnLeft)
        container.addView(tbValue)
        container.addView(btnRight)

        updateValue()
    }
}