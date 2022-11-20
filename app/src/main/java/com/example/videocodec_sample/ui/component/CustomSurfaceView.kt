package com.example.videocodec_sample.ui.component

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import androidx.camera.core.Preview
import com.example.videocodec_sample.R
import com.example.videocodec_sample.camera.CameraRender
import com.example.videocodec_sample.model.FilterItem
import com.example.videocodec_sample.utils.ShaderUtils
import com.example.videocodec_sample.utils.ShaderUtils.buildProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CustomSurfaceView : GLSurfaceView, GLSurfaceView.Renderer {

    private val TAG = this::class.simpleName
    private var preview: Preview? = null
    var cameraRender: CameraRender? = null

    constructor(context: Context) : super(context) {
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

    override fun onSurfaceCreated(gl: GL10?, p1: EGLConfig?) {
        cameraRender?.onCreate(gl, preview)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraRender?.onChange(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        cameraRender?.onDrawFrame(gl)
    }

    fun setFilter(filterItem: FilterItem) {
        queueEvent(Runnable {
            // TODO: 待優畫 
            val a = buildProgram(context, R.raw.camera_vertex, filterItem.filterId)
            cameraRender?.mainProgram = a
        })
    }
}