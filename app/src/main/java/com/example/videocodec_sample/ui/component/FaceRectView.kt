package com.example.videocodec_sample.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.core.graphics.withScale
import com.example.videocodec_sample.R
import com.example.videocodec_sample.utils.face.FaceDetectorUtils
import com.google.mlkit.vision.face.Face
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

// TODO: 如換成後置鏡頭需要清除畫布
class FaceRectView(context: Context) : View(context), ImageAnalysis.Analyzer {
    private val TAG = this::class.java.simpleName

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private val textPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        textSize = 100f
    }
    private var mRect = Rect(0, 0, 0, 0)
    private val random = Random()

    private var viewSize = Size(0, 0)
    private var widthRatio = 0f
    private var heightRatio = 0f

    private var imageAnalysis: ImageAnalysis? = null
    private var faceDetectorUtils: FaceDetectorUtils? = null

    private var drawable: Drawable? = null
    private var need2Flip = true

    init {
        // init FaceDetectorUtils
        faceDetectorUtils = FaceDetectorUtils(context)
        faceDetectorUtils?.listener = object : FaceDetectorUtils.OnFaceListener {
            override fun faceInfos(face: Face) {
                updateRect(face = face)
            }
        }
        drawable = ContextCompat.getDrawable(context, R.drawable.shark)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewSize = Size(w, h)
        Log.d(TAG, "onSizeChanged: ${viewSize}")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { c ->
            c.drawRect(mRect, paint)
            drawable?.let { d ->
                d.setBounds(mRect.left, mRect.top, mRect.right, mRect.bottom)
                d.draw(c)
            }
            c.drawText("sharrk!!", mRect.left.toFloat(), mRect.top.toFloat(), textPaint)
        }
    }

    fun updateRect(face: Face) {
        if (widthRatio == 0f || heightRatio == 0f) return
        textPaint.setARGB(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        paint.setARGB(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
//        Log.d(
//            TAG,
//            "AngleX: ${face.headEulerAngleX}, AngleY: ${face.headEulerAngleY}, AngleZ: ${face.headEulerAngleZ}"
//        )
        face.boundingBox.apply {
            mRect.set(
                viewSize.width - (right * widthRatio).roundToInt(),
                (top * heightRatio).roundToInt(),
                viewSize.width - (left * widthRatio).roundToInt(),
                (bottom * heightRatio).roundToInt()
            )
        }
        invalidate()
    }

    fun setImageAnalysis(imageAnalysis: ImageAnalysis) {
        this.imageAnalysis = imageAnalysis
        this.imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(context), this)
    }

    fun setCameraSelector(value: CameraSelector) {
        need2Flip = value == CameraSelector.DEFAULT_FRONT_CAMERA
    }

    override fun analyze(imageProxy: ImageProxy) {
        if (viewSize.width == 0 || viewSize.height == 0) return
        widthRatio =
            if (viewSize.width > imageProxy.width) viewSize.width.toFloat() / imageProxy.width else imageProxy.width.toFloat() / viewSize.width
        heightRatio =
            if (viewSize.height > imageProxy.height) viewSize.height.toFloat() / imageProxy.height else imageProxy.height.toFloat() / viewSize.height

//        Log.d(TAG, "analyze: widthRatio:${widthRatio}, heightRatio:${heightRatio}")
        faceDetectorUtils?.putImageProxy(imageProxy)
    }
}