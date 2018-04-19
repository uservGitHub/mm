package lib.book.belta

import android.view.View
interface BaseView {
    /**
     * 是否锁定，是：事件不再起作用
     */
    var locked: Boolean
    /**
     * 获取View，可用于呈现
     */
    val view: View

    /**
     * 初始化绑定事件
     */
    fun initEvents(left: () -> Unit, right: () -> Unit, fetch: () -> String)

    /**
     * 更新视图
     */
    fun updateView()
}