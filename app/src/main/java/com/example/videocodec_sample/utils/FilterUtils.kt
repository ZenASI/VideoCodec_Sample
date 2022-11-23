package com.example.videocodec_sample.utils

import com.example.videocodec_sample.R
import com.example.videocodec_sample.model.FilterItem

object FilterUtils {
    fun getAllFilter() = listOf<FilterItem>(
        FilterItem(R.raw.original, "origin", 1, false),
        FilterItem(R.raw.gray, "gray", 1, false),
        FilterItem(R.raw.black_white, "blackWhite", 1, false),
        FilterItem(R.raw.pixelize, "pixelize", 1, false),
        FilterItem(R.raw.mirror, "mirror", 1, false),
        FilterItem(R.raw.triangles_mosaic, "triangles mosaic", 1, false),
        FilterItem(R.raw.triple, "triple", 1, false),
        FilterItem(R.raw.money, "money", 1, false),
        FilterItem(R.raw.ascii, "ascii", 1, false),
        FilterItem(R.raw.cartoon, "cartoon", 1, false),
        FilterItem(R.raw.reverse, "reverse", 1, false),
        FilterItem(R.raw.newspaper, "newspaper", 1, false),
        FilterItem(R.raw.crosshatch, "crosshatch", 1, false),
        FilterItem(R.raw.basic_deform, "basic deform", 1, false),
        FilterItem(R.raw.polygonization, "polygonization", 1, false),
    )
}