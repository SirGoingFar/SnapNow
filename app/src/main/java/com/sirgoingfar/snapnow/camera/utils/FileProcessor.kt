package com.sirgoingfar.snapnow.camera.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileProcessor {

    companion object {

        val TYPE_JPEG = Bitmap.CompressFormat.JPEG
        val TYPE_PNG = Bitmap.CompressFormat.PNG

        fun getCompressedBitmapFilePathFrom(
            context: Context, bitmap: Bitmap, quality: Int = 75,
            format: Bitmap.CompressFormat = TYPE_JPEG
        ): String? {

            // save bitmap to Temp file
            val cacheFile: File

            try {

                cacheFile = getTempImageFile(context, format)

                //create the directory
                if (!cacheFile.exists())
                    cacheFile.mkdirs()

                //flush the stream into the file
                val stream = FileOutputStream(cacheFile)
                bitmap.compress(format, quality, stream)
                stream.close()

            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

            return cacheFile.absolutePath
        }


        @Throws(IOException::class)
        fun getTempImageFile(context: Context, format: Bitmap.CompressFormat): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "KUDI_$timeStamp"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: context.filesDir
            return File.createTempFile(imageFileName, getSuffix(format), storageDir)
        }


        private fun getSuffix(format: Bitmap.CompressFormat): String? {
            return when (format) {
                Bitmap.CompressFormat.PNG -> ".png"
                Bitmap.CompressFormat.JPEG -> ".jpeg"
                else -> null
            }
        }

    }

}