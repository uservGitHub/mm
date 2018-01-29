package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import pdfbook.sample.stages.ScreenUtils
import sample.common.FirstHost

/**
 * Created by work on 2018/1/25.
 */

class TestFirstHost(): AppCompatActivity(){
    private lateinit var targetView: FirstHost
    private inline fun ViewManager.firstHost() = firstHost(){}
    private inline fun ViewManager.firstHost(init: FirstHost.()->Unit) =
            ankoView({ FirstHost(it) },0,init)

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            targetView = firstHost() {

            }.lparams(width = matchParent, height = dip(0),weight = 1F)
        }
    }
}