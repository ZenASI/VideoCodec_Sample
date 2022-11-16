package com.example.videocodec_sample.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.videocodec_sample.R
import com.example.videocodec_sample.databinding.ActivityCameraPreviewBinding
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi")
class CameraPreview : AppCompatActivity() {

    val TAG = this::class.java.simpleName
    val binding by lazy {
        ActivityCameraPreviewBinding.inflate(layoutInflater)
    }

    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private val isDoProcess = MutableLiveData(false)

    private val cameraSelector = MutableLiveData(CameraSelector.DEFAULT_FRONT_CAMERA)
    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(baseContext)
    }

    private val cameraProvider by lazy {
        cameraProviderFuture.get()
    }

    private val imageCapture by lazy {
        ImageCapture.Builder().build()
    }
    private val imageAnalysis by lazy {
        ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(ContextCompat.getMainExecutor(baseContext), CustomImageAnalyzer())
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startCamera()
        initListener()
    }

    private fun initListener() {
        isDoProcess.observe(this, androidx.lifecycle.Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun startCamera() {

        cameraProviderFuture.addListener(Runnable {
            val preview = Preview.Builder().build().apply {
//                setSurfaceProvider(fuck.root)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector.value!!, preview, imageCapture, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e(TAG, "startCamera: ", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // takePicture btn
    private fun takePicture() {
        isDoProcess.value = true

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        baseContext,
                        "save success! ${outputOptions.metadata.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    isDoProcess.value = false
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "takePicture onError: ", exception)
                    isDoProcess.value = false
                }
            })
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.takePicture -> {
                takePicture()
            }
            R.id.cameraSwitch -> {
                if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                    cameraSelector.value = CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    cameraSelector.value = CameraSelector.DEFAULT_BACK_CAMERA
                }
                startCamera()
            }
            R.id.recordSwitch -> {

            }
        }
    }

    inner class CustomImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()
            image.close()
        }
    }
}