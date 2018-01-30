package sample.utils

import android.graphics.*

/**
 * Created by Administrator on 2018/1/23.
 */
class BmpUtils {
    companion object {
        //region    对外接口
        //模拟Pdfpage
        fun simPdfpage(width: Int,height: Int, lines:List<String>):Bitmap {
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                eraseColor(Color.WHITE)
                //region    最外层边框
                rectDraw(0, 8, Color.YELLOW, this)
                //endregion
            }
            //region    写文字
            val canvas = Canvas(bmp)
            textDrawLines(canvas,
                    textPaint(Color.BLUE, 40F),
                    Rect(0,0,bmp.width,bmp.height),
                    lines)
            //endregion

            return bmp
        }
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
        fun drawLineFrame(bmp:Bitmap, canvas: Canvas) {
            val linePaint = strokePaint(Color.RED, 2F)
            val pixels = IntArray(bmp.width * bmp.height, { 0 })
            bmp.getPixels(pixels,0,bmp.width,0,0,bmp.width,bmp.height)
            //region    横向扫描
            val width = bmp.width
            val height = bmp.height
            val MAX = 80
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val gray = pixels[width * y + x]
                    var alpha = gray.ushr(24)
                    var red = gray.ushr(16).and(0xFF)
                    var green = gray.ushr(8).and(0xFF)
                    var blue = gray.and(0xFF)
                    red = if (red > MAX) 255 else 0
                    green = if (green > MAX) 255 else 0
                    blue = if (blue > MAX) 255 else 0
                }
            }
            //endregion
        }
        //endregion

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
        private fun textDrawLines(canvas: Canvas, textPaint: Paint, rect: Rect, lines: List<String>) {
            val count = lines.size
            val deltaHeight = rect.height()/count
            val lineRect = Rect(0,0,rect.width(),deltaHeight)
            lines.forEach {
                canvas.drawText(it,
                        lineRect.exactCenterX(), (lineRect.exactCenterY() - deltaCenterHeightFromFont(textPaint)),
                        textPaint)
                lineRect.offset(0, deltaHeight)
            }
            /*canvas.drawText("$msg",
                    rect.exactCenterX(), (rect.exactCenterY() - deltaCenterHeightFromFont(textPaint)),
                    textPaint)*/
        }
    }
}