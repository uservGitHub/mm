package sample.Final

import android.util.Log
import pdfbook.sample.stages.info
import java.util.concurrent.locks.Lock

/**
 * Created by work on 2018/4/3.
 */

class AppTick(){
    companion object {
        //region    lastTick=xxx;=>[spanLastTick]  Update LastTick---infoTick(msg:Any);=>[spanLastTick]  msg
        /**
         * 是否单总线控制
         * 是：修改时，不需要加锁
         * 否：修改时，需要加锁(当前情况)
         */
        private val lastTickLock = Any()
        //输出格式：999_888 999秒_888毫秒
        private fun spanMsToString(spanMs: Long) = when(spanMs){
            in 0..9 -> "000_00$spanMs"
            in 10..99 -> "000_0$spanMs"
            in 100..999 -> "000_$spanMs"
            in 1000..9999 -> "00${spanMs.div(1000L)}_${spanMs.rem(1000L)}"
            in 10000..99999 -> "0${spanMs.div(10000L)}_${spanMs.rem(10000L)}"
            else -> "${spanMs.div(100000L)}_${spanMs.rem(100000L)}"
        }

        const val tickTag = "_ATck"
        @Volatile
        internal var lastTick = 0L
            set(value) {
                synchronized(lastTickLock) {
                    field = value
                    val tickFromLastTick = System.currentTimeMillis() - lastTick
                    //打印LastTick更新
                    Log.v(tickTag, "[${spanMsToString(tickFromLastTick)}]  Update LastTick")
                }
            }
        internal fun infoTick(tag:String, context: () -> Any) {
            val tickFromLastTick = System.currentTimeMillis() - lastTick
            //[距离上次时间（毫秒）]两个空格“输出内容”
            Log.v(tag, "[${spanMsToString(tickFromLastTick)}]  ${context.invoke()}")
        }
        internal fun infoTick(context: () -> Any) = infoTick(tickTag, context)
        //endregion

        fun test_infoTick(){
            lastTick = System.currentTimeMillis()
            Thread.sleep(9)
            infoTick { "9MS" }
            Thread.sleep(41)
            infoTick { "50MS" }
            Thread.sleep(100)
            infoTick { "100MS" }
            lastTick = System.currentTimeMillis()
            Thread.sleep(1200)
            infoTick { "1_200MS" }
        }
    }
}