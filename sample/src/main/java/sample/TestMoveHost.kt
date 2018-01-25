package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import pdfbook.sample.stages.ScreenUtils
import sample.common.MoveHost


/**
 * Created by work on 2018/1/25.
 */

class TestMoveHost():AppCompatActivity(){
    private lateinit var targetView: MoveHost
    private inline fun ViewManager.simpleHost() = moveHost(){}
    private inline fun ViewManager.moveHost(init: MoveHost.()->Unit) =
            ankoView({ MoveHost(it) },0,init)

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            targetView = moveHost() {

            }.lparams(width = matchParent, height = dip(0),weight = 1F)
        }
    }
}