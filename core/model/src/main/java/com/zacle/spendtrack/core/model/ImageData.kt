package com.zacle.spendtrack.core.model

import android.graphics.Bitmap
import android.net.Uri

sealed class ImageData {
    data class UriImage(val uri: Uri): ImageData()
    data class BitmapImage(val bitmap: Bitmap): ImageData()
}