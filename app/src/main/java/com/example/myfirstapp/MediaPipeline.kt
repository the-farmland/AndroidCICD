package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File

object MediaPipeline {

    /**
     * Launches the Media Picker, Gallery, or Document Picker as a fallback.
     * Attempts to prioritize opening the Media Picker or Gallery first.
     * @return The configured Intent to launch the appropriate picker.
     */
    fun launchMediaPicker(): Intent {
        val mediaPickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }

        val galleryIntent = Intent(Intent.ACTION_VIEW).apply {
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            type = "image/*"
        }

        val documentPickerIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        // Verify if any of the intents can resolve
        return Intent.createChooser(
            mediaPickerIntent,
            "Select Media"
        ).apply {
            // If Media Picker and Gallery aren't available, fallback to Document Picker
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(galleryIntent, documentPickerIntent))
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
