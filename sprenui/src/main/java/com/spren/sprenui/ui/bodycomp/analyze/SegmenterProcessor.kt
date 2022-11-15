package com.spren.sprenui.ui.bodycomp.analyze

import android.graphics.*
import android.util.Log
import androidx.core.graphics.ColorUtils
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


/** A processor to run Segmenter.  */
class SegmenterProcessor() {
  private val segmenter: Segmenter

  init {
    val options = SelfieSegmenterOptions.Builder()
      .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
      .build()

    segmenter = Segmentation.getClient(options)
    Log.d(TAG, "SegmenterProcessor created with option: $options")
  }

  fun process(image: InputImage): Task<SegmentationMask> {
    return segmenter.process(image)
  }

  suspend fun removeBackgroundImage(image: Bitmap, segmentationMask: SegmentationMask, colorBackground: Int) : Bitmap {
    val copy = image.copy(Bitmap.Config.ARGB_8888,true)
    val mask = segmentationMask.buffer
    val maskWidth = segmentationMask.width
    val maskHeight = segmentationMask.height
    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE

    val bitmap = CoroutineScope(Dispatchers.IO).async {
      for (y in 0 until maskHeight) {
        for (x in 0 until maskWidth) {
          val bgConfidence = mask.float
          val pixel = image.getPixel(x, y)

          if (bgConfidence < CONFIDENCE_MIN) {
            image.setPixel(x, y, colorBackground)
          } else {
            val color = ColorUtils.blendARGB(
              colorBackground,
              Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)),
              COLOR_BLEND_RATIO
            )
            image.setPixel(x, y, color)
            if (x < minX) {
              minX = x
            }
            if (y < minY) {
              minY = y
            }
            if (x > maxX) {
              maxX = x
            }
            if (y > maxY) {
              maxY = y
            }
          }
        }
      }
      mask.rewind()

      if (maxX < 0 && maxY < 0) {
        return@async copy
      }
      val width = maxX - minX
      val height = maxY - minY

      return@async Bitmap.createBitmap(image, minX, minY, width, height)
    }

    return bitmap.await()
  }

  fun onFailure(e: Exception) {
    Log.e(TAG, "Segmentation failed: $e")
  }

  companion object {
    private const val TAG = "SegmenterProcessor"
    private const val CONFIDENCE_MIN = 0.6
    private const val COLOR_BLEND_RATIO = 0.05F
  }
}
