package pdfbook.sample.stages

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat.startActivity
import android.view.Gravity
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import sample.Final.listfiles.ListfilesActivity
import sample.project.ListFileActivity


/**
 * Created by Administrator on 2017/12/31.
 */

class StageUtils {
    companion object {
        //region    页面流转
        fun pass(activity: Activity) {
            when (activity.localClassName) {
                Stage_1_1::class.java.simpleName -> startActivity(activity, Intent(activity, Stage_2_1::class.java), null)
                Stage_2_1::class.java.simpleName -> {
                    //startActivity(activity, Intent(activity, RxjavaActivity::class.java), null)
                    //startActivity(activity, Intent(activity, ListFileActivity::class.java), null)
                    startActivity(activity, Intent(activity, ListfilesActivity::class.java), null)
                }
                else -> startActivity(activity, Intent(activity, Stage_1_1::class.java), null)
            }
        }

        //endregion
        //region    默认UI呈现，类名称
        fun defaultRender(activity: Activity, autoPass: Boolean = false) {
            activity.verticalLayout {
                textView() {
                    text = activity.localClassName
                    textSize = 34F
                    textColor = Color.BLUE
                    gravity = Gravity.CENTER
                }.lparams(width = matchParent, height = matchParent)
            }.apply {
                if (autoPass) pass(activity)
            }
        }
        //endregion
    }
}