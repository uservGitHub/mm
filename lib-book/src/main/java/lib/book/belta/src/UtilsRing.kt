package lib.book.belta

//扩展
/**
 * 从迭代器生成，不可变
 */
public fun <T> Iterable<T>.toRing(ctx: Context,tag: String, opView:(View)->Unit):RingValue<T>?{
    return RingValue.create(toList(),tag)?.apply {
        opView(bindUi(RingView(ctx)))
    }
}

/**
 * 如从Mutable集合生成，则可变；反之不可变
 */
public fun <T> List<T>.toRing(ctx: Context,tag: String, opView:(View)->Unit):RingValue<T>? {
    return RingValue.create(this, tag)?.apply {
        opView(bindUi(RingView(ctx)))
    }
}

val ctx = Context()
val tag = "Tag"
fun error(){
    println("Error!")
}
var host:RingView? = null
fun capture(v:View){
    host = v as RingView
}
fun click(){
    println("click-->")
    host?.run {
        rightClick()
        rightClick()
        rightClick()
    }
    println()
}
fun main(argv:Array<String>){
    val list = mutableListOf<Int>(-22,0)
    val ring1 = list.toRing(ctx, tag, ::capture)
    click()
    list.add(0, 33)
    click()
}