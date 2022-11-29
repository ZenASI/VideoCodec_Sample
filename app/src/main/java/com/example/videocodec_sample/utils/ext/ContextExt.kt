package com.example.videocodec_sample.utils.ext

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import java.io.ByteArrayOutputStream
import java.io.IOException

fun Context.getRawString(@RawRes resId: Int): String =
    try {
        resources.openRawResource(resId).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                var i = inputStream.read()
                while (i != -1) {
                    outputStream.write(i)
                    i = inputStream.read()
                }

                outputStream.toString()
            }
        }
    } catch (ex: IOException) {
        Log.e("Context", "getRawString: ", ex)
        throw ex
    }