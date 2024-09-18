package com.zacle.spendtrack.core.data.datasource

interface StorageDataSource {
    /**
     * Uploads an image to Firebase Storage and returns the download URL.
     *
     * @param bucketName The Firebase Storage bucket or folder where the image will be uploaded.
     * @param imagePath The local file path of the image to be uploaded.
     * @return The download URL of the uploaded image or null if the upload fails
     */
    suspend fun uploadImageToCloud(bucketName: String, imagePath: String): String?

    /**
     * Uploads an image to Firebase Storage and returns the download URL.
     *
     * @param bucketName The Firebase Storage bucket or folder where the image will be uploaded.
     * @param imagePath The local file path of the image to be uploaded.
     * @return The download URL of the uploaded image or null if the upload fails
     */
    suspend fun deleteImageFromCloud(bucketName: String, imagePath: String)
}