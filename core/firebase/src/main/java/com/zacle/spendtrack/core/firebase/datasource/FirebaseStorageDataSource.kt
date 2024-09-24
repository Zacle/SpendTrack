package com.zacle.spendtrack.core.firebase.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.zacle.spendtrack.core.data.datasource.StorageDataSource
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * [StorageDataSource] implementation for Firebase Storage
 */
class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
): StorageDataSource {
    /**
     * Uploads an image to Firebase Storage and returns the download URL.
     *
     * @param bucketName The Firebase Storage bucket or folder where the image will be uploaded.
     * @param imagePath The local file path of the image to be uploaded.
     * @return The download URL of the uploaded image or null if the upload fails
     */
    override suspend fun uploadImageToCloud(bucketName: String, imagePath: String): String? {
        val storageRef = storage.reference.child(bucketName)

        try {
            // Create a Uri object from the local image path
            val fileUri = Uri.fromFile(File(imagePath))

            // Upload the file to Firebase Storage
            val uploadTask = storageRef.putFile(fileUri).await()

            // Check if upload was successful
            if (uploadTask.task.isSuccessful) {
                // Retrieve the download URL after the upload completes
                val downloadUrl = storageRef.downloadUrl.await()

                // Log the obtained URL for debugging purposes
                Timber.d("Download URL: $downloadUrl")

                return downloadUrl.toString()
            } else {
                // Handle case where uploadTask failed without exception
                Timber.e("Upload failed: ${uploadTask.task.exception?.message}")
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Deletes an image from Firebase Storage.
     *
     * @param bucketName The Firebase Storage bucket or folder where the image is stored.
     * @param imagePath The full path of the image in Firebase Storage to be deleted.
     */
    override suspend fun deleteImageFromCloud(bucketName: String, imagePath: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("$bucketName/$imagePath")

        try {
            // Delete the image file from Firebase Storage
            storageRef.delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}