package ca.warp7.android.scouting.v5.ui

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.util.*


/**
 * Creates a Bitmap from a BitMatrix
 *
 *
 * Code modified from
 * https://github.com/journeyapps/zxing-android-embedded/
 *
 *
 * LICENSED UNDER Apache 2.0
 */

private fun createBitmap0(matrix: BitMatrix): Bitmap {

    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}

fun createQRBitmap(message: String, dim: Int): Bitmap {
    val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
    hints[EncodeHintType.MARGIN] = 0
    return createBitmap0(MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, dim, dim, hints))
}