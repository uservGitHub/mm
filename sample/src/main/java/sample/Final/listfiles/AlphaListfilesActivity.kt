package sample.Final.listfiles

import android.content.Context
import android.os.ParcelFileDescriptor
import android.widget.Toast
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import sample.Final.AppTick
import java.io.File

/**
 * Created by work on 2018/4/4.
 */
open class AlphaListfilesActivity:BaseListfilesActivity(){
    override fun layoutSetup() {
        val root = dirRoot
        val folder = File(root)
        val p0 = SubscribeItem()

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
    }

    class SubscribeItem(){
        private var tick = 0L
        private var nextCount = 0
        private lateinit var _consumer:Disposable
        val consumer:Disposable get() = _consumer
        fun next(t:Any){
            AppTick.infoTick(t)
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