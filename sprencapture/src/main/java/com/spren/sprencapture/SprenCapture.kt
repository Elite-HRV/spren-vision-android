package com.spren.sprencapture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.os.Build
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.Surface.ROTATION_90
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.spren.sprencore.Spren
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class SprenCapture(
    private val activity: Activity,
    private val surfaceProvider: Preview.SurfaceProvider
) {

    private val primaryWidth: Int
    private val primaryHeight: Int
    private val secondaryWidth: Int
    private val secondaryHeight: Int
    private var width: Int
    private var height: Int
    private var frameRate: Int = 0
    private var cameraId: String = "0"
    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var torchMode: Boolean = true
    private var isOverExposed: Boolean = false
    private var exposurePercentage: Double = DEFAULT_EXPOSURE_PERCENTAGE
    private var exposureWasChanged: Boolean = false

    companion object {
        private const val TAG = "SprenCapture"
        private const val IMAGE_QUEUE_DEPTH = 30
        private const val DEFAULT_EXPOSURE_PERCENTAGE = 0.002
        private const val SECONDARY_EXPOSURE_PERCENTAGE = 0.02
        private const val ISO_PERCENTAGE = 27.7
    }

    init {
        if (activity !is LifecycleOwner) {
            throw RuntimeException("Activity does not implement getLifecycle() method")
        }

        val cameraManager =
            activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val map: StreamConfigurationMap =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
        val checkSizesAvailable = map.getOutputSizes(ImageFormat.YUV_420_888)
        val checkSizesAvailableOrdered = checkSizesAvailable.sortedByDescending { it.width }
        primaryWidth = checkSizesAvailableOrdered[checkSizesAvailableOrdered.size - 2].width
        primaryHeight = checkSizesAvailableOrdered[checkSizesAvailableOrdered.size - 2].height
        secondaryWidth = checkSizesAvailableOrdered[checkSizesAvailableOrdered.size - 1].width
        secondaryHeight = checkSizesAvailableOrdered[checkSizesAvailableOrdered.size - 1].height
        width = primaryWidth
        height = primaryHeight
    }

    private inner class ImageAnalyzer :
        ImageAnalysis.Analyzer {

        private fun Image.toBitmap(): Bitmap {
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride: Int = planes[0].pixelStride
            val rowStride: Int = planes[0].rowStride
            val rowPadding: Int = rowStride - pixelStride * width
            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height, Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            return bitmap
        }

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            if (imageProxy.format == PixelFormat.RGBA_8888 && imageProxy.image != null) {
                val bitmap = imageProxy.image!!.toBitmap()
                Spren.process(bitmap, imageProxy.imageInfo.timestamp)
            }
            imageProxy.close()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera(
    ): Boolean {
        val bestCameraValues = getBestCameraValues()
        if (frameRate >= 30) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(surfaceProvider)
                    }

                val imageAnalyzer = buildImageAnalysis(bestCameraValues.second)

                val cameraSelector = CameraSelector.Builder()
                    .addCameraFilter { cameraInfoList ->
                        cameraInfoList.filter { Camera2CameraInfo.from(it).cameraId == bestCameraValues.first }
                    }.build()

                try {
                    // Unbind use cases before rebinding
                    cameraProvider?.unbindAll()

                    // Bind use cases to camera
                    camera = cameraProvider?.bindToLifecycle(
                        activity as LifecycleOwner, cameraSelector, preview, imageAnalyzer
                    )

                    camera?.let {
                        if (it.cameraInfo.hasFlashUnit()) {
                            it.cameraControl.enableTorch(torchMode)
                        }
                    }

                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(activity))
            cameraExecutor = Executors.newSingleThreadExecutor()
            return true
        } else {
            return false
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun buildImageAnalysis(highestAvailableFPS: Range<Int>): ImageAnalysis {
        val builder = ImageAnalysis.Builder()
            .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setTargetResolution(Size(width, height))
            .setTargetRotation(ROTATION_90)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .setImageQueueDepth(IMAGE_QUEUE_DEPTH)

        val camera2InterOp = Camera2Interop.Extender(builder)
            .setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                highestAvailableFPS
            )

        if (isOverExposed && exposurePercentage >= 0.0) {
            val cameraManager =
                activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val exposureTimeRange =
                characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
            val exposureTime: Double =
                (exposurePercentage * (exposureTimeRange!!.upper - exposureTimeRange.lower) / 100) + exposureTimeRange.lower
            val isoRange = characteristics.get(
                CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE
            )
            val iso: Double =
                (ISO_PERCENTAGE * (isoRange!!.upper - isoRange.lower) / 100) + isoRange.lower
            camera2InterOp.setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_OFF
            ).setCaptureRequestOption(
                CaptureRequest.SENSOR_EXPOSURE_TIME,
                exposureTime.toLong()
            ).setCaptureRequestOption(
                CaptureRequest.SENSOR_SENSITIVITY, iso.toInt()
            ).setCaptureRequestOption(
                CaptureRequest.SENSOR_FRAME_DURATION,
                (1000 * 1000 * 1000 / frameRate).toLong()
            )
            isOverExposed = false
            exposurePercentage -= if (torchMode) 0.001 else 0.01
            exposureWasChanged = true
        }

        return builder
            .build()
            .also {
                it.setAnalyzer(cameraExecutor!!, ImageAnalyzer())
            }
    }

    private fun getBestCameraValues(): Pair<String, Range<Int>> {
        val cameraManager =
            activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var highestAvailableFps = Range(0, 0)
        var bestCameraId = "0"
        val sortedHwLevels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            arrayOf(
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
            )
        } else {
            arrayOf(
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
            )
        }
        var bestHardwareLevelIndex = 0
        if (frameRate > 0) {
            highestAvailableFps = Range(frameRate, frameRate)
        } else {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val fpsRanges =
                    characteristics[CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES]
                fpsRanges?.let {
                    for (range in it) {
                        if (range.lower > highestAvailableFps.lower && range.upper > highestAvailableFps.upper && characteristics.get(
                                CameraCharacteristics.LENS_FACING
                            ) == CameraCharacteristics.LENS_FACING_BACK && findIndex(
                                sortedHwLevels,
                                characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) as Int
                            ) >= bestHardwareLevelIndex
                        ) {
                            highestAvailableFps = range
                            bestHardwareLevelIndex = findIndex(
                                sortedHwLevels,
                                characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) as Int
                            )
                            bestCameraId = cameraId
                        }
                    }
                }
            }
            frameRate =
                if (highestAvailableFps.upper >= highestAvailableFps.lower) highestAvailableFps.upper else highestAvailableFps.lower
            this.cameraId = bestCameraId
        }
        return Pair(cameraId, highestAvailableFps)
    }

    private fun findIndex(arr: Array<Int>, item: Int) = arr.indexOf(item)

    // MARK: - public API

    // It returns false when the camera does not support at least 30 FPS
    fun start() = startCamera()

    fun stop() {
        activity.runOnUiThread { cameraProvider?.unbindAll() }
        cameraExecutor?.shutdown()
    }

    fun setTorchMode(torch: Boolean): Boolean {
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                torchMode = torch
                it.cameraControl.enableTorch(torch)
                exposurePercentage =
                    if (torch) DEFAULT_EXPOSURE_PERCENTAGE else SECONDARY_EXPOSURE_PERCENTAGE
                if (exposureWasChanged) {
                    exposureWasChanged = false
                    stop()
                    activity.runOnUiThread { start() }
                }
                return torch
            }
        }
        torchMode = false
        return false
    }

    private fun dropFrameRate(): Boolean {
        return when (frameRate) {
            120 -> {
                frameRate = 60
                stop()
                activity.runOnUiThread { start() }
                true
            }
            60 -> {
                frameRate = 30
                stop()
                activity.runOnUiThread { start() }
                true
            }
            else -> {
                false
            }
        }
    }

    private fun dropResolution(): Boolean {
        return if (width == primaryWidth && height == primaryHeight) {
            width = secondaryWidth
            height = secondaryHeight
            stop()
            activity.runOnUiThread { start() }
            true
        } else
            false
    }


    fun dropComplexity(): Boolean {
        val resolution = width * height
        print("dropping complexity - ${frameRate}fps, ${resolution}px")

        if (dropResolution()) {
            print("dropped resolution - ${frameRate}fps, ${resolution}px")
            return true
        }

        if (dropFrameRate()) {
            print("dropped framerate - ${frameRate}fps, ${resolution}px")
            return true
        }

        return false
    }

    fun handleOverExposure() {
        isOverExposed = true
        stop()
        activity.runOnUiThread { start() }
    }

}