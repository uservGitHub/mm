package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pdfbook.sample.stages.StageUtils

/**
 * Created by work on 2018/4/4.
 */

class BootstrapActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StageUtils.defaultRender(this, true)
    }
}