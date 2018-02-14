package pdfbook.sample.stages.skeleton

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewManager
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import pdfbook.sample.stages.ScreenUtils
import sample.skeleton.OuterHost

/**
 * Created by work on 2018/2/14.
 */
class MainActivity(): AppCompatActivity(){
    private lateinit var targetView: OuterHost
    private inline fun ViewManager.firstHost() = firstHost(){}
    private inline fun ViewManager.firstHost(init: OuterHost.()->Unit) =
            ankoView({ OuterHost(it) },0,init)

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            /*linearLayout {
                button("toggleEndToEnd"){
                    onClick {
                        targetView.toggleEndToEnd()
                    }
                }
                button("VisX++"){
                    onClick {
                        targetView.moveOffset(1F,0F)
                    }
                }
                button("VisX--"){
                    onClick {
                        targetView.moveOffset(-1F,0F)
                    }
                }
                button("VisY++"){
                    onClick {
                        targetView.moveOffset(0F,1F)
                    }
                }
                button("VisY--"){
                    onClick {
                        targetView.moveOffset(0F,-1F)
                    }
                }
            }*/
            targetView = firstHost() {
            }.lparams(width = matchParent, height = dip(0),weight = 1F)
        }
    }
    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}