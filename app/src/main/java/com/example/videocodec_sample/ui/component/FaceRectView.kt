package com.example.videocodec_sample.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import java.util.*

class FaceRectView(context: Context) : View(context) {


    val paint = Paint()
    var mRect = Rect(0, 0, 0, 0)
    val rnd = Random()
    init {

        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(mRect, paint)
    }

    fun updateRect(rect: Rect) {
        paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        mRect.set(rect)
        invalidate()
    }
}