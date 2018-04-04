package sample.Final

import android.util.Log
import io.reactivex.Flowable
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
        internal var spanMsFmtZero = true
        //输出格式：999_888 999秒_888毫秒
        private fun spanMsToString(spanMs: Long):String{
            val f = spanMsFmtZero
            return when(spanMs){
                in 0..9 -> "${if (f)"000_00" else "      "}$spanMs"
                in 10..99 -> "${if (f)"000_0" else "     "}$spanMs"
                in 100..999 -> "${if (f)"000_" else "    "}$spanMs"
                in 1000..9999 -> "${if (f)"00" else "  "}${spanMs.div(1000L)}_${spanMs.rem(1000L)}"
                in 10000..99999 -> "${if (f)"0" else " "}${spanMs.div(1000L)}_${spanMs.rem(1000L)}"
                else -> "${spanMs.div(1000L)}_${spanMs.rem(1000L)}"
            }
        }
        private inline fun logV(tag:String, dump:String){
            //需要加锁
            synchronized(lastTickLock){
                logSb.append(dump)
                logSb.append('\n')
            }
            Log.v(tag, dump)
        }
        const val tickTag = "_ATck"
        private val logSb = StringBuilder()
        internal val lastDump:String
            get() = logSb.toString()
        @Volatile
        internal var lastTick = 0L
            private set(value) {
                synchronized(lastTickLock) {
                    field = value
                    val tickFromLastTick = System.currentTimeMillis() - lastTick
                    //打印LastTick更新
                    logV(tickTag, "[${spanMsToString(tickFromLastTick)}]  Update LastTick")
                }
            }
        internal fun infoTick(tag:String, msg: Any) {
            val tickFromLastTick = System.currentTimeMillis() - lastTick
            //[距离上次时间（毫秒）]两个空格“输出内容”
            logV(tag, "[${spanMsToString(tickFromLastTick)}]  ${msg}")
        }
        internal fun infoTick(context: () -> Any) = infoTick(tickTag, context.invoke())
        internal fun infoTick(msg:Any) = infoTick(tickTag, msg)
        internal fun resetTick(){
            synchronized(lastTickLock){
                logSb.delete(0, logSb.length)
            }
            lastTick = System.currentTimeMillis()
        }
        //endregion

        fun test_infoTick1(){
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

        fun test_infoTick(){
            Flowable.just("abc")
                    .subscribe(this::infoTick)
        }
    }

}