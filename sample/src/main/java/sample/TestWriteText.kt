package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewManager
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.common.TextHost

/**
 * Created by work on 2018/1/25.
 */

class TestWriteText(): AppCompatActivity(){
    private lateinit var targetView: TextHost
    private inline fun ViewManager.textHost() = textHost(){}
    private inline fun ViewManager.textHost(init: TextHost.()->Unit) =
            ankoView({ TextHost(it) },0,init)

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            linearLayout {
                button("Next"){
                    onClick {
                        targetView.nextText()
                    }
                }
            }
            targetView = textHost() {
            }.lparams(width = matchParent, height = dip(0),weight = 1F)
        }
    }
    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}