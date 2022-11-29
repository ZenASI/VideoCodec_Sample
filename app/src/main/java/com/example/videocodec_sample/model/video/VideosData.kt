package com.example.videocodec_sample.model.video

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideosData(
    @Json(name = "videos")
    val list: List<VideoItem>
)
