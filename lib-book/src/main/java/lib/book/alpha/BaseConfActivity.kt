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

    protected lateinit var numRangeA1:ValueRange
    protected lateinit var numRangeA2:ValueRange
    protected lateinit var numRangeB:BiValueRange

    val num1 = listOf<Int>(1,101,-200)
    val num2 = listOf<Float>(0.33F, -2.4F, .89F)
    val num3 = listOf<Int>(101,202,303)
    val str4 = listOf<String>("io方式","cpu方式", "Cache")

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
            button("打印上面的值"){
                onClick {
                    Toast.makeText(this@BaseConfActivity,
                            "${num1.get(numRangeA1.index)}\n${num2.get(numRangeA2.index)}",
                            Toast.LENGTH_LONG).show()
                }
            }
            rangePanelB = linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundColor = Color.LTGRAY

                //region    内容

                //endregion
            }.lparams(width = matchParent)
            button("打印上面的值"){
                onClick {
                    Toast.makeText(this@BaseConfActivity,
                            "${num3.get(numRangeB.index1)}\n${str4.get(numRangeB.index2)}",
                            Toast.LENGTH_LONG).show()
                }
            }

        }
        numRangeA1 = ValueRange(rangePanelA, num1)
        numRangeA2 = ValueRange(rangePanelA, num2)
        numRangeB = BiValueRange(rangePanelB, num3, str4)
    }

    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}