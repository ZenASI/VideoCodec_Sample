package com.example.videocodec_sample.camera

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
import com.example.videocodec_sample.model.FilterItem
import com.example.videocodec_sample.utils.BufferUtils
import com.example.videocodec_sample.utils.ShaderUtils
import java.util.concurrent.Executors
import javax.microedition.khronos.opengles.GL10

class CameraRender(val context: Context) : Preview.SurfaceProvider,
    SurfaceTexture.OnFrameAvailableListener {

    private val vertexBuffer = BufferUtils.createBuffer(
        1.0f, -1.0f,        // Right-bottom
        -1.0f, -1.0f,       // Left-bottom
        1.0f, 1.0f,         // Right-top
        -1.0f, 1.0f         // Left-top
    )

    private val textureCoordinatesBuffer = BufferUtils.createBuffer(
        1.0f, 0.0f,     // Left-bottom
        0.0f, 0.0f,     // Right-bottom
        1.0f, 1.0f,     // Left-top
        0.0f, 1.0f      // Right-top
    )

    private val rotatedTextureCoordinatesBuffer = BufferUtils.createBuffer(
        1.0f, 0.0f,     // Left-bottom
        1.0f, 1.0f,     // Right-bottom
        0.0f, 0.0f,     // Left-top
        0.0f, 1.0f      // Right-top
    )

    private val TAG = this::class.java.simpleName
    public var mainProgram = 0
    private var surfaceTexture: SurfaceTexture? = null
    private var tex: IntArray = IntArray(1)
    private val matrix = FloatArray(16)
    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    override fun onSurfaceRequested(request: SurfaceRequest) {
        val size = request.resolution
        surfaceTexture?.setDefaultBufferSize(size.width, size.height)
        val surface = Surface(surfaceTexture)
        Log.d(TAG, "onSurfaceRequested: call ${surfaceTexture == null}")
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
        surfaceTexture = SurfaceTexture(tex[0])
        surfaceTexture?.setOnFrameAvailableListener(this)


        mainProgram = ShaderUtils.buildProgram(context, R.raw.camera_vertex, R.raw.original)

        ContextCompat.getMainExecutor(context).execute {
            preview?.setSurfaceProvider(this)
        }
    }

    fun onChange(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    fun onDrawFrame(gk: GL10?) {
        Log.d(TAG, "onDrawFrame: ")
        try {
            surfaceTexture?.updateTexImage()
            surfaceTexture?.getTransformMatrix(matrix)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "onDrawFrame: ${e}")
        }
        // Clear the color buffer
        GLES31.glClear(GL_COLOR_BUFFER_BIT)

        // Use shaders for rendering texture from a camera
        GLES31.glUseProgram(mainProgram)

        // Prepare to render texture from a camera
        val iChannel0Location = GLES31.glGetUniformLocation(mainProgram, "iChannel0")
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
        GLES31.glUniform1i(iChannel0Location, 0)

        val vPositionLocation = GLES31.glGetAttribLocation(mainProgram, "vPosition")
        GLES31.glEnableVertexAttribArray(vPositionLocation)
        GLES31.glVertexAttribPointer(
            vPositionLocation,
            2,
            GLES31.GL_FLOAT,
            false,
            4 * 2,
            vertexBuffer
        )

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

        GLES31.glBindTexture(GL_TEXTURE_2D, tex[0])
        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP, 0, 4)
        GLES31.glFlush()
    }
}