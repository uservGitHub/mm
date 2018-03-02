package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by work on 2018/3/2.
 */

fun info(msg:()->Any) {
    Log.v("_Rx", "${msg()}")
}

class RxjavaActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "onCreate ===" }
        test3()
    }

    companion object {
        /**
         * onSubscribe,on[Next,Error,Complete]
         */
        fun test3() {
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
