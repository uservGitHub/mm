package sample.project

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.shockwave.pdfium.PdfiumCore
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.button
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import pdfbook.sample.stages.StorageUtils

/**
 * Created by Administrator on 2018/3/5.
 */

abstract class BaseListFileActivity:AppCompatActivity(){
    protected inline fun info(msg:()->Any) {
        Log.v("LFA", "${msg()}")
    }
    protected lateinit var btnListFiles:Button
    protected val dir = StorageUtils.inPdfRoot
    protected lateinit var core: PdfiumCore
    protected inline fun tag(action:()->Unit, tagName:String){
        info { "\nBegin: $tagName" }
        action.invoke()
        info { "End: $tagName\n" }
    }

    abstract fun listFiels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tag(this::testfromIterable, "fromIterable")
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
    private fun testfromIterable(){
        //Subscribe onNext:first onNext:two onComplete
        Observable.fromIterable(arrayListOf("first", "two"))
                .subscribe(
                        this::comNext,
                        this::comError,
                        this::comComplete,
                        this::comSubscribe
                )
    }
    private fun testjust(){
        //Subscribe onNext:first onNext:two onComplete
        Observable.just("first", "two")
                .subscribe(
                        this::comNext,
                        this::comError,
                        this::comComplete,
                        this::comSubscribe
                )
    }

    //region    com xxx
    private fun comNext(any: Any) = info { "onNext:$any" }
    private fun comError(error: Throwable) = info { "onError:${error.message}" }
    private fun comComplete()=info { "onComplete" }
    private fun comSubscribe(scribe:Disposable) = info { "Subscribe" }
    //endregion
}