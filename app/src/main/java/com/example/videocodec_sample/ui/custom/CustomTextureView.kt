package com.example.videocodec_sample.ui.custom

import android.annotation.SuppressLint
import android.content.AttributionSource
import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.databinding.DataBindingUtil
import com.example.videocodec_sample.databinding.ItemCustomGlsurfaceBinding
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CustomTextureView : GLSurfaceView, Preview.SurfaceProvider,
    GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    val TAG = this::class.java.simpleName
    var binding: ItemCustomGlsurfaceBinding? = null

    constructor(context: Context) : super(context) {
        initSetting()
    }

    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ) {
        initSetting()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding = DataBindingUtil.bind(this)
    }

    private fun initSetting() {
        setEGLContextClientVersion(2)
        setRenderer(this)
    }

    private fun initSurfaceTexture(){

    }

    // preview
    override fun onSurfaceRequested(request: SurfaceRequest) {

    }

    // surfaceTexture
    override fun onFrameAvailable(p0: SurfaceTexture?) {

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {

    }

    override fun onDrawFrame(p0: GL10?) {

    }
}