package lib.book.utils

import android.graphics.*
import android.view.View
import lib.book.belta.toValue

/**
 * Created by work on 2018/4/19.
 */

class DrawUtils {
    companion object {
        //region    可动态改变
        val FONT_RINGVALUE = 20.rangeTo(30).step(2).toValue("FontSize")!!
        val FACE_RINGVALUE = listOf(Typeface.DEFAULT,
                Typeface.DEFAULT_BOLD,
                Typeface.MONOSPACE,
                Typeface.SANS_SERIF,
                Typeface.MONOSPACE).toValue("FontType")!!
        val FONT_SIZE: Float
            get() = FONT_RINGVALUE.value.toFloat()
        val FONT_FACE: Typeface
            get() = FACE_RINGVALUE.value

        val FONT_COLOR: Int
            get() = Color.BLACK
        //endregion

        private val TEXT_PAINT:Paint
            get() = textPaint(FONT_COLOR, FONT_SIZE)
        private val TEXT_PAINT_BOLD:Paint
            get() = textPaint(FONT_COLOR, FONT_SIZE).apply { isFakeBoldText = true }

        fun View.drawDefaultText(canvas: Canvas, text: String, isLock: Boolean = false, isBold: Boolean = true) {
            canvas.drawColor(if (isLock) Color.LTGRAY else Color.WHITE)
            if (text.isNotEmpty()) {
                textDrawCenter(canvas,
                        if (isBold) TEXT_PAINT_BOLD else TEXT_PAINT_BOLD,
                        Rect(0, 0, width, height),
                        text)
            }
        }

        private inline fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: Rect, msg: String) {
            canvas.drawText(msg,
                    rect.exactCenterX(), (rect.exactCenterY() - deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }

        private inline fun textPaint(colorInt: Int, fontSize: Float) =
                Paint().apply {
                    color = colorInt
                    style = Paint.Style.FILL
                    textAlign = Paint.Align.CENTER
                    textSize = fontSize
                    flags = Paint.ANTI_ALIAS_FLAG
                    typeface = FONT_FACE
                }

        private inline fun deltaCenterHeightFromFont(paint: Paint) =
                paint.fontMetricsInt.let {
                    (it.top + it.bottom) / 2
                }
    }
}