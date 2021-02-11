package com.example.barshiftlineview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val colors : Array<Int> = arrayOf(
    "#F44336",
    "#2196F3",
    "#8BC34A",
    "#673AB7",
    "#009688"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 3
val scGap : Float = 0.02f / parts
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val strokeFactor : Float = 90f
val barHFactor : Float = 11.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBarShiftLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val barH : Float = Math.min(w, h) / barHFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    save()
    translate(w / 2, h / 2)
    for (j in 0..1) {
        save()
        scale(1f, 1f - 2 * j)
        drawLine(w * 0.5f * sf3, 0f, w * 0.5f * sf3, h * sf1 * 0.5f, paint)
        drawRect(RectF(0f, (h - barH), w * 0.5f * (sf2 - sf3), barH), paint)
        drawRect(RectF(0f, 0f, w * 0.5f * sf3, h / 2), paint)
        restore()
    }
    restore()
}

fun Canvas.drawBSLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBarShiftLine(scale, w, h, paint)
}

class BarShiftLineView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}