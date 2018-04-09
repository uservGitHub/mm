package lib.book.alpha

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import lib.book.utils.ScreenUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Administrator on 2018/4/9.
 */

abstract class BaseConfActivity:AppCompatActivity(){

    protected lateinit var rangePanelA:LinearLayout
    protected lateinit var rangePanelB:LinearLayout

    protected lateinit var numRangeA:ValueRange
    protected lateinit var numRangeB:ValueRange
    protected lateinit var btnToast:Button

    val num1 = listOf<Int>(1,101,-200)
    val num2 = listOf<Float>(0.33F, -2.4F, .89F)

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            orientation = LinearLayout.VERTICAL
            backgroundColor = Color.YELLOW

            rangePanelA = linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.GREEN

                //region    内容

                //endregion
            }.lparams(width= matchParent)

            rangePanelB = linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.LTGRAY

                //region    内容

                //endregion
            }.lparams(width = matchParent)

            btnToast = button("打印当前值"){
                onClick {
                    Toast.makeText(this@BaseConfActivity, "${num2.get(numRangeB.index)}", Toast.LENGTH_LONG).show()
                }
            }
        }



        numRangeA = ValueRange(rangePanelA, num1)
        numRangeB = ValueRange(rangePanelB, num2)
    }

    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}