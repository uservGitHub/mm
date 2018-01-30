package sample.New

import android.graphics.*

/**
 * Created by work on 2018/1/30.
 */

/**
 * 分类条目：
 * 条目名称
 * 条目子项
 *
 * 可用于生成，渲染
 */
data class KindItem(val name:String,val subItems:MutableList<SubItem>) {
    //渲染，只进行单一的拼接
    fun render(width: Int): Bitmap {
        if (subItems.size == 0) throw IllegalArgumentException("SubItem为空")
        val handle = UtilsDocument.instacne
        val bmps = subItems.map { handle.buildBmp(it, width) }
        val result = Bitmap.createBitmap(width,bmps.sumBy { it.height },Bitmap.Config.RGB_565)

        val canvas = Canvas(result)
        val paint = Paint()
        var height = 0
        bmps.forEach {
            canvas.drawBitmap(it, Rect(0, 0, it.width, it.height), Rect(0, height, width, height + it.height), paint)
            height += it.height
            it.recycle()
        }
        return result
    }
}

/**
 * 分类条目子项：
 * 文件id，可获得文件相关资源（例如document、filename等等），需要借助辅助单例对象
 * 页码，从0开始
 * 矩形框，表示横纵比例截取（对应原始PageSize为0,0到1,1）
 */
data class SubItem(val fileId:Int,val pageInd:Int,val rect:RectF)

class UtilsDocument(){
    companion object {
        val instacne: UtilsDocument
            get() = global!!
        @Volatile
        private var global: UtilsDocument? = null
        fun initDocument(){
            if (global==null){
                synchronized(UtilsDocument::class){
                    if (global == null){
                        global = UtilsDocument()
                    }
                }
            }
        }
    }
    //错误时，呈现错误信息在Bitmap中
    fun buildBmp(subItem: SubItem, width:Int):Bitmap{
        return Bitmap.createBitmap(width,width,Bitmap.Config.RGB_565)
    }
}