package sample.Final.listfiles

import io.reactivex.Flowable
import io.reactivex.Observable
import sample.Final.AppTick
import java.io.File

/**
 * Created by work on 2018/4/4.
 */
open class AlphaListfilesActivity:BaseListfilesActivity(){
    override fun layoutSetup() {
        val root = dirRoot
        val folder = File(root)

        bindBtnFn("AllFiles"){
            //AppTick.infoTick("${folder.name},${folder.canRead()}")
            val fileSource = Observable.fromIterable(
                    folder.listFiles()
                            .filter { it.isDirectory }
                            .flatMap { it.listFiles().filter { file -> file.name.toLowerCase().endsWith(".pdf") } }
            )

            fileSource.subscribe({
                AppTick.infoTick(it)
            },{
                AppTick.infoTick(it)
            },{
                AppTick.infoTick("complete")
            })
        }
    }
}