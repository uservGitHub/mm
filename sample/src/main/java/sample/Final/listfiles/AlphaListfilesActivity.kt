package sample.Final.listfiles

import android.os.ParcelFileDescriptor
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import sample.Final.LogBuilder
import java.io.File
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * Created by work on 2018/4/4.
 */
open class AlphaListfilesActivity : BaseListfilesActivity() {
    private inline fun trySleep(millis:Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun layoutSetup() {
        //总共有两级目录
        val root = File(dirRoot)
        //赋值通用退出操作
        val log = LogBuilder().apply {
            //通用操作
            busEnd = {
                //增量方式输出日志
                appendUiDump(dump)
                //释放UI锁定
                releaseUiBtns()
            }
        }
        //更新开关，从UI设置中更新
        val updateSwitch:()->Unit = {
            log.switch(isFlow, isLogv)
        }
        //3个核心池线程
        val cp3 = Executors.newScheduledThreadPool(3)
        //关闭核心线程
        val shutdownCp3 = {
            cp3.shutdownNow()
            log.pilling("(cp3.isShutdown=${cp3.isShutdown})")
        }
        //3个线程
        val th3 = Executors.newFixedThreadPool(3)
        //关闭Fixed线程
        val shutdownTh3 = {
            th3.shutdownNow()
            log.pilling("(th3.isShutdown=${th3.isShutdown})")
        }

        //无限个缓存线程，使用 Schedulers.io()
        //Executors.newCachedThreadPool()
        //核心数线程，使用 Schedulers.computation()

        //一个线程
        val sg1 = Executors.newSingleThreadExecutor()
        val shutdownSg1 = {
            sg1.shutdownNow()
            log.pilling("(sg1.isShutdown=${sg1.isShutdown})")
        }

        var t1 = 200L
        var t2 = 150L
        val tBreak = t1 + t2 + 20
        var count = 7

        var typeInd = 0
        val typeA = {
            "A_${++typeInd}"
        }
        //region    A系列
        //map
        bindBtnFn(typeA.invoke()) {
            val title = "map(T1=$t1)"
            val source = Observable.range(0, count)
                    .map {
                        log.pillingThread("T1")
                        trySleep(t1)
                        "T1:$it"
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete)
        }
        //map.io
        bindBtnFn(typeA.invoke()) {
            val title = "map(T1=$t1).io"
            val source = Observable.range(0, count)
                    .map {
                        log.pillingThread("T1")
                        trySleep(t1)
                        "T1:$it"
                    }
                    .subscribeOn(Schedulers.io())
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete)
        }
        //map.break
        bindBtnFn(typeA.invoke()){
            val title = "map(T1=$t1).break($tBreak)"
            val source = Observable.range(0, count)
                    .map {
                        log.pillingThread("T1")
                        trySleep(t1)
                        "T1:$it"
                    }
                    .doOnDispose {
                        //被取消/中断
                        log.pillingThread("disposed")
                        log.manualEnd()
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete,log::preSubscribe)
            //上面的执行是在主线程中，上面执行完了才能执行下面，是阻塞方式。
            thread(true, true) {
                trySleep(tBreak)
                log.disposer?.dispose()
            }
        }
        //map.io.break
        bindBtnFn(typeA.invoke()){
            val title = "map(T1=$t1).io.break($tBreak)"
            val source = Observable.range(0, count)
                    .map {
                        log.pillingThread("T1")
                        trySleep(t1)
                        "T1:$it"
                    }
                    .subscribeOn(Schedulers.io())
                    .doOnDispose {
                        //被取消/中断
                        log.pillingThread("disposed")
                        log.manualEnd()
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete,log::preSubscribe)
            //可以被取消，下面和source的执行是同时的
            thread(true, true) {
                trySleep(tBreak)
                log.disposer?.dispose()
            }
        }

        //flatMap.map
        bindBtnFn(typeA.invoke()) {
            val title = "flatMap.map(T1=$t1)"
            val source = Observable.range(0, count)
                    .flatMap {
                        Observable.just(it)
                                .map {
                                    log.pillingThread("T1")
                                    trySleep(t1)
                                    "T1:$it"
                                }
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete)
        }
        //flatMap.map.io
        bindBtnFn(typeA.invoke()) {
            val title = "flatMap.map(T1=$t1).io"
            val source = Observable.range(0, count)
                    .flatMap {
                        Observable.just(it)
                                .map {
                                    log.pillingThread("T1")
                                    trySleep(t1)
                                    "T1:$it"
                                }
                                .subscribeOn(Schedulers.io())
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete)
        }
        //flatMap.map.break
        bindBtnFn(typeA.invoke()) {
            val tBreak = t1-20L
            val title = "flatMap.map(T1=$t1).break($tBreak)"
            val source = Observable.range(0, count)
                    .flatMap {
                        Observable.just(it)
                                .map {
                                    log.pillingThread("T1")
                                    trySleep(t1)
                                    "T1:$it"
                                }
                    }
                    .doOnDispose {
                        //被取消/中断
                        log.pillingThread("disposed")
                        log.manualEnd()
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete,log::preSubscribe)
            //上面的执行是在主线程中，上面执行完了才能执行下面，是阻塞方式。
            thread(true, true) {
                trySleep(tBreak)
                log.disposer?.dispose()
            }
        }
        //flatMap.map.io.break
        bindBtnFn(typeA.invoke()) {
            val tBreak = t1-20L
            val title = "flatMap.map(T1=$t1).io.break($tBreak)"
            val source = Observable.range(0, count)
                    .flatMap {
                        Observable.just(it)
                                .map {
                                    log.pillingThread("T1")
                                    trySleep(t1)
                                    "T1:$it"
                                }
                                .subscribeOn(Schedulers.io())
                    }
                    .doOnDispose {
                        //被取消/中断
                        log.pillingThread("disposed")
                        log.manualEnd()
                    }
            updateSwitch.invoke()
            log.reset(title)
            source.subscribe(log::preNext,log::preError,log::preComplete,log::preSubscribe)
            thread(true, true) {
                trySleep(tBreak)
                log.disposer?.dispose()
            }
        }
        //endregion

        typeInd = 0
        val typeB = {
            "B_${++typeInd}"
        }
        //region    B系列

        //endregion



        bindBtnFn("T1->T2") {


            val title = "T1($t1)\"单独\"执行，T2($t2)_i紧跟着T1_i执行；共${count}个,dx=$tBreak"
            val source = Observable.range(0, 7)
                    .subscribeOn(Schedulers.from(cp3))
                    .map {
                        log.pillingThread("T1")
                        trySleep(t1)
                        "T1:$it"
                    }  //前面以周期XXXms往下执行，直到完毕，起始是0ms
                    .observeOn(Schedulers.computation())
                    .map {
                        log.pillingThread("T2")
                        trySleep(t2)
                        "T2:$it"
                    }   //这一段跟着T1_i执行，直到完毕，起始是T1_0ms
                    .doOnDispose {
                        //被取消/中断
                        log.pillingThread("disposed")
                        log.manualEnd()
                    }

            updateSwitch.invoke()
            log.reset(title, shutdownCp3)
            source.subscribe(log::preNext, log::preError, log::preComplete, log::preSubscribe)
            thread(true, true) {
                trySleep(tBreak)
                log.disposer?.dispose()
            }
        }

        //region    简单的

        /*bindBtnFn(pdfFilesB) {
            //总结：
            //T1_0=0ms,T2_0=T1_1 ==>T2_i 紧跟着T1_i执行
            //T1_i单独执行，与T2_i-1 无关，T2_i紧跟着T1_i执行，不能提前执行。
            val title = "T1、T2各用几个线程；可以用flatMap{Observable.subscribeOn()}来控制"
            val source = Observable.range(0, 7)
                    .subscribeOn(Schedulers.io())
                    //.map {
                    .flatMap {
                        Observable.just(it)
                                .subscribeOn(Schedulers.from(est3))
                                .map {
                                    p0.pillingThread("m1")
                                    Thread.sleep(300)
                                    "m1:$it"
                                }
                    }  //前面以周期500ms往下执行，直到完毕，起始是0ms
                    .observeOn(Schedulers.computation())
                    .flatMap {
                        Observable.just(it)
                                .subscribeOn(Schedulers.computation())
                                .map {
                                    p0.pillingThread("m2")
                                    Thread.sleep(400)
                                    "m2:$it"
                                }
                    }

            p0.reset(title, isFlow, isLogv, est3End)
            source.subscribe(p0::next, p0::error, p0::complete, p0::subscribe)
        }*/
        //endregion

        //region    PdfFiles
/*        bindBtnFn(pdfFilesA) {
            val source = Observable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            )
                    .map {
                        p0.pilling("map")
                        return@map it.name
                    }
                    .filter {
                        p0.pilling("filter")
                        it.length<10
                    }

            p0.reset("Observable", isFlow, isLogv, showDump)
            source

                    .doOnComplete { p0.pilling("doOnComplete") }
                    .doOnSubscribe { p0.pilling("doOnSubscribe") }
                    .subscribe(p0::next,p0::error,p0::complete)
        }

        bindBtnFn(pdfFilesB){
            val source = Flowable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).subscribeOn(Schedulers.io())

            p0.reset("Flowable.io.subscribe", isFlow, isLogv, showDump)
            source.subscribe(p0::next,p0::error,p0::complete)
        }

        bindBtnFn(pdfFilesC){
            val source = Flowable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).subscribeOn(Schedulers.io())

            p0.reset("Flowable.io.blocking", isFlow, isLogv, showDump)
            source.blockingSubscribe(p0::next,p0::error,p0::complete)
        }*/
        //endregion

        val openFilesA = "OpenA"
        val openFilesB = "OpenB"
        val openFilesC = "OpenC"
        val openFilesD = "OpenD"

        //region    ParcelFileDescriptor
/*        bindBtnFn(openFilesA){
            val source = Observable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).map { ParcelFileDescriptor.open(it, ParcelFileDescriptor.MODE_READ_ONLY) }

            p0.reset("Observable_PracelFile", isFlow, isLogv, showDump)
            source.subscribe(p0::next,p0::error,p0::complete)
        }

        bindBtnFn(openFilesB){
            val source = Flowable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).flatMap { v ->
                Flowable.just(ParcelFileDescriptor.open(v, ParcelFileDescriptor.MODE_READ_ONLY))
                        .subscribeOn(Schedulers.io())
            }

            p0.reset("Flowable.io.subscribe_PracelFile", isFlow, isLogv, showDump)
            source.subscribe(p0::next,p0::error,p0::complete)
        }

        bindBtnFn(openFilesC){
            val source = Flowable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).flatMap { v ->
                Flowable.just(ParcelFileDescriptor.open(v, ParcelFileDescriptor.MODE_READ_ONLY))
                        .subscribeOn(Schedulers.io())
            }

            p0.reset("Flowable.io.blocking_PracelFile", isFlow, isLogv, showDump)
            source.blockingSubscribe(p0::next,p0::error,p0::complete)
        }

        bindBtnFn(openFilesD){
            //request只能控制subscribe(onNext的处理)，而无法控制源的发射，并且没有处理完毕，无complete
            //cancel相当于执行了source.dispose()，控制源结束
            //Log.v(p0.tag, "mainThreadId:${Thread.currentThread().name},${Thread.currentThread().isDaemon}")
            //val ces = Executors.newCachedThreadPool() //所有同时触发压力太大
            val ces = Executors.newScheduledThreadPool(3)
            var mapCount = 0
            val source = Flowable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).flatMap { v ->
                Flowable.just(ParcelFileDescriptor.open(v, ParcelFileDescriptor.MODE_READ_ONLY))
                        .map {
                            p0.pilling("${Thread.currentThread().name},${Thread.currentThread().isDaemon}")
                            //RxCachedThreadScheduler-20,true <== Schedulers.io()
                            //RxComputationThreadPool-1,true
                            //RxComputationThreadPool-2,true <== Schedulers.computation()
                            //双核CPU
                            //main,false <== 主线程
                            //pool-1-thread-3,false <== Schedulers.from(Executors.newFixedThreadPool(3)
                            //http://www.importnew.com/23325.html
                            mapCount++
                            if (mapCount == 5){
                                //p0.subscriptor?.cancel()
                            }
                            //模拟耗时
                            try {
                                Thread.sleep(1000)
                            }catch (e:Exception){
                                //Log.v(p0.tag, e.message) 这个不能用
                                e.printStackTrace()
                            }

                            return@map it
                        }
                        .subscribeOn(Schedulers.from(ces))
                //newCachedThreadPool 是无限个
                //newSingleThreadExecutor 是一个
                //newScheduledThreadPool 是指定个
                //newFixedThreadPool 是指定个
            }.doOnCancel({
                //手动执行结束
                //runOnUiThread {
                    p0.complete()
                    try{
                        Log.v(p0.tag, "abcd")
                    }catch (e:Exception){
                        val s = e.message
                    }

                //}

            })

            //p0.reset("Flowable.io_Description", isFlow, isLogv, showDump)
            val end:(String)->Unit = {
                p0.pilling("esc:${ces.isShutdown}") //false(Executors.newFixedThreadPool)
                ces.shutdownNow()
                p0.pilling("esc:${ces.isShutdown}") //true
                showDump(it)
            }
            p0.reset("Flowable.io_Description", isFlow, isLogv, end)
            //一旦subscribe()，source就开始执行，但subscribe里的执行是另外控制的，这里（subscriptor）
            source.subscribe(
                    {
                        p0.next(it)

                        //p0.subscriptor?.request(1)
                        p0.subscriptor?.cancel()
                    },
                    p0::error,p0::complete,
                    p0::subscribe)
            *//*thread {
                Thread.sleep(2000)
                try {
                    p0.subscriptor?.cancel()
                }catch (e:Exception){
                    Log.v(p0.tag, e.message)
                }
            }*//*
        }*/
        //endregion


    }
}