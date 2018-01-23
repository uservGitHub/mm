package sample.utils

import android.graphics.*

/**
 * Created by Administrator on 2018/1/23.
 */
class BmpUtils {
    companion object {
        fun buildBmp(side: Int, msg: Any? = null): Bitmap {
            val bmp = Bitmap.createBitmap(side, side, Bitmap.Config.RGB_565).apply {
                eraseColor(Color.LTGRAY)
            }
            val canvas = Canvas(bmp)
            val strokeWidth = 3F
            val rect = Rect(0, 0, side, side)
            //region    rect,innerRect
            rectDraw(canvas,
                    strokePaint(Color.BLACK, 2 * strokeWidth),
                    rect)
            val aliseStrokeWdith = strokeWidth.toInt()

            val innerRect = Rect(aliseStrokeWdith, aliseStrokeWdith,
                    side - aliseStrokeWdith, side - aliseStrokeWdith)
            rectDraw(canvas,
                    strokePaint(Color.RED, 2 * strokeWidth),
                    innerRect)
            //endregion
            //region msg
            if (msg != null) {
                textDrawCenter(canvas,
                        textPaint(Color.BLUE, 40F),
                        rect,
                        msg)
            }
            //endregion
            return bmp
        }

        private fun deltaCenterHeightFromFont(paint: Paint) =
                paint.fontMetricsInt.let {
                    (it.top + it.bottom) / 2
                }

        private fun strokePaint(colorInt: Int, width: Float) =
                Paint().apply {
                    color = colorInt
                    style = Paint.Style.STROKE
                    strokeWidth = width
                }

        private fun textPaint(colorInt: Int, fontSize: Float) =
                Paint().apply {
                    color = colorInt
                    style = Paint.Style.FILL
                    textAlign = Paint.Align.CENTER
                    textSize = fontSize
                    flags = Paint.ANTI_ALIAS_FLAG
                }

        private fun rectDraw(canvas: Canvas, strokePaint: Paint, rect: Rect) {
            canvas.drawRect(rect, strokePaint)
        }

        private fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: Rect, msg: Any) {
            canvas.drawText("$msg",
                    rect.exactCenterX(), (rect.exactCenterY() - deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }
    }
}