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
import sample.end.OutHost

/**
 * Created by work on 2018/1/25.
 */

class TestOutHost(): AppCompatActivity(){
    private lateinit var targetView: OutHost
    private inline fun ViewManager.outHost() = outHost(){}
    private inline fun ViewManager.outHost(init: OutHost.()->Unit) =
            ankoView({ OutHost(it) },0,init)

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            targetView = outHost() {
            }.lparams(width = matchParent, height = dip(0),weight = 1F)
        }
    }
    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}