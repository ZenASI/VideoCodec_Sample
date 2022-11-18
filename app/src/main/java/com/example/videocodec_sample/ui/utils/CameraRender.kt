package com.example.videocodec_sample.ui.utils

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLSurface
import javax.microedition.khronos.opengles.GL10

class CameraRender(val context: Context) : GLSurfaceView.Renderer, Preview.SurfaceProvider,
    SurfaceTexture.OnFrameAvailableListener {

    val TAG = this::class.java.simpleName

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }
    var surfaceTexture: SurfaceTexture? = null

    private val VERTEX_SHADER =
        "void main() {\n" + "gl_Position = vec4(0.0, 0.0, 0.0, 1.0);\n" + "gl_PointSize = 20.0;\n" + "}\n"
    private val FRAGMENT_SHADER =
        "void main() {\n" + "gl_FragColor = vec4(1., 0., 0.0, 1.0);\n" + "}\n"

    private var mGLProgram: Int = -1

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // onSurfaceCreated 通常拿來init
        // r, g, b, a
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        // shader vertex 頂點著色器
        val vsh = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vsh, VERTEX_SHADER)
        GLES20.glCompileShader(vsh)
        // shader fragment 片段著色器
        val fsh = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fsh, FRAGMENT_SHADER)
        GLES20.glCompileShader(fsh)

        // gl program
        mGLProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mGLProgram, vsh) // add vertex shader
        GLES20.glAttachShader(mGLProgram, fsh) // add fragment shader
        GLES20.glLinkProgram(mGLProgram) // linking

        // 由opengles 驗證 shader program 有無錯誤
        GLES20.glValidateProgram(mGLProgram)
        val programStatus = IntArray(1)
        // 取得驗證結果
        GLES20.glGetProgramiv(mGLProgram, GLES20.GL_VALIDATE_STATUS, programStatus, 0)
        // log print
        // 如果有语法错误，编译错误，或者状态出错，这一步是能够检查出来的。如果一切正常，则取出来的status[0]为0。
        Log.d(
            TAG,
            "onSurfaceCreated validate shader program: ${GLES20.glGetProgramInfoLog(mGLProgram)}"
        )

        // compile end =======================================================================================
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 参数是left, top, width, height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) // 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据。
        GLES20.glUseProgram(mGLProgram) // 告诉OpenGL，使用我们在onSurfaceCreated里面准备好了的shader program来渲染
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1) // 开始渲染，发送渲染点的指令， 第二个参数是offset，第三个参数是点的个数。目前只有一个点，所以是1。
    }

    // preview
    override fun onSurfaceRequested(request: SurfaceRequest) {

    }

    // surfaceTexture
    override fun onFrameAvailable(p0: SurfaceTexture?) {

    }

    fun shutdown() {
        executor?.shutdown()
    }
}