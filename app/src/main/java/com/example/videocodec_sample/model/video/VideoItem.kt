package com.example.videocodec_sample.model.video

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoItem(
    val description: String = "",
    val sources: String = "",
    val subtitle: String = "",
    val thumb: String = "",
    val title: String = ""
)
