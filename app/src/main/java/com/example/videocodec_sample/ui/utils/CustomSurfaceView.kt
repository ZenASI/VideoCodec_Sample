package com.example.videocodec_sample.ui.utils

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import androidx.databinding.DataBindingUtil
import com.example.videocodec_sample.databinding.CustomGlsurfaceBinding
import java.util.jar.Attributes

class CustomSurfaceView : GLSurfaceView {

    val TAG = this::class.simpleName
    private var binding: CustomGlsurfaceBinding? = null
    val cameraRender by lazy {
        CameraRender(context)
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding = DataBindingUtil.bind(this)
        preserveEGLContextOnPause = true
        setEGLContextClientVersion(2)
        setRenderer(cameraRender)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onDetachedFromWindow() {
        cameraRender?.shutdown()
        super.onDetachedFromWindow()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
    }
}