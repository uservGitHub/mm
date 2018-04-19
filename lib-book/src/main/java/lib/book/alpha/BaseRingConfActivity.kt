package lib.book.alpha

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.INotificationSideChannel
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import lib.book.utils.ScreenUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import lib.book.belta.*
import lib.book.utils.ControlUtils
import lib.book.utils.DrawUtils

/**
 * Created by Administrator on 2018/4/9.
 */

abstract class BaseRingConfActivity : AppCompatActivity(){
    protected lateinit var configPanel:LinearLayout
    protected lateinit var ringValuePanel:LinearLayout

    protected lateinit var btnConfigSave:Button
    protected lateinit var rvFontSizes:lib.book.belta.RingValue<Float>
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
                //看下面
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
        val fontSizes = 20.rangeTo(40).step(5).map { it.toFloat() }
        val fontNames = listOf<String>("宋体", "幼圆", "仿宋")
        val lp = ViewGroup.LayoutParams(dip(230),dip(36))

        /*rvFontNames = fontNames.toRing(applicationContext, "FontName"){
            configPanel.addView(it, lp)
        }!!*/
        /*rvFontSizes = fontSizes.toRing(applicationContext, "FontSize"){
            configPanel.addView(it, lp)
        }!!*/
        DrawUtils.FONT_RINGVALUE.bindUi(RingView(applicationContext)).let {
            configPanel.addView(it, lp)
        }
        DrawUtils.FACE_RINGVALUE.bindUi(RingView(applicationContext)).let {
            configPanel.addView(it, lp)
        }
        val btn = ControlUtils.defaultBtn(ctx).apply {
            onClick {
                Toast.makeText(this@BaseRingConfActivity,
                        "${DrawUtils.FONT_RINGVALUE.value}",
                        Toast.LENGTH_LONG).show()
            }
            configPanel.addView(this)
        }
        //endregion
    }

    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}