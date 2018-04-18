package lib.book.belta

abstract class RingBaseView {
    protected var leftClick: (() -> Unit)? = null
    fun setOnLeftClick(f: () -> Unit) {
        leftClick = f
    }

    protected var rightClick: (() -> Unit)? = null
    fun setOnRightClick(f: () -> Unit) {
        rightClick = f
    }

    protected var fetchAction: (() -> String)? = null
    fun setOnFetchText(f: () -> String) {
        fetchAction = f
    }

    abstract fun updateText()
}