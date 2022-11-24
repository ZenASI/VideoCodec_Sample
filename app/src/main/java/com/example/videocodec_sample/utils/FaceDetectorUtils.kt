package com.example.videocodec_sample.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageProxy
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
        fun faceBounds(x: Float, y: Float, rect: Rect)
    }

    var listener: OnFaceListener? = null

    fun putByteBufferImage(byteBuffer: ByteBuffer, imageSize: Size) {
        val image = InputImage.fromByteBuffer(
            byteBuffer,
            /* image width */
            imageSize.width,
            /* image height */
            imageSize.height, 0, InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )
        val detector = FaceDetection.getClient(realTimeOpts)
        detector.process(image).addOnCompleteListener { face ->
            analyzeFaceInfo(faces = face.result)
        }.addOnFailureListener { exp ->
            Log.e(TAG, "putByteBufferImage: ", exp)
        }
    }

    fun putByteArrayImage(byteArray: ByteArray, imageSize: Size) {
        val image = InputImage.fromByteArray(
            byteArray,
            /* image width */
            imageSize.width,
            /* image height */
            imageSize.height, 0, InputImage.IMAGE_FORMAT_YV12 // or IMAGE_FORMAT_YV12
        )
        val detector = FaceDetection.getClient(realTimeOpts)
        detector.process(image).addOnCompleteListener { face ->
            analyzeFaceInfo(faces = face.result)
        }.addOnFailureListener { exp ->
            Log.e(TAG, "putByteArrayImage: ", exp)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun putImageProxy(cameraImage: ImageProxy) {
        if (cameraImage.image == null) return
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
        for (face in faces) {
            val bounds = face.boundingBox
//            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
            Log.d(TAG, "analyzeFaceInfo: ${bounds}")
            listener?.faceBounds(face.headEulerAngleX, face.headEulerAngleY, bounds)

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
//            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
//            leftEye?.let {
//                val leftEarPos = leftEye.position
//            }
//
//            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
//            rightEye?.let {
//                val rughtEarPos = rightEye.position
//            }

            // If contour detection was enabled:
//            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
//            val rightEyeContour = face.getContour(FaceContour.RIGHT_EYE)?.points
//
//            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

            // If classification was enabled:
//            if (face.smilingProbability != null) {
//                val smileProb = face.smilingProbability
//            }
//            if (face.rightEyeOpenProbability != null) {
//                val rightEyeOpenProb = face.rightEyeOpenProbability
//            }

            // If face tracking was enabled:
//            if (face.trackingId != null) {
//                val id = face.trackingId
//            }
        }
    }
}