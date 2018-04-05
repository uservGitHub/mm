package sample.Final.listfiles

import android.os.ParcelFileDescriptor
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.async
import sample.Final.AppTick
import sample.Final.LogBuilder
import java.io.File

/**
 * Created by work on 2018/4/4.
 */
open class BetaListfilesActivity : BaseListfilesActivity() {
    override fun layoutSetup() {
        val root = File(dirRoot)


        val p0 = LogBuilder(busEnd = releaseBtns)

        val pdfFilesA = "FilesA"
        val pdfFilesB = "FilesB"
        val pdfFilesC = "FilesC"

        //region    PdfFiles
        bindBtnFn(pdfFilesA) {
            val source = Observable.fromIterable(
                    root.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            )

            p0.reset("Observable", isFlow, isLogv, showDump)
            source.subscribe(p0::next,p0::error,p0::complete)
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
        }
        //endregion

        val openFilesA = "OpenA"
        val openFilesB = "OpenB"
        val openFilesC = "OpenC"

        //region    ParcelFileDescriptor
        bindBtnFn(openFilesA){
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
            enterBtns.invoke()
            p0.reset("Flowable.io.blocking_PracelFile", isFlow, isLogv, showDump)
            source.blockingSubscribe(p0::next,p0::error,p0::complete)
        }
        //endregion

        bindBtnFn("5s"){
            p0.reset("等待5秒钟", isFlow, isLogv, showDump)
            p0.next("wait 5 seconde")
            //防止锁定界面
            async {
                Thread.sleep(5000)
                p0.complete()
            }
        }
    }
}