package sample.project

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.shockwave.pdfium.PdfiumCore
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.button
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import pdfbook.sample.stages.StorageUtils
import io.reactivex.schedulers.Schedulers
import io.reactivex.Flowable
import sample.Final.AppTick


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
    protected inline fun tag(action:(preFix:String)->Unit, tagName:String){
        info { "\nBegin: $tagName" }
        action.invoke("==>")
        info { "End: $tagName\n" }
    }
    protected var printPreFix = ""

    abstract fun listFiels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppTick.test_infoTick()
        //tag(this::testcreate, "testcreate")
        tag(this::testParallel, "testParallel")
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
    private fun testcreate(preFix: String = ""){
        printPreFix = preFix
        //Subscribe onNext:first onNext:two onComplete
        Observable.create(object :ObservableOnSubscribe<String>{
            override fun subscribe(p0: ObservableEmitter<String>) {
                val items = arrayListOf<String>("one", "two")
                items.forEach {
                    p0.onNext(it)
                }
                p0.onComplete()
            }
        })
                .subscribe(
                        this::comNext,
                        this::comError,
                        this::comComplete,
                        this::comSubscribe
                )
    }
    var parallelTick = 0L
    private fun testParallel(preFix: String = ""){
        info { "BeginThreadId:${Thread.currentThread().id}" }
        parallelTick = System.currentTimeMillis()
        Flowable.range(1, 10)
                .flatMap { v ->
                    Flowable.just(v)
                            .subscribeOn(Schedulers.io())
                            .map<Int> {
                                //按CPU核的数量启动
                                info { "MapThreadId:${Thread.currentThread().id}" }
                                //模拟耗时
                                Thread.sleep(2000)
                                return@map it*it
                            }
                }
                .blockingSubscribe(::comNextTick)
    }

    //region    com xxx
    private fun comNext(any: Any) = info { "${printPreFix}onNext:$any" }
    private fun comNextTick(any: Any) = info { "${printPreFix}[${System.currentTimeMillis()-parallelTick}]onNext:$any" }
    private fun comError(error: Throwable) = info { "${printPreFix}onError:${error.message}" }
    private fun comComplete()=info { "${printPreFix}onComplete" }
    private fun comSubscribe(scribe:Disposable) = info { "${printPreFix}Subscribe" }
    //endregion
}