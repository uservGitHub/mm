package sample.project

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.shockwave.pdfium.PdfiumCore
import org.jetbrains.anko.button
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import pdfbook.sample.stages.StorageUtils

/**
 * Created by Administrator on 2018/3/5.
 */

abstract class BaseListFileActivity:AppCompatActivity(){
    protected lateinit var btnListFiles:Button
    protected val dir = StorageUtils.inPdfRoot
    protected lateinit var core: PdfiumCore

    abstract fun listFiels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        core = PdfiumCore(ctx)
        verticalLayout {
            btnListFiles = button().apply {
                text = "查询"
                onClick {
                    listFiels()
                }
            }

        }
    }
}