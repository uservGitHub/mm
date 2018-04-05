package sample.Final.listfiles

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import pdfbook.sample.stages.ScreenUtils
import pdfbook.sample.stages.StorageUtils
import sample.Final.AppTick
import sample.project.AppConfigure

/**
 * Created by work on 2018/4/4.
 */

abstract class BaseListfilesActivity:AppCompatActivity(){

    //region    布局要求
    protected lateinit var btnPanel:LinearLayout
    protected lateinit var contentPanel:LinearLayout
    private lateinit var tbDump:TextView

    //必须使用bindBtnFn进行添加
    protected abstract fun layoutSetup()
    protected fun bindBtnFn(title:String, code:()->Unit){
        btnList.add(BtnItem(title, code))
    }
    //endregion

    fun updateDump(text:String){
        tbDump.text = text
    }

    //region    业务要求
    protected var dirRoot = StorageUtils.inPdfRoot
        private set
    //endregion

    private val btnList = mutableListOf<BtnItem>()
    private val lastHistoryMessage:String
        get() = AppTick.lastDump

    override fun onCreate(savedInstanceState: Bundle?) {
        //region    布局
        //无标题
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)

        //region    Setup
        //先自身
        bindBtnFn("Dump"){
            Toast.makeText(this, lastHistoryMessage, Toast.LENGTH_LONG).show()
        }
        //再子类
        layoutSetup()
        //endregion

        verticalLayout {
            orientation = LinearLayout.VERTICAL
            btnPanel = linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.YELLOW
                //按照实际功能进行添加
                btnList.forEach { btn->
                    button(){
                        text = btn.title
                        onClick {

                            if (btn.title != "Dump"){
                                //清空计数和输出
                                AppTick.resetTick()
                            }

                            //执行Code
                            btn.code.invoke()
                        }
                    }
                }
            }.lparams(matchParent, wrapContent)
            contentPanel = linearLayout{
                orientation = LinearLayout.VERTICAL
                backgroundColor = Color.GREEN

                tbDump = textView(){
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

    data class BtnItem(val title: String, val code: () -> Unit)
}