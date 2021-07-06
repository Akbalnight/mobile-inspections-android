package ru.madbrains.data.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileUtil(
    val context: Context
) {

    fun createJpgFile(bitmap: Bitmap, file: File?): File? {
        file?.createNewFile()
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
}