package com.example.mealflow.database.community

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
    return try {
        val file = File(context.getDatabasePath(fileName).absolutePath)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        file.absolutePath // 🔹 Return the path to save it in the database
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun loadBitmapFromStorage(path: String): Bitmap? {
    return try {
        BitmapFactory.decodeFile(path)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
