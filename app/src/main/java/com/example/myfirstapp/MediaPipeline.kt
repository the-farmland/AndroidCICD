package com.example.myfirstapp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.HttpURLConnection

object MediaPipeline {

    // Function to handle media file upload
    fun uploadMedia(uri: Uri, context: Context) {
        try {
            // Get the file path from the URI
            val file = File(uri.path ?: "")
            if (!file.exists()) {
                Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
                return
            }

            // Upload the file to the server (Replace with actual upload logic)
            val serverUrl = "https://plus-us.com/upload" // Placeholder URL
            val connection = URL(serverUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "multipart/form-data")
            connection.doOutput = true

            // Upload the file (Replace with proper multipart form-data handling)
            val outputStream = connection.outputStream
            outputStream.write(file.readBytes())
            outputStream.flush()

            // Check the response
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "File uploaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Log.e("MediaPipeline", "Error uploading media: ${e.message}")
            Toast.makeText(context, "Error uploading media", Toast.LENGTH_SHORT).show()
        }
    }
}
