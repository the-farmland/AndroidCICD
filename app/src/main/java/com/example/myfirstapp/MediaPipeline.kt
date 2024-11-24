package com.example.myfirstapp

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object MediaPipeline {

    fun getFileFromUri(uri: Uri, context: Context): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = uri.lastPathSegment ?: "selected_file"
            val tempFile = File(context.cacheDir, fileName)

            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    copyStream(input, output)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("MediaPipeline", "Error handling file: ${e.message}")
            null
        }
    }

    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }
}
