package com.spren.sprenui.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream


object Image {
    /**
     * Rotate an image if required.
     *
     * @param fileDescriptor FileDescriptor
     * @param bitmap           The image bitmap
     * @return The resulted Bitmap after manipulation
     */
    @Throws(IOException::class)
    fun rotateImageIfRequired(fileDescriptor: FileDescriptor, bitmap: Bitmap): Bitmap? {
        val ei = ExifInterface(fileDescriptor)
        val orientation: Int =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
            else -> bitmap
        }
    }

    fun base64EncodeImage(image: Uri, contentResolver: ContentResolver): String {
        val bytes: ByteArray = readBytes(image, contentResolver)
        val encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)

        return encodedBase64
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
//        val b: ByteArray = baos.toByteArray()
//
//        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    @Throws(IOException::class)
    private fun readBytes(uri: Uri, resolver: ContentResolver): ByteArray {
        // this dynamically extends to take the bytes you read
        val inputStream: InputStream? = resolver.openInputStream(uri)
        val byteBuffer = ByteArrayOutputStream()

        // this is storage overwritten on each iteration with bytes
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        // we need to know how may bytes were read to write them to the
        // byteBuffer
        var len = 0
        while (inputStream!!.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray()
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }
}