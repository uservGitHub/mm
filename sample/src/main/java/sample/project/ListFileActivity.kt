package sample.project

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.IpPrefix
import android.os.ParcelFileDescriptor
import android.util.Log
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File


/**
 * Created by Administrator on 2018/3/5.
 */
class ListFileActivity:BaseListFileActivity(){
    private var tick = 0L
    private var count = 0


    override fun listFiels() {
        //myList()
        tag(this::myListBooks, "myListBooks")
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

                .observeOn(Schedulers.io())
                .doOnNext {
                    //info { "doNext:耗时操作" }
                    //Thread.sleep(1000)
                    val begTick = System.currentTimeMillis()
                    val pfd = ParcelFileDescriptor.open(it, ParcelFileDescriptor.MODE_READ_ONLY)
                    val pdfDocument = core.newDocument(pfd)
                    try{
                        core.openPage(pdfDocument, 0,1)
                        val pageSize = core.getPageSize(pdfDocument, 0)
                        val bmp = Bitmap.createBitmap(pageSize.width, pageSize.height,Bitmap.Config.RGB_565)
                        core.renderPageBitmap(pdfDocument,bmp,0,0,0,bmp.width,bmp.height)
                        bmp.recycle()
                    }catch (e:Exception){
                        info { "${printPreFix}e:${e.message}" }
                    }finally {
                        core.closeDocument(pdfDocument)
                        val endTick = System.currentTimeMillis()
                        info { "${printPreFix}span=${endTick-begTick},size=${it.length()/1000}" }
                    }

                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            //info { "Next:${it}" }
                            count++
                        },
                        {
                            info { "${printPreFix}Error:${it.message}" }
                        },
                        {
                            val curTick = System.currentTimeMillis()
                            btnListFiles.text = "${curTick-tick}"
                            info { "${printPreFix}Complete,tick=$curTick\ttimespan=${curTick - tick},count=$count" }
                        },
                        {
                            tick = System.currentTimeMillis()
                            count = 0
                            info { "${printPreFix}Subscribe,tick=$tick" }
                        }
                )
    }
    private fun myList3(){
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
    //没有并行
    private fun myListBooksNoParallel(prefix: String = ""){
        info { "${printPreFix}list dir=$dir" }
        info { "${printPreFix}list count=${File(dir).listFiles().size}" }

        val source = Observable.create(object :ObservableOnSubscribe<File>{
            override fun subscribe(p0: ObservableEmitter<File>) {
                File(dir).listFiles().forEach {
                    if (it.isDirectory){
                        info { "${printPreFix}sub dir=${it.name}" }

                        it.listFiles().filter { subFile ->
                            subFile.name.toLowerCase().endsWith(".pdf")
                        }.forEach{ okFile ->
                            //发射合法文件
                            p0.onNext(okFile)
                        }
                    }
                }

                p0.onComplete()
            }
        })

        //再次发射（异步）
        source.observeOn(Schedulers.io())
                .doOnNext {
                    //这里要并行执行
                    val begTick = System.currentTimeMillis()
                    //模拟耗时任务，确定是否并行执行
                    Thread.sleep(2000)
                    //执行完毕，打印
                    val endTick = System.currentTimeMillis()
                    info { "${printPreFix}读取IO完毕[${endTick-begTick}]：$it" }
                    //进行传递...
                }
                .map {
                    "doc:[$it]"
                }
                /*.observeOn(Schedulers.computation())
                .flatMap{
                    //密集计算
                    Observable.create(object :ObservableOnSubscribe<Bitmap>{
                        override fun subscribe(p0: ObservableEmitter<Bitmap>) {
                            //这里要并行执行
                            val begTick = System.currentTimeMillis()
                            //模拟耗时任务，确定是否并行执行
                            Thread.sleep(2000)
                            //执行完毕，打印
                            val endTick = System.currentTimeMillis()

                            val bmp = Bitmap.createBitmap(10,10,Bitmap.Config.RGB_565)
                            info { "${printPreFix}生成BP完毕[${endTick-begTick}]：$it,再次发射" }
                            p0.onNext(bmp)
                        }
                    })
                }*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            info { "${printPreFix}RenderBP" }
                            count++
                        },
                        {
                            info { "${printPreFix}Error:${it.message}" }
                        },
                        {
                            val curTick = System.currentTimeMillis()
                            btnListFiles.text = "${curTick-tick}"
                            info { "${printPreFix}Complete\ttimespan=${curTick - tick},count=$count" }
                        },
                        {
                            tick = System.currentTimeMillis()
                            count = 0
                            info { "${printPreFix}Subscribe,tick=$tick" }
                        }
                )
    }
    private fun myListBooks(prefix: String = ""){
        info { "${printPreFix}list dir=$dir" }
        info { "${printPreFix}list count=${File(dir).listFiles().size}" }

        tick = System.currentTimeMillis()
        val source = Flowable.create(object :FlowableOnSubscribe<File>{
            override fun subscribe(p0: FlowableEmitter<File>) {
                File(dir).listFiles().forEach {
                    if (it.isDirectory){
                        info { "${printPreFix}sub dir=${it.name}" }

                        it.listFiles().filter { subFile ->
                            subFile.name.toLowerCase().endsWith(".pdf")
                        }.forEach{ okFile ->
                            //发射合法文件
                            p0.onNext(okFile)
                        }
                    }
                }

                p0.onComplete()
            }
        },BackpressureStrategy.DROP)
                .parallel()
                .runOn(Schedulers.io())
                .map {
                    //这里要并行执行
                    val begTick = System.currentTimeMillis()
                    //模拟耗时任务，确定是否并行执行
                    Thread.sleep(2000)
                    //执行完毕，打印
                    val endTick = System.currentTimeMillis()
                    info { "${printPreFix}读取IO完毕[${endTick-begTick}]：$it" }
                    //进行传递...
                    return@map "doc:[$it]"
                }
                .sequential()
                .blockingSubscribe(
                        {
                            info { "${printPreFix}Ok:$it" }
                            count++
                        },
                        {
                            info { "${printPreFix}Error:${it.message}" }
                        },
                        {
                            val curTick = System.currentTimeMillis()
                            btnListFiles.text = "${curTick-tick}"
                            info { "${printPreFix}Complete\ttimespan=${curTick - tick},count=$count" }
                        }
                )
    }

}