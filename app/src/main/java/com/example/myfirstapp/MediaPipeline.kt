package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File

object MediaPipeline {

    /**
     * Launches the Media Picker to allow users to select media files.
     */
    fun launchMediaPicker(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }

    /**
     * Processes the URI selected from the Media Picker and retrieves the corresponding file.
     * @param uri The URI of the selected media file.
     * @param context The application context.
     * @return The File object representing the selected media, or null if the file cannot be processed.
     */
    fun getFileFromUri(uri: Uri, context: Context): File? {
        try {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            context.contentResolver.query(uri, filePathColumn, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0])
                    val filePath = cursor.getString(columnIndex)
                    return File(filePath)
                }
            }
        } catch (e: Exception) {
            Log.e("MediaPipeline", "Failed to retrieve file from URI: ${e.localizedMessage}")
        }
        return null
    }

    /**
     * Uploads the selected media to a remote server or cloud storage.
     * This function can be customized for Firebase or any other backend.
     * @param uri The URI of the selected media file.
     * @param context The application context.
     */
    fun uploadMedia(uri: Uri, context: Context) {
        val file = getFileFromUri(uri, context)
        if (file != null) {
            Log.d("MediaPipeline", "Uploading file: ${file.absolutePath}")
            // Add your upload logic here, e.g., Firebase storage upload or REST API call
        } else {
            Log.e("MediaPipeline", "Unable to upload. File is null.")
        }
    }
}
