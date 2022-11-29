package com.example.videocodec_sample.ui

import android.media.MediaExtractor
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.videocodec_sample.R
import com.example.videocodec_sample.databinding.ActivityVideoDecodeBinding
import com.example.videocodec_sample.model.video.VideoItem
import com.example.videocodec_sample.model.video.VideoItemJsonAdapter
import com.example.videocodec_sample.model.video.VideosDataJsonAdapter
import com.example.videocodec_sample.ui.base.BaseActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class VideoDecode : BaseActivity(), SurfaceHolder.Callback {
    private val TAG = this::class.java.simpleName

    private var videoJson = ""
    private var videoList = mutableListOf<VideoItem>()
    private var isJsonLoadFinish = false
    private val moshi by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private val binding by lazy {
        ActivityVideoDecodeBinding.inflate(layoutInflater)
    }
    private val mediaExtractor by lazy {
        MediaExtractor()
    }

    // 影片軌道數
    private var videoLink = ""
    private var trackCount = 0
    private var surfaceView: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initMediaExtractor(item: VideoItem) {
        Log.d(TAG, ": ${VideoItemJsonAdapter(moshi).toJson(item)}")
        try {
            val extractorThread = Thread{
//                mediaExtractor.setDataSource()
//                Log.d(TAG, ": ${mediaExtractor.trackCount}")
            }
            extractorThread.start()
        } catch (exp: IOException) {
            mediaExtractor.release()
            binding.playBtn.isEnabled = true
        }
    }

    private fun initView() {
        if (surfaceView != null) binding.videoContainer.removeView(surfaceView)
        surfaceView = SurfaceView(baseContext)
        surfaceView?.holder?.addCallback(this)
        binding.videoContainer.addView(surfaceView)

        // json
        videoJson =
            resources.openRawResource(R.raw.videolist).bufferedReader().use { it.readText() }
        val videosData = VideosDataJsonAdapter(moshi).fromJson(videoJson)
        videosData?.let {
            videoList.clear()
            videoList.addAll(it.list)
            Log.d(TAG, "json: load finish")
            isJsonLoadFinish = true
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.playBtn -> {
                if (!isJsonLoadFinish) return
                binding.playBtn.isEnabled = false
                initMediaExtractor(videoList.random())
            }
            else -> throw RuntimeException("no match xml id ${view.id}")
        }
    }
}