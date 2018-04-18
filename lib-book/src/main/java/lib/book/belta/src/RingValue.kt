package lib.book.belta

/**
 * 环行值
 * list 完全是引用，也可以是MutableList
 */
class RingValue<T> private constructor(private val list:List<T>){
    companion object {
        /**
         * 集合不为空才返回有效环行值
         */
        fun <T> create(list: List<T>): RingValue<T>? {
            return if (list.isEmpty()) null else RingValue(list)
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
        if (!list.isEmpty()){
            ind = 0
        }
    }

    /**
     * 通过构造函数来防护，用不着
     */
    val hasValue:Boolean
        get() = ind != -1

    /**
     * 值（当前值）
     */
    val value:T
        get() = list[ind]

    var renderView:RingBaseView? = null
        private set

    fun bindUi(view:RingBaseView){
        if (renderView == null){
            renderView = view

            view.setOnFetchText { "$value" }
            view.setOnLeftClick { dec() }
            view.setOnRightClick { inc() }
            view.updateText()
        }
    }
    fun unbindUi(){
        if (renderView != null){
            //清理
            //renderView.
        }
        renderView = null
    }

    /*var renderView: RingValueView? = null
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
    }*/


}