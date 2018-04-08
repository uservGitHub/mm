package lib.book.alpha

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import lib.book.utils.ScreenUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick


/**
 * Created by work on 2018/4/4.
 */

abstract class BaseRxActivity:AppCompatActivity() {

    //region    布局要求
    protected lateinit var btnPanel: LinearLayout
    protected lateinit var contentPanel: LinearLayout
    private lateinit var tbDump: TextView

    //必须使用bindBtnFn进行添加
    protected abstract fun layoutSetup()

    protected fun bindBtnFn(title: String, code: () -> Unit) {
        btnList.add(BtnItem(title, code))
    }
    //endregion

    //region    公共方法
    fun updateDump(text: String) {
        tbDump.text = text
    }

    fun appendUiDump(text: String){
        runOnUiThread {
            tbDump.append(text)
        }
    }

    fun appendDump(text: String) {
        tbDump.append(text)
    }

    fun clearDump() {
        tbDump.text = ""
    }
    //endregion

    //region    业务要求
    /*protected var dirRoot = StorageUtils.inPdfRoot
        private set*/
    //endregion

    //region    控制属性
    protected var isLogv = false
        private set
    protected var isFlow = false
        private set
    protected var isPill = false
        private set

    protected var t1 = 100
        private set
    protected var t2 = 100
        private set

    //endregion

    //region    UI控制
    //锁定面板
    protected val enterBtns: (Button) -> Unit = { v ->
        val count = btnPanel.childCount
        if (count > 0) {
            for (i in 0..count - 1) {
                btnPanel.getChildAt(i).isEnabled = false
            }
        }
        btnList.forEach {
            it.view.isEnabled = false
            if (it.view.text.last() == '。') {
                val count = it.view.text.length - 1
                it.view.text = it.view.text.substring(0, count)
            }
        }
        v.text = "${v.text}。"
    }
    //释放面板
    protected val releaseUiBtns: () -> Unit = {
        runOnUiThread {
            val count = btnPanel.childCount
            if (count > 0) {
                for (i in 0..count - 1) {
                    btnPanel.getChildAt(i).isEnabled = true
                }
            }
            btnList.forEach {
                it.view.isEnabled = true
            }
        }
    }
    //显示输出信息 = appendUiDump
    /*protected val showDump: (String)->Unit = {
        runOnUiThread {
            appendDump(it)
        }
    }*/
    //endregion

    //访问指定的View
    protected fun viewFromTitle(title: String):View {
        return btnList.first { title == it.title }.view
    }

    private val btnList = mutableListOf<BtnItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //region    布局
        //无标题
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)

        //region    Install
        layoutSetup()
        //endregion

        verticalLayout {
            orientation = LinearLayout.VERTICAL
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.YELLOW

                btnPanel = linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    //region    固定设置
                    button("Clr") {
                        onClick { clearDump() }
                    }
                    checkBox("Flow") {
                        onCheckedChange { buttonView, isChecked ->
                            isFlow = isChecked
                        }
                    }
                    checkBox("Pill") {
                        onCheckedChange { buttonView, isChecked ->
                            isPill = isChecked
                        }
                    }
                    checkBox("Log") {
                        onCheckedChange { buttonView, isChecked ->
                            isLogv = isChecked
                        }
                    }
                    //endregion
                }
                horizontalScrollView {
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        //按照实际功能动态添加
                        btnList.forEach { btn ->
                            button() {
                                btn.view = this
                                text = btn.title
                                onClick { sender ->
                                    //sender?.isEnabled = false
                                    //锁定全部功能按钮
                                    enterBtns.invoke(btn.view)
                                    //执行Code
                                    btn.code.invoke()
                                }
                            }
                        }
                    }
                }.lparams(0, wrapContent, 1F)
            }.lparams(matchParent, wrapContent)
            contentPanel = linearLayout {
                orientation = LinearLayout.VERTICAL
                backgroundColor = Color.GREEN

                tbDump = textView() {
                    textSize = 22F
                    gravity = Gravity.LEFT
                    typeface = Typeface.MONOSPACE
                    movementMethod = ScrollingMovementMethod.getInstance()
                }.lparams(matchParent, matchParent)
            }.lparams(matchParent, 0, 1F)
        }
        //endregion
    }

    override fun onResume() {
        super.onResume()
        //全屏
        ScreenUtils.fullScreen(this)
    }

    data class BtnItem(val title: String, val code: () -> Unit) {
        lateinit var view: Button
    }
}
