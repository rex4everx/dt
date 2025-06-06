package com.example.museart.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Утилита для загрузки и управления изображениями в приложении.
 * В реальном приложении здесь была бы логика загрузки на сервер,
 * но для демонстрации мы сохраняем изображения локально.
 */
class ImageUploader(private val context: Context) {

    /**
     * Загружает изображение и возвращает URL для доступа к нему.
     * В данной реализации сохраняет изображение в локальное хранилище приложения.
     */
    suspend fun uploadImage(imageFile: File): String = withContext(Dispatchers.IO) {
        try {
            // Создаем уникальное имя файла
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "JPEG_${timeStamp}_${UUID.randomUUID()}.jpg"
            
            // Создаем директорию для изображений, если она не существует
            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MuseArt")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            
            // Создаем файл для сохранения изображения
            val destinationFile = File(storageDir, fileName)
            
            // Оптимизируем и сохраняем изображение
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            val outputStream = FileOutputStream(destinationFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
            
            // Возвращаем URI изображения в виде строки
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                destinationFile
            )
            
            uri.toString()
        } catch (e: IOException) {
            throw IOException("Не удалось загрузить изображение", e)
        }
    }

    /**
     * Удаляет изображение по URL.
     */
    suspend fun deleteImage(imageUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(imageUrl)
            val file = uri.path?.let { File(it) }
            if (file != null && file.exists()) {
                file.delete()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Создает временный файл для хранения изображения при съемке камерой.
     */
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
}

