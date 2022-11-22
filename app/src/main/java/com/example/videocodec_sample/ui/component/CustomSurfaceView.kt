package com.example.videocodec_sample.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.util.Size
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import com.example.videocodec_sample.camera.CameraRender
import com.example.videocodec_sample.model.FilterItem
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CustomSurfaceView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    private val TAG = this::class.simpleName
    private var preview: Preview? = null
    var cameraRender: CameraRender? = null
//    var mRatioWidth = 0
//    var mRatioHeight = 0

    init {
        cameraRender = CameraRender(context)
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

    override fun onSurfaceCreated(gl: GL10?, glConfig: EGLConfig?) {
        holder.setKeepScreenOn(true)
        cameraRender?.onCreate(gl, preview)
    }

    @SuppressLint("RestrictedApi")
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val cameraSize = preview?.attachedSurfaceResolution ?: Size(0, 0)
//        val previewWidth = cameraSize.width
//        val previewHeight = cameraSize.height
//        if (width > height) {
//            setAspectRatio(previewWidth, previewHeight)
//        } else {
//            setAspectRatio(previewHeight, previewWidth)
//        }
        cameraRender?.onChange(gl, width, height)
    }

//    private fun setAspectRatio(width: Int, height: Int) {
//        mRatioWidth = width
//        mRatioHeight = height
//        ContextCompat.getMainExecutor(context).execute {
//            requestLayout()
//        }
//    }

    override fun onDrawFrame(gl: GL10?) {
        cameraRender?.onDrawFrame(gl)
    }

    fun setFilter(filterItem: FilterItem) =
        queueEvent {
            cameraRender?.updateGLProgram(filterItem.filterId)
        }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//        if (mRatioWidth == 0 || mRatioHeight == 0) {
//            setMeasuredDimension(width, height)
//        } else {
//            if (width < height * mRatioWidth / mRatioHeight) {
//                Log.d(TAG, "onMeasure: width=$width, height=${width * mRatioHeight / mRatioWidth}")
//                setMeasuredDimension(
//                    width,
//                    width * mRatioHeight / mRatioWidth
//                )
//            } else {
//                Log.d(TAG, "onMeasure: width=${height * mRatioWidth / mRatioHeight}, height=${height}")
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight,
//                    height
//                )
//            }
//        }
//    }
}