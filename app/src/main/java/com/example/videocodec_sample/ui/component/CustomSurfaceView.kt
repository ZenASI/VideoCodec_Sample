package com.example.videocodec_sample.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import com.example.videocodec_sample.camera.CameraRender
import com.example.videocodec_sample.model.FilterItem
import com.example.videocodec_sample.utils.FaceDetectorUtils
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 主要做gl繪製
 * 分析圖片
 */
class CustomSurfaceView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer,
    ImageAnalysis.Analyzer {

    private val TAG = this::class.simpleName

    private var preview: Preview? = null
    private var cameraRender: CameraRender? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var faceDetectorUtils:FaceDetectorUtils? = null

    var listener: ((imageRect:Rect) -> Unit)? = null

    init {
        cameraRender = CameraRender(context)
        faceDetectorUtils = FaceDetectorUtils(context)
        faceDetectorUtils?.listener = object : FaceDetectorUtils.OnFaceListener{
            override fun faceBounds(x: Float, y: Float, rect: Rect) {
                listener?.invoke(rect)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        preserveEGLContextOnPause = true
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onDetachedFromWindow() {
        cameraRender?.shutdown()
        cameraRender?.release()
        super.onDetachedFromWindow()
    }

    fun setPreview(preview: Preview) {
        this.preview = preview
    }

    fun setImageAnalyzer(imageAnalysis: ImageAnalysis) {
        this.imageAnalysis = imageAnalysis
    }

    override fun onSurfaceCreated(gl: GL10?, glConfig: EGLConfig?) {
        cameraRender?.onCreate(gl, preview)

        // init analyzer
        imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(context), this)
    }

    @SuppressLint("RestrictedApi")
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        val cameraSize = preview?.attachedSurfaceResolution ?: Size(0, 0)
        cameraRender?.onChange(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        cameraRender?.onDrawFrame(gl)
    }

    fun setFilter(filterItem: FilterItem) =
        queueEvent {
            cameraRender?.updateGLProgram(filterItem.filterId)
        }

    override fun analyze(image: ImageProxy) {
        //        val buffer = image.planes[0].buffer
        Log.d(TAG, "analyze: ${Size(image.width, image.height)}")
//        listener?.invoke(image)
        faceDetectorUtils?.putImageProxy(image)
//        image.close()
    }
}