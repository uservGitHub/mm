package lib.book.alpha

import android.content.Context
import android.view.View

/**
 * Created by work on 2018/4/11.
 */


/**
 * 环行值
 * list 完全是引用，也可以是MutableList
 */
class RingValue<T> private constructor(private val list:List<T>) {
    companion object {
        fun <T> create(list: List<T>): RingValue<T>? {
            return if (list.isEmpty()) null else RingValue<T>(list)
        }
    }

    //region    以后要修改为私有成员
    internal var ind = -1
        private set

    internal inline fun inc() {
        ind++
        adjustInd()
    }

    internal inline fun dec() {
        ind--
        adjustInd()
    }
    //endregion

    private inline fun adjustInd() {
        if (ind < 0) ind += list.size
        ind = ind.rem(list.size)
    }

    val selected: T
        get() = list[ind]

    var renderView: RingValueView? = null
        private set

    fun bindUi(ctx: Context, op: RingValueView.() -> Unit): View {
        if (renderView == null) {
            renderView = RingValueView(ctx)
            renderView!!.op()
        }
        return renderView!!.group
    }

    fun bindUi(ctx: Context): View {
        if (renderView == null) {
            renderView = RingValueView(ctx).also {
                it.setOnFetchText { "${this.selected}" }
                it.setOnClick { dec() }
                it.setOnAnotherClick { inc() }
                it.updateText()
            }
        }
        return renderView!!.group
    }

    init {
        ind = 0
    }
}