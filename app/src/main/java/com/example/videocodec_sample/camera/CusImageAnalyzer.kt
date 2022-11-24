package com.example.videocodec_sample.camera

import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class CusImageAnalyzer() : ImageAnalysis.Analyzer {
    private val TAG = this::class.java.simpleName
    override fun analyze(image: ImageProxy) {
        //            val buffer = image.planes[0].buffer
        Log.d(TAG, "analyze: ${Size(image.width, image.height)}")
//            faceDetector?.putImageProxy(image, Size(binding.container.width, binding.container.height))
        image.close()
    }
}