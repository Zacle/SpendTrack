package com.zacle.spendtrack.core.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.model.ImageData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * A manager for saving images to local storage.
 */
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Saves an image to local storage.
     *
     * @param imageData The image in the form of a Bitmap (typically from the camera) or a Uri (from a gallery image).
     * @param filename The name to save the image as.
     *
     * @return The path of the saved image file, or null if the save failed.
     */
    suspend fun saveImageLocally(imageData: ImageData, filename: String): String? = withContext(ioDispatcher) {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFile = File(storageDir, "$filename.jpg")
        try {
            val outputStream = FileOutputStream(imageFile)

            when (imageData) {
                is ImageData.BitmapImage -> imageData.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                is ImageData.UriImage -> {
                    val inputStream = context.contentResolver.openInputStream(imageData.uri)
                    val uriBitmap = BitmapFactory.decodeStream(inputStream)
                    uriBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                else -> return@withContext null
            }
            outputStream.flush()
            outputStream.close()

            return@withContext imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Deletes an image from local storage.
     *
     * @param imagePath The path of the image file to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    suspend fun deleteImageLocally(imagePath: String): Boolean = withContext(ioDispatcher) {
        try {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                return@withContext imageFile.delete()
            } else {
                return@withContext false // File does not exist
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false // Return false if an exception occurred
        }
    }
}