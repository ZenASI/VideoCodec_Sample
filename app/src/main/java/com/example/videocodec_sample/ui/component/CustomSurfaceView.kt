package com.example.videocodec_sample.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.util.Size
import androidx.camera.core.Preview
import com.example.videocodec_sample.camera.CameraRender
import com.example.videocodec_sample.model.FilterItem
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CustomSurfaceView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    private val TAG = this::class.simpleName
    private var preview: Preview? = null
    var cameraRender: CameraRender? = null

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
        cameraRender?.onCreate(gl, preview)
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
}