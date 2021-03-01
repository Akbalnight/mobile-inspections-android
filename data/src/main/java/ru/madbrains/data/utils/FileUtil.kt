package ru.madbrains.data.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileUtil(
        val context: Context
) {

    fun createFile(bitmap: Bitmap, filename: String): File {
        val file = File(context.cacheDir, filename).apply { createNewFile() }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        val bitmapData = stream.toByteArray()
        FileOutputStream(file).apply {
            write(bitmapData)
            flush()
            close()
        }
        return file
    }

    fun createFile(data: ByteArray, filename: String): File {
        val file = File(context.cacheDir, filename).apply { createNewFile() }
        FileOutputStream(file).apply {
            write(data)
            close()
        }
        return file
    }

    fun createFile(data: Int, filename: String): File {
        val file = File(context.cacheDir, filename).apply { createNewFile() }
        val bitmap = ContextCompat.getDrawable(context, data)!!.toBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bitmapData = stream.toByteArray()
        FileOutputStream(file).apply {
            write(bitmapData)
            flush()
            close()
        }
        return file
    }
}