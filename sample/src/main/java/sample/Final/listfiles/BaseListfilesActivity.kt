package sample.Final.listfiles

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import pdfbook.sample.stages.ScreenUtils
import pdfbook.sample.stages.StorageUtils
import sample.Final.AppTick
import sample.project.AppConfigure

/**
 * Created by work on 2018/4/4.
 */

abstract class BaseListfilesActivity:AppCompatActivity() {

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
    protected var dirRoot = StorageUtils.inPdfRoot
        private set
    //endregion

    //region    控制属性
    protected var isLogv = false
        private set
    protected var isFlow = false
        private set
    protected var isBreak = false
        private set
    //endregion

    //region    UI控制
    //锁定面板
    protected val enterBtns: () -> Unit = {
        val count = btnPanel.childCount
        if (count>0){
            for (i in 0..count-1){
                btnPanel.getChildAt(i).isEnabled = false
            }
        }
    }
    //释放面板
    protected val releaseUiBtns: () -> Unit = {
        val count = btnPanel.childCount
        if (count > 0) {
            runOnUiThread {
                for (i in 0..count - 1) {
                    btnPanel.getChildAt(i).isEnabled = true
                }
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
            btnPanel = linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.YELLOW

                //region    固定设置
                button("Clear") {
                    onClick { clearDump() }
                }
                checkBox("Flow") {
                    onCheckedChange { buttonView, isChecked ->
                        isFlow = isChecked
                    }
                }
                checkBox("Logv") {
                    onCheckedChange { buttonView, isChecked ->
                        isLogv = isChecked
                    }
                }
                checkBox("Break") {
                    onCheckedChange { buttonView, isChecked ->
                        isBreak = isChecked
                    }
                }
                //endregion

                //按照实际功能动态添加
                btnList.forEach { btn ->
                    button() {
                        btn.view = this
                        text = btn.title
                        onClick { sender ->
                            //sender?.isEnabled = false
                            //锁定全部功能按钮
                            enterBtns.invoke()
                            //执行Code
                            btn.code.invoke()
                        }
                    }
                }
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
        lateinit var view: View
    }
}