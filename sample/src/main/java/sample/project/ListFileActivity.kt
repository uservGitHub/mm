package sample.project

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File


/**
 * Created by Administrator on 2018/3/5.
 */
class ListFileActivity:BaseListFileActivity(){
    private var tick = 0L
    private var count = 0
    private inline fun info(msg:()->Any) {
        Log.v("LFA", "${msg()}")
    }

    override fun listFiels() {
        myList()
    }
    private fun myList(){
        val folders = File(dir)
        Observable.create(object :ObservableOnSubscribe<File>{
            override fun subscribe(p0: ObservableEmitter<File>) {
                folders.listFiles().forEach {
                    it.listFiles().filter { file ->
                        file.name.toLowerCase().endsWith(".pdf")
                    }.forEach { file->
                        p0.onNext(file)
                    }
                }
                p0.onComplete()
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    //info { "doNext:耗时操作" }
                    //Thread.sleep(1000)
                    val pfd = ParcelFileDescriptor.open(it, ParcelFileDescriptor.MODE_READ_ONLY)
                    val pdfDocument = core.newDocument(pfd)
                    try{
                        core.openPage(pdfDocument, 0,1)
                        val pageSize = core.getPageSize(pdfDocument, 0)
                        val bmp = Bitmap.createBitmap(pageSize.width, pageSize.height,Bitmap.Config.RGB_565)
                        core.renderPageBitmap(pdfDocument,bmp,0,0,0,bmp.width,bmp.height)
                        bmp.recycle()
                    }catch (e:Exception){
                        info { "e:${e.message}" }
                    }finally {
                        core.closeDocument(pdfDocument)
                    }

                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            //info { "Next:${it}" }
                            count++
                        },
                        {
                            info { "Error:${it.message}" }
                        },
                        {
                            val curTick = System.currentTimeMillis()
                            btnListFiles.text = "${curTick-tick}"
                            info { "Complete,tick=$curTick\ttimespan=${curTick - tick},count=$count" }
                        },
                        {
                            tick = System.currentTimeMillis()
                            count = 0
                            info { "Subscribe,tick=$tick" }
                        }
                )
    }
    private fun myList2(){
        val folders = File(dir)
        Flowable.just(folders)
                .flatMap({ Flowable.fromIterable(it.listFiles().toList()) })
                .flatMap({ Flowable.fromIterable(it.listFiles().toList())})
                .filter({ it.name.toLowerCase().endsWith(".pdf") })
                .map({ it.length() })
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    //info { "doNext:耗时操作" }
                    Thread.sleep(1000)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            info { "Next:${it}" }
                            count++
                        },
                        {
                            info { "Error:${it.message}" }
                        },
                        {
                            val curTick = System.currentTimeMillis()
                            info { "Complete,tick=$curTick\ttimespan=${curTick - tick},count=$count" }
                        },
                        {
                            tick = System.currentTimeMillis()
                            count = 0
                            info { "Subscribe,tick=$tick" }
                            it.request(10)
                        }
                )
    }
    private fun myList1(){
        val folders = File(dir)
        Observable.just(folders)
                .flatMap({ Observable.fromIterable(it.listFiles().toList()) })
                .flatMap({ Observable.fromIterable(it.listFiles().toList())})
                .filter({ it.name.toLowerCase().endsWith(".pdf") })
                .map({ it.length() })
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    //info { "doNext:耗时操作" }
                    Thread.sleep(1000)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            info { "Next:${it}" }
                            count++
                        },
                        {
                            info { "Error:${it.message}" }
                        },
                        {
                            val curTick = System.currentTimeMillis()
                            info { "Complete,tick=$curTick\ttimespan=${curTick - tick},count=$count" }
                        },
                        {
                            tick = System.currentTimeMillis()
                            count = 0
                            info { "Subscribe,tick=$tick" }
                        }
                )
    }



}