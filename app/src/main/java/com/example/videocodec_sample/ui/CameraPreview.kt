package com.example.videocodec_sample.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videocodec_sample.R
import com.example.videocodec_sample.adapter.FilterAdapter
import com.example.videocodec_sample.databinding.ActivityCameraPreviewBinding
import com.example.videocodec_sample.ui.component.CustomSurfaceView
import com.example.videocodec_sample.ui.component.FaceRectView
import com.example.videocodec_sample.utils.FilterUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @see CustomSurfaceView 繪製濾鏡
 * @see FaceRectView 臉部輪廓追蹤
 * DEFAULT_FRONT_CAMERA scale(-1, 1)
 * DEFAULT_BACK_CAMERA scale(1, 1)
 */
@SuppressLint("RestrictedApi")
class CameraPreview : BaseActivity() {

    val TAG = this::class.java.simpleName
    val binding by lazy {
        ActivityCameraPreviewBinding.inflate(layoutInflater)
    }

    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private val isDoProcess = MutableLiveData(false)

    var camera: Camera? = null
    private val cameraSelector = MutableLiveData(CameraSelector.DEFAULT_FRONT_CAMERA)
    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(baseContext)
    }
    private val cameraProvider by lazy {
        cameraProviderFuture.get()
    }

    private val imageCapture by lazy {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
    }

    private val imageAnalysis by lazy {
        ImageAnalysis.Builder()
            .setOutputImageRotationEnabled(true)
            .setTargetRotation(Surface.ROTATION_0)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    // 繪製濾鏡
    private var customSurfaceView: CustomSurfaceView? = null
    // 捕捉人臉輪廓
    private var faceRectView: FaceRectView? = null

    private val filterAdapter: FilterAdapter by lazy {
        FilterAdapter(FilterUtils.getAllFilter())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startCamera()
        initListener()
        binding.cameraFilterRV.apply {
            layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
            filterAdapter.notifyDataSetChanged()
            filterAdapter.listener = {
//                Toast.makeText(baseContext, it.toString(), Toast.LENGTH_SHORT).show()
                customSurfaceView?.setFilter(FilterUtils.getAllFilter()[it])
            }
        }
    }

    private fun initListener() {
        isDoProcess.observe(this) {
            if (it) {
                binding.progressBar.visibility = VISIBLE
            } else {
                binding.progressBar.visibility = GONE
            }
        }
        binding.recordSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

            } else {

            }
        }
    }

    override fun onPause() {
        super.onPause()
        customSurfaceView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        customSurfaceView?.onResume()
    }

    private fun removeCameraStateObservers(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.removeObservers(this)
    }

    private fun cameraStateObserve(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this) { state ->
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
        }
    }

    private fun startCamera() {
        // def scale -1f
//        binding.drawContainer.scaleX = -1f
        cameraProviderFuture.addListener({
            bindCameraUseCase()
        }, ContextCompat.getMainExecutor(this))
    }

    // bind usecase
    private fun bindCameraUseCase() {
        if (customSurfaceView != null) binding.container.removeView(customSurfaceView)
        if (faceRectView != null) binding.drawContainer.removeView(faceRectView)

        customSurfaceView = CustomSurfaceView(baseContext)
        binding.container.addView(customSurfaceView)
        val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build().also {
            customSurfaceView?.setPreview(it)
        }

        faceRectView = FaceRectView(baseContext)
        faceRectView?.setImageAnalysis(imageAnalysis)
        faceRectView?.setCameraSelector(cameraSelector.value!!)
        binding.drawContainer.addView(faceRectView)

        val aspectRatio = Rational(binding.container.height, binding.container.width)
        val viewPort =
            ViewPort.Builder(aspectRatio, Surface.ROTATION_0)
                .setScaleType(ViewPort.FILL_CENTER)
                .build()
        val useCaseGroup = UseCaseGroup.Builder()
            .setViewPort(viewPort)
            .addUseCase(preview)
            .addUseCase(imageAnalysis)
            .addUseCase(imageCapture)
            .build()

        try {
            cameraProvider.unbindAll()
            if (camera != null) removeCameraStateObservers(camera!!.cameraInfo)
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector.value!!, useCaseGroup
            )
            cameraStateObserve(cameraInfo = camera?.cameraInfo!!)
        } catch (exc: Exception) {
            Log.e(TAG, "startCamera: ", exc)
        }
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
//                    binding.drawContainer.scaleX = -1f
                } else {
                    cameraSelector.value = CameraSelector.DEFAULT_BACK_CAMERA
//                    binding.drawContainer.scaleX = 1f
                }
                bindCameraUseCase()
            }
            R.id.recordSwitch -> {

            }
        }
    }
}