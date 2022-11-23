package com.example.videocodec_sample.camera

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import android.util.Size
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class FaceDetector(val context: Context) {

    private val TAG = this::class.java.simpleName

//    private val highAccuracyOpts = FaceDetectorOptions.Builder()
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//        .build()

    // Real-time contour detection
    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    init {
        Log.d(TAG, ": init!")
    }

    fun putByteBufferImage(byteBuffer: ByteBuffer, imageSize: Size) {
        val image = InputImage.fromByteBuffer(
            byteBuffer,
            /* image width */ imageSize.width,
            /* image height */ imageSize.height,
            0,
            InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )
        val detector = FaceDetection.getClient(realTimeOpts)
        detector.process(image)
            .addOnCompleteListener { face ->
                analyzeFaceInfo(faces = face.result)
            }
            .addOnFailureListener { exp ->
                Log.e(TAG, "putByteBufferImage: ", exp)
            }
    }

    fun putByteArrayImage(byteArray: ByteArray, imageSize: Size) {
        val image = InputImage.fromByteArray(
            byteArray,
            /* image width */ imageSize.width,
            /* image height */ imageSize.height,
            0,
            InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )
        val detector = FaceDetection.getClient(realTimeOpts)
        detector.process(image)
            .addOnCompleteListener { face ->
                analyzeFaceInfo(faces = face.result)
            }
            .addOnFailureListener { exp ->
                Log.e(TAG, "putByteArrayImage: ", exp)
            }
    }

    private fun analyzeFaceInfo(faces:List<Face>){
        for (face in faces) {
            val bounds = face.boundingBox
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
            leftEar?.let {
                val leftEarPos = leftEar.position
            }

            // If contour detection was enabled:
            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

            // If classification was enabled:
            if (face.smilingProbability != null) {
                val smileProb = face.smilingProbability
            }
            if (face.rightEyeOpenProbability != null) {
                val rightEyeOpenProb = face.rightEyeOpenProbability
            }

            // If face tracking was enabled:
            if (face.trackingId != null) {
                val id = face.trackingId
            }
        }
    }
}