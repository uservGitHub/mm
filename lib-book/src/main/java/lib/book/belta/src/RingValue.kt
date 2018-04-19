package lib.book.belta

import android.view.View
/**
 * 环行值
 * list 完全是引用，也可以是MutableList
 */
class RingValue<T> private constructor(private val list:List<T>, val tagColon:String) {
    companion object {
        /**
         * 集合不为空才返回有效环行值
         */
        fun <T> create(list: List<T>, tag: String): RingValue<T>? {
            return if (list.isEmpty() or tag.isEmpty()) null else RingValue(list, tag + ": ")
        }
    }

    //region    私有成员
    /**
     * -1 表示无效值
     */
    private var ind = -1

    private inline fun inc() {
        ind++
        adjustInd()
    }

    private inline fun dec() {
        ind--
        adjustInd()
    }

    private inline fun adjustInd() {
        if (ind < 0) ind += list.size
        ind = ind.rem(list.size)
    }
    //endregion

    init {
        //再次防护
        if (!list.isEmpty()) {
            ind = 0
        }
    }

    /**
     * 通过构造函数来防护，用不着
     */
    val hasValue: Boolean
        get() = ind != -1

    /**
     * 值（当前值）
     */
    val value: T
        get() = list[ind]

    var renderView: BaseView? = null
        private set

    fun bindUi(view: BaseView): View {
        if (renderView == null) {
            renderView = view.also {
                it.initEvents(::dec, ::inc, { "$tagColon$value" })
                //显示初始值
                it.updateView()
            }
        }
        return renderView!!.view
    }

    fun unbindUi() {
        //清理
        renderView?.initEvents({}, {}, { "" })
        renderView = null
    }
}