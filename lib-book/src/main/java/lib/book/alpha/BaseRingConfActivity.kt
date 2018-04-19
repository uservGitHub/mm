package lib.book.alpha

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import lib.book.utils.ScreenUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import lib.book.belta.*

/**
 * Created by Administrator on 2018/4/9.
 */

abstract class BaseRingConfActivity : AppCompatActivity(){
    protected lateinit var configPanel:LinearLayout
    protected lateinit var ringValuePanel:LinearLayout

    protected lateinit var btnConfigSave:Button
    protected lateinit var rvFontSizeConfig:lib.book.belta.RingValue<Float>
    protected lateinit var rvFontNames:lib.book.belta.RingValue<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            orientation = LinearLayout.VERTICAL
            backgroundColor = Color.YELLOW

            configPanel = linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.GREEN


            }.lparams(width= matchParent)
            button("打印上面的值"){
                onClick {
                    Toast.makeText(this@BaseRingConfActivity,
                            "${rvFontNames.value}",
                            Toast.LENGTH_LONG).show()
                }
            }
        }

        //region    内容
        rvFontNames = listOf<String>("abc","def")
                .toRing(applicationContext,"Font",{
                    val lp = ViewGroup.LayoutParams(dip(400), dip(40))
                    configPanel.addView(it, lp)
                })!!
        //endregion
    }

    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}