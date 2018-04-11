package lib.book.alpha

import android.content.Context
import android.view.View

/**
 * Created by work on 2018/4/11.
 */

class RingBiValue<T,V> private constructor(private val list1:List<T>,private val list2: List<V>) {
    companion object {
        fun <T, V> create(list1: List<T>, list2: List<V>): RingBiValue<T, V>? {
            return if (list1.isEmpty() or list2.isEmpty()) null
            else RingBiValue<T, V>(list1, list2)
        }
    }

    //region    以后要修改成私有成员
    internal var ind1 = -1
        private set
    internal var ind2 = -1
        private set
    internal inline fun inc(){
        ind1++
        ind2++
        adjustInd()
    }
    internal inline fun dec(){
        ind1--
        ind2--
        adjustInd()
    }
    //endregion

    val selected1: T
        get() = list1[ind1]
    val selected2: V
        get() = list2[ind2]

    var renderView: RingValueView? = null
        private set

    fun bindUi(ctx: Context, op: RingValueView.() -> Unit): View {
        if (renderView == null) {
            renderView = RingValueView(ctx, true)
            renderView!!.op()
        }
        return renderView!!.group
    }

    fun bindUi(ctx: Context): View {
        if (renderView == null) {
            renderView = RingValueView(ctx, true).also {
                it.setOnFetchText { "${this.selected1},${this.selected2}" }
                it.setOnClick { dec() }
                it.setOnAnotherClick { inc() }
                it.updateText()
            }
        }
        return renderView!!.group
    }

    private inline fun adjustInd() {
        if (ind1 < 0) ind1 += list1.size
        ind1 = ind1.rem(list1.size)
        if (ind2 < 0) ind2 += list2.size
        ind2 = ind2.rem(list2.size)
    }

    init {
        ind1 = 0
        ind2 = 0
    }
}

