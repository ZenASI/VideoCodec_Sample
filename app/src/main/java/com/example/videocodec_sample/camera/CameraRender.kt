package com.example.videocodec_sample.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLES30.GL_COLOR_BUFFER_BIT
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES31
import android.util.Log
import android.view.Surface
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.core.content.ContextCompat
import com.example.videocodec_sample.R
import com.example.videocodec_sample.utils.BufferUtils
import com.example.videocodec_sample.utils.ShaderUtils
import java.nio.FloatBuffer
import java.util.concurrent.Executors
import javax.microedition.khronos.opengles.GL10

class CameraRender(val context: Context) : Preview.SurfaceProvider,
    SurfaceTexture.OnFrameAvailableListener {

    private val vertexBuffer = BufferUtils.createBuffer(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    private val textureCoordinatesBuffer = BufferUtils.createBuffer(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
//        1.0f, 0.0f,
//        1.0f, 1.0f,
//        0.0f, 0.0f,
//        0.0f, 1.0f
    )

    private val rotatedTextureCoordinatesBuffer = BufferUtils.createBuffer(
        1.0f, 0.0f,     // Left-bottom
        1.0f, 1.0f,     // Right-bottom
        0.0f, 0.0f,     // Left-top
        0.0f, 1.0f      // Right-top
    )

    private val TAG = this::class.java.simpleName
    private var mainProgram = 0
    private var surfaceTexture: SurfaceTexture? = null
    private var tex: IntArray = IntArray(1)
    private val matrix = FloatArray(16)
    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    // def filter
    private var currentFilter = R.raw.original
    private var width: Int = 0
    private var height: Int = 0

    override fun onSurfaceRequested(request: SurfaceRequest) {
        val size = request.resolution
        surfaceTexture?.setDefaultBufferSize(size.width, size.height)
        val surface = Surface(surfaceTexture)
        request.provideSurface(surface, executor) {
//            surfaceTexture?.release()
//            surface.release()
        }
    }

    override fun onFrameAvailable(p0: SurfaceTexture?) {
        // do nothing
    }

    fun shutdown() {
        Log.d(TAG, "shutdown: execute")
        executor.shutdown()
    }

    fun release() {
        Log.d(TAG, "release: render")
        surfaceTexture?.setOnFrameAvailableListener(null)
        surfaceTexture?.release()

        GLES30.glDeleteTextures(1, tex, 0)

        mainProgram = 0
    }

    fun onCreate(gl: GL10?, preview: Preview?) {
        val arr = IntArray(1)
        GLES31.glGenTextures(1, arr, 0)
        GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, arr[0])
        GLES31.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLES31.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLES30.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES30.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)

        surfaceTexture = SurfaceTexture(tex[0])
        surfaceTexture?.setOnFrameAvailableListener(this)

        mainProgram = ShaderUtils.buildProgram(context, R.raw.camera_vertex, currentFilter)

        ContextCompat.getMainExecutor(context).execute {
            preview?.setSurfaceProvider(this)
        }
    }

    fun onChange(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES30.glViewport(0, 0, width, height)
    }

    fun onDrawFrame(gk: GL10?) {
        // Clear the color buffer
        GLES31.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        // Use shaders for rendering texture from a camera
        GLES31.glUseProgram(mainProgram)
        try {
            surfaceTexture?.updateTexImage()
            surfaceTexture?.getTransformMatrix(matrix)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "onDrawFrame: ${e}")
        }
        // Prepare to render texture from a camera
        val iChannel0Location = GLES31.glGetUniformLocation(mainProgram, "iChannel0")
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
        GLES31.glUniform1i(iChannel0Location, 0)

        val vPositionLocation = GLES31.glGetAttribLocation(mainProgram, "vPosition")
        GLES31.glEnableVertexAttribArray(vPositionLocation)
        GLES31.glVertexAttribPointer(vPositionLocation, 2, GLES31.GL_FLOAT, false, 4 * 2, vertexBuffer)

        val vTexCoordLocation = GLES31.glGetAttribLocation(mainProgram, "vTexCoord")
        GLES31.glEnableVertexAttribArray(vTexCoordLocation)
        GLES31.glVertexAttribPointer(
            vTexCoordLocation,
            2,
            GLES31.GL_FLOAT,
            false,
            4 * 2,
            textureCoordinatesBuffer
        )

        val textureMatrixId = GLES30.glGetUniformLocation(mainProgram, "vMatrix");
        GLES31.glUniformMatrix4fv(textureMatrixId, 1, false, matrix, 0)

        checkExtFilter()

        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP , 0, 4)
    }

    private fun checkExtFilter() {
        when (currentFilter) {
            R.raw.pixelize, R.raw.money, R.raw.ascii, R.raw.cartoon -> {
                val iResolutionHandle = GLES31.glGetUniformLocation(mainProgram, "iResolution")
                GLES31.glUniform3fv(iResolutionHandle, 1, BufferUtils.createBuffer(width.toFloat(), height.toFloat()))
            }
            R.raw.triangles_mosaic -> {
                val blockSize = GLES31.glGetUniformLocation(mainProgram, "tileNum")
                GLES31.glUniform2fv(blockSize, 1, FloatBuffer.wrap(floatArrayOf(50f, 100f, 1.0f)))
            }
            else -> {

            }
        }
    }

    fun updateGLProgram(filterId: Int) {
        currentFilter = filterId
        mainProgram = ShaderUtils.buildProgram(context, R.raw.camera_vertex, filterId)
    }
}