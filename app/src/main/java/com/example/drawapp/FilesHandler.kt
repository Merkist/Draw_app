package com.example.drawapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asAndroidPathEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import java.io.IOException

fun saveBitmapToGallery(bitmap: Bitmap, context: Context) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, "Drawing_${System.currentTimeMillis()}")
        put(MediaStore.Images.Media.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    }

    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    try {
        uri?.let {
            val outputStream = contentResolver.openOutputStream(it)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            outputStream?.flush()
            outputStream?.close()

            // Оновлюємо галерею
            MediaScannerConnection.scanFile(context, arrayOf(uri.toString()), null, null)

            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_LONG).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }
}

fun createBitmapFromPaths(paths: List<PathData>, canvasSize: IntSize): Bitmap {
    val bitmap = Bitmap.createBitmap(canvasSize.width, canvasSize.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(Color.WHITE)

    paths.forEach { pathData ->
        val paint = Paint().apply {
            color = pathData.color.toArgb()
            strokeWidth = pathData.lineWidth
            style = Paint.Style.STROKE
            strokeCap = when (pathData.cap) {
                StrokeCap.Round -> android.graphics.Paint.Cap.ROUND
                StrokeCap.Square -> android.graphics.Paint.Cap.SQUARE
                StrokeCap.Butt -> android.graphics.Paint.Cap.BUTT
                else -> {android.graphics.Paint.Cap.ROUND}
            }
            pathEffect = pathData.pathEffect?.asAndroidPathEffect()
        }
        canvas.drawPath(pathData.path.asAndroidPath(), paint)
    }

    return bitmap
}

fun createBitmapFromPathsAndImage(bitmap: Bitmap,
                                  paths: List<PathData>,
                                  canvasSize: IntSize): Bitmap {
    // Створюємо новий Bitmap з таким же розміром, як оригінальний
    val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(newBitmap)
    canvas.drawColor(Color.WHITE)

    // Малюємо оригінальне зображення на новий canvas
    canvas.drawBitmap(bitmap, 0f, 0f, null)

    // Отримуємо розмір оригінального зображення
    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height

    // Маcштабуємо малюнок відповідно до розміру зображення
    paths.forEach { pathData ->
        val scaledPath = Path().apply {
            val matrix = Matrix()
            matrix.setScale(
                bitmapWidth / canvasSize.width.toFloat(),
                bitmapHeight / canvasSize.height.toFloat()
            )
            pathData.path.asAndroidPath().transform(matrix, this)
        }
        val paint = Paint().apply {
            color = pathData.color.toArgb()
            strokeWidth = pathData.lineWidth * (bitmapWidth / canvasSize.width.toFloat())
            style = Paint.Style.STROKE
            strokeCap = when (pathData.cap) {
                StrokeCap.Round -> android.graphics.Paint.Cap.ROUND
                StrokeCap.Square -> android.graphics.Paint.Cap.SQUARE
                StrokeCap.Butt -> android.graphics.Paint.Cap.BUTT
                else -> {android.graphics.Paint.Cap.ROUND}
            }
            pathEffect = if (pathData.pathEffect != null) {
                val scaleFactor = (pathData.lineWidth * (bitmapWidth / canvasSize.width.toFloat())) / 5f
                val adjustedIntervals = floatArrayOf(20f * scaleFactor, 10f * scaleFactor)
                DashPathEffect(adjustedIntervals, 0f)
            } else {
                null
            }
        }
        canvas.drawPath(scaledPath, paint)
    }

    return newBitmap
}

