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
            val width = 6
            //region pixRect
            rectDraw(0, width, Color.BLACK, bmp)
            rectDraw(width, width, Color.RED, bmp)
            //endregion
            val canvas = Canvas(bmp)

            val rect = Rect(0, 0, side, side)

            //region    rect,innerRect
            /*val strokeWidth = width.toFloat()
            rectDraw(canvas,
                    strokePaint(Color.BLACK, 2 * strokeWidth),
                    rect)

            val innerRect = Rect(width, width,
                    side - width, side - width)
            rectDraw(canvas,
                    strokePaint(Color.RED, 2 * strokeWidth),
                    innerRect)*/
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

        private fun rectDraw(offset:Int, width:Int, color:Int, bmp:Bitmap) {
            val endX = bmp.width - offset - 1
            val endY = bmp.height - offset - 1
            for (x in offset until offset + width) {
                for (y in offset..endY) {
                    bmp.setPixel(x, y, color)
                }
            }
            for (x in endX downTo endX - width + 1) {
                for (y in offset..endY) {
                    bmp.setPixel(x, y, color)
                }
            }
            for (y in offset until offset + width) {
                for (x in offset..endX) {
                    bmp.setPixel(x, y, color)
                }
            }
            for (y in endY downTo endY - width + 1) {
                for (x in offset..endX) {
                    bmp.setPixel(x, y, color)
                }
            }
        }

        private fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: Rect, msg: Any) {
            canvas.drawText("$msg",
                    rect.exactCenterX(), (rect.exactCenterY() - deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }
    }
}