package lib.book.alpha

import android.util.Log
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

/**
 * Created by Administrator on 2018/4/5.
 * 考虑到日志输出会影响执行性能，此类的目的就是搜集日志信息，
 * 进行日志信息的控制。
 * 不加锁，无输出流，无打印的情况下，对程序影响最小
 * 2018/4/8 新增打桩控制，选中时才记录打桩信息
 */

class LogBuilder(val tag:String = "_LogB") {

    private var tick = 0L
    private var nextCount = 0
    private val sb = StringBuilder(10 * 1024)   //10KB
    private var endAction: (() -> Unit)? = null
    /**
     * 运行的过程中，不可以更新
     */
    var busEnd:(()->Unit)? = null
        set(value) {
            val running = (disposer == null && subscriptor == null)
            if (running) {
                //可以更新
                field = value
            }
        }

    protected var isLogv: Boolean = false
        private set
    protected var isFlow: Boolean = false
        private set
    protected var isPill: Boolean = false
        private set

    val dump: String get() = sb.toString()

    @Volatile
    var isEnd: Boolean = false
        private set
    var disposer: Disposable? = null
        private set
    var subscriptor: Subscription? = null
        private set


    /**
     * 开关控制：
     * pill的处理是单独控制的
     * logv是依赖flow或pill的
     * error,complete,break是无条件输出的
     */
    fun switch(flow: Boolean = false, logv: Boolean = false, pill:Boolean = false) {
        isFlow = flow
        isLogv = logv
        isPill = pill
    }

    /**
     * 重置（标题，本次操作:可能在非UI线程中执行）
     */
    fun reset(title: String, end: (() -> Unit)? = null) {
        isEnd = false
        endAction = end

        sb.delete(0, sb.length)
        headTitle(title)

        nextCount = 0
        tick = System.currentTimeMillis()
    }

    /**
     * 打桩 两个空格>>[span ms]>>桩名称flag
     */
    fun pilling(flag: Any) {
        if (isPill) {
            //序号从0开始，默认2位宽度右对齐
            //两个空格[序号] [xxms]信息
            val flow = textPill(flag)
            sb.append(flow)
            if (isLogv) {
                Log.v(tag, flow.substring(0, flow.length-1))
            }
        }
    }

    /**
     * 被取消或中断，无条件打印
     */
    fun postBreak(){
        if (true) {
            //序号从0开始，默认2位宽度右对齐
            //两个空格[序号] [xxms]信息
            val flow = textPill("break:(${Thread.currentThread().name},${Thread.currentThread().isDaemon})")
            sb.append(flow)
            if (isLogv) {
                Log.v(tag, flow.substring(0, flow.length-1))
            }
        }
        manualEnd()
    }

    /**
     *  打桩 桩名称:当前线程名,是否守护线程
     */
    fun pillingThread(flag: String) {
        pilling("$flag:(${Thread.currentThread().name},${Thread.currentThread().isDaemon})")
    }

    /**
     * 两个空格[序号] [span ms]信息，即[8位ms]
     */
    fun preNext(t: Any) {
        if (isFlow) {
            //序号从0开始，默认4位宽度右对齐
            //两个空格[序号] [xxms]信息
            val flow = "  ${textTick()}$t\n"
            sb.append(flow)
            if (isLogv) {
                Log.v(tag, flow.substring(0,flow.length-1))
            }
        }
        nextCount++
    }

    /**
     * --> [数量 | span ms]异常信息
     */
    fun preError(t: Throwable) {
        t.printStackTrace()
        val flowBreak = "> ${textTick()}${t.message!!}\n\n"
        sb.append(flowBreak)
        excuteEnd()
        if (isLogv) {
            Log.v(tag, flowBreak.substring(0, flowBreak.length - 1))
        }
    }

    /**
     * 手动结束
     */
    fun manualEnd() {
        val flowComplete = ">>${textTick()}manualEnd\n\n"
        sb.append(flowComplete)
        excuteEnd()
        if (isLogv) {
            Log.v(tag, flowComplete.substring(0, flowComplete.length - 1))
        }
    }

    /**
     * --> [数量 | span ms]completed
     */
    fun postComplete() {
        val flowComplete = "> ${textTick()}completed\n\n"
        sb.append(flowComplete)
        excuteEnd()
        if (isLogv) {
            Log.v(tag, flowComplete.substring(0, flowComplete.length - 1))
        }
    }

    /**
     * 前置Subscribe，引用Dispable
     */
    fun preSubscribe(t: Disposable) {
        disposer = t
    }

    /**
     * 前置Subscribe，引用Subscription
     */
    fun preSubscribe(t: Subscription) {
        subscriptor = t
    }

    private inline fun excuteEnd() {
        if (!isEnd) {
            isEnd = true
            endAction?.invoke()
            busEnd?.invoke()
            //是否要清理?
            subscriptor = null
            disposer = null
        }
    }

    private inline fun headTitle(title: String) {
        val headTxt = ">>[=$title=]\n"
        sb.append(headTxt)
        if (isLogv) {
            Log.v(tag, headTxt.substring(0, headTxt.length - 1))
        }
    }

    private inline fun textTick() = "[${nextCount.no2()} |${(System.currentTimeMillis() - tick).no4()}ms]"
    private inline fun textPill(flag: Any) = "  >>[${(System.currentTimeMillis() - tick).no4()}ms]>>$flag\n"
    private inline fun trySleep(millis:Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //默认2位宽度右对齐
    protected inline fun Int.no2(): String {
        return when (this) {
            in 0..9 -> " $this"
        //in 10..99 -> "  $this"
        //in 100..999 -> " $this"
            else -> this.toString()
        }
    }
    protected inline fun Long.no4(): String {
        return when (this) {
            in 0..9 -> "   $this"
            in 10..99 -> "  $this"
            in 100..999 -> " $this"
            else -> this.toString()
        }
    }
}