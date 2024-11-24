package com.example.myfirstapp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.content.ContentResolver
import java.io.InputStream
import java.io.ByteArrayOutputStream

object MediaPipeline {

    // Function to handle media file upload
    fun uploadMedia(uri: Uri, context: Context) {
        // Perform the upload on a background thread
        Thread {
            try {
                // Get the file path from the URI (handle content URIs)
                val file = getFileFromUri(uri, context)
                if (file == null || !file.exists()) {
                    Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
                    return@Thread
                }

                // Server URL to which the file will be uploaded
                val serverUrl = "https://plus-us.com/upload" // Replace with actual server URL

                // Set up the connection
                val connection = URL(serverUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                connection.doOutput = true

                // Create the multipart body for the file upload
                val outputStream = connection.outputStream
                val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
                val prefix = "--$boundary\r\n"
                val fileHeader = "Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"\r\n"
                val fileTypeHeader = "Content-Type: ${getMimeType(context, uri)}\r\n\r\n"
                val suffix = "\r\n--$boundary--\r\n"

                outputStream.write(prefix.toByteArray())
                outputStream.write(fileHeader.toByteArray())
                outputStream.write(fileTypeHeader.toByteArray())
                outputStream.write("\r\n".toByteArray())

                // Read the file and write it to the output stream
                val inputStream = context.contentResolver.openInputStream(uri)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.write("\r\n".toByteArray())
                outputStream.write(suffix.toByteArray())

                // Close streams
                inputStream?.close()
                outputStream.flush()

                // Check server response
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(context, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {
                Log.e("MediaPipeline", "Error uploading media: ${e.message}")
                Toast.makeText(context, "Error uploading media", Toast.LENGTH_SHORT).show()
            }
        }.start() // Start the background thread
    }

    // Helper method to get the file from the URI (handle content URIs)
    private fun getFileFromUri(uri: Uri, context: Context): File? {
        val contentResolver = context.contentResolver
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var file: File? = null
        try {
            val fileName = getFileName(uri, context)
            val tempFile = File(context.cacheDir, fileName)

            inputStream = contentResolver.openInputStream(uri)
            outputStream = tempFile.outputStream()

            val buffer = ByteArray(4096)
            var length: Int
            while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
                outputStream.write(buffer, 0, length)
            }

            file = tempFile
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }

        return file
    }

    // Helper method to get the file name from the URI
    private fun getFileName(uri: Uri, context: Context): String {
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val nameIndex = cursor?.getColumnIndexOrThrow("_display_name")
        val fileName = cursor?.getString(nameIndex ?: 0)
        cursor?.close()

        return fileName ?: "uploaded_file"
    }

    // Helper method to get MIME type of the file
    private fun getMimeType(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType ?: "application/octet-stream"
    }
}
