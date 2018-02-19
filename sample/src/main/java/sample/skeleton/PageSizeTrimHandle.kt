package sample.skeleton

import android.graphics.Rect
import android.view.ViewGroup

/**
 * Created by Administrator on 2018/2/17.
 */

interface PageSizeTrimHandle{
    //依次是 左，上，右，下 4个值
    val num:IntArray
    val isLock:BooleanArray

    val showing: Boolean
    fun show()
    fun hide()
    fun setupLayout(view: ViewGroup, rect: Rect)
    fun destroyLayout()

}