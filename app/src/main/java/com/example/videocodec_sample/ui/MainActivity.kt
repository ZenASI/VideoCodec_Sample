package com.example.videocodec_sample.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.videocodec_sample.R
import com.example.videocodec_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun onClick(view: View) {
        var intent: Intent? = null
        when (view.id) {
            R.id.CameraPreviewXBasicBtn -> intent = Intent(this, CameraXPreviewBasic::class.java)
            R.id.CameraPreviewBtn -> intent = Intent(this, CameraPreview::class.java)
            R.id.AACRecordBtn -> intent = Intent(this, AACRecord::class.java)
            R.id.VideoDecodeBtn -> intent = Intent(this, VideoDecode::class.java)
            R.id.VideoEncodeBtn -> intent = Intent(this, VideoEncode::class.java)
        }
        startActivity(intent)
    }
}