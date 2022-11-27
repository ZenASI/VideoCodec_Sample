package com.example.videocodec_sample.utils

import com.google.mlkit.vision.face.FaceDetectorOptions

object ConstantValue {
    // mlkit config
    const val PERFORMANCEMODE = FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
    const val LANDMARKMODE = FaceDetectorOptions.LANDMARK_MODE_NONE
    const val CLASSIFICATIONMODE = FaceDetectorOptions.CLASSIFICATION_MODE_ALL
    const val CONTOURMODE = FaceDetectorOptions.CONTOUR_MODE_NONE
}