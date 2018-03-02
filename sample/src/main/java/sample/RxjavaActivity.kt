package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

/**
 * Created by work on 2018/3/2.
 * http://blog.csdn.net/DeMonliuhui/article/details/77848691
 * http://blog.csdn.net/qq_35064774/article/details/53045298
 */

fun info(msg:()->Any) {
    Log.v("_Rx", "${msg()}")
}

class RxjavaActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "onCreate ===" }
        test2()
    }

    companion object {
        /**
         * onSubscribe,on[Next,Error,Complete]
         */
        fun test1() {
            Observable.create<Int> {
                try {
                    if (!it.isDisposed) {
                        for (i in 1 until 5) {
                            it.onNext(i)
                        }
                        it.onComplete()
                    }
                } catch (e: Exception) {
                    it.onError(e)
                } finally {
                    info { "finally" }
                }
            }.subscribe(
                    { info { "Next: $it" } },
                    { info { "Error: ${it.message}" } },
                    { info { "Complete" } },
                    { info { "isDispose: ${it.isDisposed}" } }
            )
        }
        fun test2(){
            Flowable.create<String>({
                it.onNext("first node")
                it.onNext("second node")
                it.onComplete()
            }, BackpressureStrategy.BUFFER).subscribe(
                    { info { "Next: $it" } },
                    { info { "Error: ${it.message}" } },
                    { info { "Complete" } },
                    { info { "isDispose: ${it.request(2)}" } }
            )
        }
    }
}

data class User(var id:Int, var name:String)

class StrObserver:Observer<String>{
    override fun onNext(p0: String) {
        info { "onNext:$p0" }
    }

    override fun onComplete() {
        info { "onComplete:" }
    }

    override fun onError(p0: Throwable) {
        info { "onError:${p0.message}" }
    }

    override fun onSubscribe(p0: Disposable) {
        info { "onSubscribe:" }
    }
}
