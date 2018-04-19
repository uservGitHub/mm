package lib.book.belta


class RingView(ctx:Context):View(ctx),BaseView {


    override var locked: Boolean = true
    override val view: View
        get() = this

    private inline fun refresh(){
        val text = fetchFun?.invoke() ?: "null"
        println("refresh:\t$text")
    }
    internal inline fun leftClick(){
        leftClick?.let {
            it.invoke()
            updateView()
        }
    }
    internal inline fun rightClick(){
        rightClick?.let {
            it.invoke()
            updateView()
        }
    }
    private var leftClick: (() -> Unit)? = null
    private var rightClick: (() -> Unit)? = null
    private var fetchFun: (() -> String)? = null
    override fun initEvents(left: () -> Unit, right: () -> Unit, fetch: () -> String) {
        leftClick = left
        rightClick = right
        fetchFun = fetch
    }
    override fun updateView() {
        refresh()
    }
}