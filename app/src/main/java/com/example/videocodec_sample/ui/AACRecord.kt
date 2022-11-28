package com.example.videocodec_sample.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.videocodec_sample.R
import com.example.videocodec_sample.databinding.ActivityAacrecordBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AACRecord : BaseActivity() {

    private val TAG = this::class.java.simpleName
    private val binding by lazy {
        ActivityAacrecordBinding.inflate(layoutInflater)
    }
    private val PCM_NAME = "${TAG.lowercase(Locale.ROOT)}.pcm"

    // play pcm
    private val audioTrack by lazy {
        AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(44100)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            AudioTrack.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ),
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }
    private lateinit var playBuffer: ByteArray

    private lateinit var audioRecord: AudioRecord
    private lateinit var recordBuffer: ByteArray
    private lateinit var ouput: FileOutputStream
    private lateinit var input: FileInputStream

    private var recordThread: Thread? = null
    private var playThread: Thread? = null

    private val rotationThread by lazy {
        Executors.newScheduledThreadPool(1)
    }

    // state
    private var isRecording = false
    private var isPlaying = false

    private val executor by lazy {
        ContextCompat.getMainExecutor(baseContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecord()
        initTrack()
        initRotation()
        initListener()
    }

    private fun initListener() {

    }

    private fun initTrack() {
        // init bufferSize
        playBuffer = ByteArray(
            AudioTrack.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        )
    }

    private fun initRotation() {
        rotationThread.scheduleAtFixedRate({
            binding.root.post {
                binding.sharkIcon.rotation = binding.sharkIcon.rotation + 6f
            }
        }, 1, 1, TimeUnit.SECONDS)
    }

    private fun initRecord() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            throw RuntimeException("Record Permission not allow!")
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        )
        // recordBuffer
        recordBuffer = ByteArray(
            AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        )
    }

    private fun startRecord() {
        recordThread = Thread {
            // file check
            val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), PCM_NAME)
            if (!file.mkdirs()) {

            }
            if (file.exists()) {
                file.delete()
            }
            // state start
            audioRecord?.startRecording()
            isRecording = true
            // prepare fos
            try {
                ouput = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            // loop start
            while (isRecording) {
                val read = audioRecord.read(recordBuffer, 0, recordBuffer.size)
                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    try {
                        // write data
                        ouput.write(recordBuffer)
                        Log.d(TAG, "recordRunnable: write data")
                    } catch (e: IOException) {
                        Log.e(TAG, "recordRunnable: ", e)
                        e.printStackTrace()
                    }
                }
            }
            // close fos
            try {
                ouput.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        recordThread?.start()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.recordStartBtn -> {
                Log.d(TAG, "onClick: recordStartBtn")
                binding.recordStartBtn.isEnabled = false
                binding.recordStopBtn.isEnabled = true
                binding.recordPlayBtn.isEnabled = false
                startRecord()
            }
            R.id.recordStopBtn -> {
                Log.d(TAG, "onClick: recordStopBtn")
                stopRecord()
                binding.recordStartBtn.isEnabled = true
                binding.recordStopBtn.isEnabled = false
                binding.recordPlayBtn.isEnabled = true
            }
            R.id.recordPlayBtn -> {
                Log.d(TAG, "onClick: recordPlayBtn")
                binding.recordStartBtn.isEnabled = false
                binding.recordStopBtn.isEnabled = false
                binding.recordPlayBtn.isEnabled = false
                playPcmFile()
            }
            else -> {
                throw RuntimeException("no match xml id ${view.id}")
            }
        }
    }

    private fun playPcmFile() {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), PCM_NAME)
        playThread = Thread {
            try {
                // change play state
                audioTrack.play()
                isPlaying = true
                // prepare fis
                input = FileInputStream(file)
                // play loop
                while (input.available() > 0) {
                    val readCount: Int = input.read(playBuffer)
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                        readCount == AudioTrack.ERROR_BAD_VALUE
                    ) {
                        continue
                    }
                    if (readCount != 0 && readCount != -1 && isPlaying) {
                        Log.d(TAG, "playPcmFile: call")
                        audioTrack.write(playBuffer, 0, readCount)
                    }
                }
                stopPlay()
                executor.execute {
                    Log.d(TAG, "playPcmFile: finish")
                    binding.recordStartBtn.isEnabled = true
                    binding.recordPlayBtn.isEnabled = true
                }
            } catch (exp: Exception) {
                Log.e(TAG, "playPcmFile: ", exp)
            }
        }
        playThread?.start()
    }

    private fun stopRecord() {
        Log.d(TAG, "stopRecord: call")
        isRecording = false
        audioRecord?.stop()
    }

    private fun stopPlay() {
        Log.d(TAG, "stopPlay: call")
        isPlaying = false
        audioTrack?.stop()
    }

    override fun onStop() {
        try {
            if (isRecording) stopRecord()
            if (isPlaying) stopPlay()
        } catch (e: Exception) {
            Log.e(TAG, "onStop: ", e)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: audioRecord release")
        audioRecord?.release()
        audioTrack?.release()
        super.onDestroy()
    }
}