package sample.Final

import android.util.Log
import io.reactivex.disposables.Disposable

/**
 * Created by Administrator on 2018/4/5.
 * 考虑到日志输出会影响执行性能，此类的目的就是搜集日志信息，
 * 进行日志信息的控制。
 * 不加锁，无输出流，无打印的情况下，对程序影响最小
 */

class LogBuilder(val tag:String = "_LogB") {

    private var tick = 0L
    private var nextCount = 0
    private val sb = StringBuilder(10 * 1024)   //10KB

    protected var isLogv: Boolean = false
        private set
    protected var isFlow: Boolean = false
        private set

    val dump: String get() = sb.toString()

    /**
     * 默认最少最简原则（不记录中间流，不打印LogV）
     */
    fun reset(title: String, flow: Boolean = false, logv: Boolean = false) {
        isFlow = flow
        isLogv = logv

        sb.delete(0, sb.length)
        headTitle(title)

        nextCount = 0
        tick = System.currentTimeMillis()
    }

    /**
     * 五个空格[序号] [span ms]信息
     */
    fun next(t: Any) {
        if (isFlow) {
            //序号从0开始，默认4位宽度右对齐
            //五个空格[序号] [xxms]信息
            val flow = "     [${nextCount.no4()}] [${System.currentTimeMillis() - tick}ms]$t\n"
            sb.append(flow)
            if (isLogv) {
                Log.v(tag, flow.substring(0, flow.length - 1))
            }
        }
        nextCount++
    }

    /**
     * -->X [数量] [span ms]异常信息
     */
    fun error(t: Throwable) {
        val flowBreak = "-->X [${nextCount.no4()}] [${System.currentTimeMillis() - tick}ms]${t.message!!}\n\n"
        sb.append(flowBreak)
        if (isLogv) {
            Log.v(tag, flowBreak.substring(0, flowBreak.length - 1))
        }
    }

    /**
     * -->| [数量] [span ms]completed
     */
    fun complete() {
        val flowComplete = "-->| [${nextCount.no4()}] [${System.currentTimeMillis() - tick}ms]completed\n\n"
        sb.append(flowComplete)
        if (isLogv) {
            Log.v(tag, flowComplete.substring(0, flowComplete.length - 1))
        }
    }

    private inline fun headTitle(title: String) {
        val headTxt = "-->O [=$title=]\n"
        sb.append(headTxt)
        if (isLogv) {
            Log.v(tag, headTxt.substring(0, headTxt.length - 1))
        }
    }

    //默认4位宽度右对齐
    protected inline fun Int.no4(): String {
        return when (this) {
            in 0..9 -> "   $this"
            in 10..99 -> "  $this"
            in 100..999 -> " $this"
            else -> this.toString()
        }
    }
}