package sample.Final.listfiles

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import sample.Final.AppTick
import sample.Final.LogBuilder
import java.io.File

/**
 * Created by work on 2018/4/4.
 */
open class AlphaListfilesActivity:BaseListfilesActivity(){
    override fun layoutSetup() {
        val root = dirRoot
        val folder = File(root)
        val p0 = SubscribeItem()
        val p1 = LogBuilder()

        bindBtnFn("AllFiles") {
            //AppTick.infoTick("${folder.name},${folder.canRead()}")
            val fileSource = Observable.fromIterable(
                    folder.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            )
            fileSource.subscribe(p0::next, p0::error, p0::complete, p0::subscrib)
            //fileSource.subscribe(p0::next, p0::error, p0::complete)
            //AppTick.infoTick(p0.consumer.isDisposed)
            /*val pdfSource = fileSource.flatMap { v->
                val pdf = ParcelFileDescriptor.open(File(v),ParcelFileDescriptor.MODE_READ_ONLY)
                Flowable.just(pdf)
            }*/
        }

        bindBtnFn("AllPdfs") {
            val root = dirRoot
            val folder = File(root)
            val p0 = SubscribeItem()

            /*val pdfFiles = folder.listFiles()
                    .filter { it.isDirectory }
                    .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }*/
            val pdfSource = Flowable.fromIterable(
                    folder.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).flatMap { v ->
                Flowable.just(ParcelFileDescriptor.open(v, ParcelFileDescriptor.MODE_READ_ONLY))
                        .subscribeOn(Schedulers.io())
            }

            p0.reset()
            //pdfSource.blockingSubscribe(p0::next,p0::error,p0::complete)
            pdfSource.blockingSubscribe({}, {}, p0::complete)
        }

        bindBtnFn("AllPdfsSync") {
            val root = dirRoot
            val folder = File(root)
            val p0 = SubscribeItem()

            /*val pdfFiles = folder.listFiles()
                    .filter { it.isDirectory }
                    .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }*/
            val pdfSource = Observable.fromIterable(
                    folder.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            ).map { ParcelFileDescriptor.open(it, ParcelFileDescriptor.MODE_READ_ONLY) }
            p0.reset()
            pdfSource.blockingSubscribe({}, {}, p0::complete)
        }

        bindBtnFn("LogBuilder"){
            val fileSource = Observable.fromIterable(
                    folder.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            )
            p1.reset("Log", true, true)
            fileSource.subscribe(p1::next, p1::error, p1::complete)
        }
        bindBtnFn("_LogB"){
            Log.v("_LogB", p1.dump)
        }
        bindBtnFn("dump"){
            updateDump(p1.dump)
        }
    }

    /**
     * isLogv 默认不输出至Logv（特指next）
     */
    class SubscribeItem(var isLogv:Boolean = false){
        private var tick = 0L
        private var nextCount = 0
        private lateinit var _consumer:Disposable
        val consumer:Disposable get() = _consumer
        fun next(t:Any){
            if (isLogv){
                AppTick.infoTick(t)
            }
            nextCount++
        }
        fun error(t:Throwable){
            AppTick.infoTick("-->X [$nextCount] [${System.currentTimeMillis()-tick}ms]${t.message!!}")
        }
        fun complete(){
            AppTick.infoTick("-->| [$nextCount] [${System.currentTimeMillis()-tick}ms]complete")
        }
        fun subscrib(t:Disposable){
            _consumer = t
            reset()
        }
        fun reset(){
            nextCount = 0
            tick = System.currentTimeMillis()
        }
    }
}