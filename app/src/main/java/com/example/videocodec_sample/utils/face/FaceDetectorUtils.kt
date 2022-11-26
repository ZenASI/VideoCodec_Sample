package com.example.videocodec_sample.utils.face

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageProxy
import com.example.videocodec_sample.utils.ConstantValue
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.nio.ByteBuffer

class FaceDetectorUtils(val context: Context) {

    private val TAG = this::class.java.simpleName

    // Real-time contour detection
    private val realTimeOpts =
        FaceDetectorOptions.Builder().setPerformanceMode(ConstantValue.PERFORMANCEMODE)
            .setLandmarkMode(ConstantValue.LANDMARKMODE)
            .setClassificationMode(ConstantValue.CLASSIFICATIONMODE)
            .setContourMode(ConstantValue.CONTOURMODE)
            .enableTracking()
            .build()

    init {
        Log.d(TAG, ": init!")
    }

    interface OnFaceListener {
        fun faceInfos(face: Face)
    }

    var listener: OnFaceListener? = null

    @SuppressLint("UnsafeOptInUsageError")
    fun putImageProxy(cameraImage: ImageProxy) {
        if (cameraImage.image == null) return
        // set imageProxy size
        val image = InputImage.fromMediaImage(cameraImage.image!!, 0)
        val detector = FaceDetection.getClient(realTimeOpts)
        detector.process(image).addOnCompleteListener { face ->
            analyzeFaceInfo(faces = face.result)
            cameraImage.image!!.close()
            cameraImage.close()
        }.addOnFailureListener { exp ->
            Log.e(TAG, "putImageProxy: ", exp)
        }
    }

    private fun analyzeFaceInfo(faces: List<Face>) {
        faces.forEach {
            listener?.faceInfos(it)
        }
    }
}