package pdfbook.sample.stages

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TimeUtils
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.*
import sample.utils.CalcTimeUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by work on 2018/3/2.
 * http://blog.csdn.net/DeMonliuhui/article/details/77848691
 * http://blog.csdn.net/qq_35064774/article/details/53045298
 */

fun info(msg:()->Any) {
    Log.v("_Rx", "${msg()}")
}
inline fun Observable<Int>.commSubscribe()=
        this.subscribe(
                { info { "Next: $it" } },
                { info { "Error: ${it.message}" } },
                { info { "Complete" } },
                { info { "Subscribe" } }
        )

inline fun Flowable<Int>.commSubscribe(requestCount:Int)=
        this.subscribe(
                { info { "Next: $it" } },
                { info { "Error: ${it.message}" } },
                { info { "Complete" } },
                { info { "Subscribe: request($requestCount)${it.request(requestCount.toLong())}" } }
        )

class commIntOos:ObservableOnSubscribe<Int>{
    var autoSend = false
    override fun subscribe(p0: ObservableEmitter<Int>) {
        val it = p0
        try {
            if (!it.isDisposed  && autoSend) {
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
    }
}


data class LineMsg(var id:Int, var name: String)

class RxjavaActivity:AppCompatActivity() {
    val collection = mutableListOf<LineMsg>()
    val data:Array<LineMsg> by lazy {
        collection.toTypedArray()
    }
    lateinit var line:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "onCreate ===" }
        collection.add(LineMsg(1, "郭梦涵"))
        collection.add(LineMsg(2, "郭安琪"))
        collection.add(LineMsg(12, "孙文娟"))

        verticalLayout {
            line = linearLayout {
                orientation = LinearLayout.VERTICAL
                textView(){
                    text = "Header"
                    backgroundColor = Color.YELLOW
                }.lparams(width= matchParent)
            }.lparams(width= matchParent,height = matchParent)
        }.apply {
            /*async {
                Observable.fromIterable(collection).
                        flatMap({
                            //Observable.fromArray(it.copy(id=id+100))
                            val newId= it.id + 100
                            Observable.just(it.copy(newId))
                        }).
                        filter({
                            return@filter it.id%2 == 0
                        }).
                        map({
                            val name = "${it.name} ++"
                            LineMsg(it.id, name)
                        }).
                        repeat(2).
                        subscribe (
                                {
                                    info { "Next: ${it}" }
                                    Thread.sleep(1500)
                                    runOnUiThread {
                                        line.addView(TextView(ctx).apply {
                                            text = it.toString()
                                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                                        })
                                    }

                                },
                                { info { "Error: ${it.message}" } },
                                {
                                    info { "Complete" }
                                    runOnUiThread {
                                        line.addView(TextView(ctx).apply {
                                            text = "完毕"
                                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                                        })
                                    }
                                },
                                { info { "Subscribe" } }
                        )
            }*/
        }

        val ms = CalcTimeUtils.calcMs({
            addItems()
        })
        info { "ms:$ms" }
        //        addItems()
    }
    private fun addItems(){
        Observable.fromIterable(collection).
                flatMap({
                    //Observable.fromArray(it.copy(id=id+100))
                    Thread.sleep(1000)
                    val newId= it.id + 100
                    Observable.just(it.copy(newId))
                }).
                /*filter({
                    return@filter it.id%2 == 0
                }).*/
                map({
                    val name = "${it.name} ++"
                    LineMsg(it.id, name)
                }).
                repeat(2).
                subscribeOn(Schedulers.computation()).
                observeOn(Schedulers.computation()).  //AndroidSchedulers.mainThread()
                subscribe (
                        {
                            info { "Next: ${it}" }
                            //耗时操作的模拟应该是在发射端，不应该在这里
                            Thread.sleep(1500)
                            runOnUiThread {
                                line.addView(TextView(ctx).apply {
                                    text = it.toString()
                                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT)
                                })
                            }

                        },
                        { info { "Error: ${it.message}" } },
                        {
                            info { "Complete" }
                            runOnUiThread {
                                line.addView(TextView(ctx).apply {
                                    text = "完毕"
                                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT)
                                })
                            }

                        },
                        { info { "Subscribe" } }
                )
    }

    companion object {
        fun commTest1() {
            val oos = commIntOos().apply {
                autoSend = true
            }
            Observable.create<Int>(oos).
                    doOnComplete {
                        info { "doOnComplete" }
                    }.commSubscribe()

            //val pub = PublishSubject.create(oos)
            //pub.commSubscribe()


        }
        fun commTest2(){
            /*val list = mutableListOf<Int>()
            list.add(1)
            list.add(2)*/
            Observable.fromArray(3,2,1).subscribe(
                    { info { "Next: $it" } },
                    { info { "Error: ${it.message}" } },
                    { info { "Complete" } },
                    { info { "Subscribe" } }
            )
        }

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
                    {
                        val count = 2L
                        info { "Subscribe: request($count)" }
                        it.request(count)
                    }
            )
        }
    }
}

data class User(var id:Int, var name:String)


