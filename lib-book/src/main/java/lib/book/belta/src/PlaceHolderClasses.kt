package lib.book.belta

class Context

open class View(ctx:Context)

class Button(ctx:Context){
    fun setOnClickListener(f:()->Unit){}
}

class TextView(ctx:Context) {
    var text: CharSequence = ""
}
