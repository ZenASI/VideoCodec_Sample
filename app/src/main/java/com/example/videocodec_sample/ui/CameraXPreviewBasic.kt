package com.example.videocodec_sample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.videocodec_sample.R
import com.example.videocodec_sample.ui.component.CustomSurfaceView

class CameraXPreviewBasic : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    private val cameraSelector = MutableLiveData(CameraSelector.DEFAULT_FRONT_CAMERA)
    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(baseContext)
    }

    private var camera: Camera? = null
    private val cameraProvider by lazy {
        cameraProviderFuture.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_xpreview_basic)
        startCamera()

    }

    private fun startCamera() {
        cameraProviderFuture.addListener(Runnable {
            bindCameraUseCase()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCase() {
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build().also {
                val previewX = findViewById(R.id.previewX) as PreviewView
                it.setSurfaceProvider(previewX.surfaceProvider)
            }

        try {
            cameraProvider.unbindAll()
            if (camera != null) removeCameraStateObservers(camera!!.cameraInfo)
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector.value!!, preview
            )
            cameraStateObserve(cameraInfo = camera?.cameraInfo!!)
        } catch (exc: Exception) {
            Log.e(TAG, "startCamera: ", exc)
        }
    }

    private fun removeCameraStateObservers(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.removeObservers(this)
    }

    private fun cameraStateObserve(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this, androidx.lifecycle.Observer { state ->
            when (state.type) {
                CameraState.Type.PENDING_OPEN -> Toast.makeText(
                    baseContext, "CameraState: PENDING_OPEN", Toast.LENGTH_SHORT
                ).show()
                CameraState.Type.OPENING -> Toast.makeText(
                    baseContext, "CameraState: OPENING", Toast.LENGTH_SHORT
                ).show()
                CameraState.Type.OPEN -> Toast.makeText(
                    baseContext, "CameraState: OPEN", Toast.LENGTH_SHORT
                ).show()
                CameraState.Type.CLOSING -> Toast.makeText(
                    baseContext, "CameraState: CLOSING", Toast.LENGTH_SHORT
                ).show()
                CameraState.Type.CLOSED -> Toast.makeText(
                    baseContext, "CameraState: CLOSED", Toast.LENGTH_SHORT
                ).show()
                else -> {
                    throw RuntimeException("not in allow state")
                }
            }
        })
    }
}