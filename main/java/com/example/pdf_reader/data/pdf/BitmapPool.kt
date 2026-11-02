package com.example.pdf_reader.data.pdf

import android.graphics.Bitmap
import android.graphics.Color
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BitmapPool @Inject constructor() {

    private val lock = Any()
    private val pool = ArrayDeque<Bitmap>()
    private val maxSize = 4

    fun acquire(width: Int, height: Int): Bitmap {
        synchronized(lock) {
            val reusable = pool.firstOrNull { bitmap ->
                !bitmap.isRecycled && bitmap.width == width && bitmap.height == height
            }
            if (reusable != null) {
                pool.remove(reusable)
                reusable.eraseColor(Color.WHITE)
                return reusable
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        return bitmap
    }

    fun release(bitmap: Bitmap) {
        if (bitmap.isRecycled) return
        synchronized(lock) {
            if (pool.size >= maxSize) {
                bitmap.recycle()
            } else {
                pool.addLast(bitmap)
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            pool.forEach { bitmap ->
                if (!bitmap.isRecycled) bitmap.recycle()
            }
            pool.clear()
        }
    }
}
