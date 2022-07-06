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
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.spren.sprencore.Spren
import com.spren.sprencore.event.SprenEvent
import com.spren.sprencore.event.SprenEventManager
import com.spren.sprencore.finger.compliance.ComplianceCheck
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class SprenCapture(
    private val activity: Activity,
    private val surfaceProvider: Preview.SurfaceProvider
) {

    private var width: Int
    private var height: Int
    private var frameRate: Int = 0
    private var cameraId: String = "0"
    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var isOverExposed: Boolean = false
    private var defaultExposure: Double = 0.0
    private var currentExposure: Double = 0.0

    companion object {
        private const val TAG = "SprenCapture"
        private const val IMAGE_QUEUE_DEPTH = 30
        private const val MINIMUM_WIDTH = 192
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
        val checkSizesAvailableOrdered = checkSizesAvailable
            .filter { it.width.toDouble() / it.height.toDouble() == 4.0 / 3.0 && it.width >= MINIMUM_WIDTH }
            .sortedByDescending { it.width }
        width = checkSizesAvailableOrdered.last().width
        height = checkSizesAvailableOrdered.last().height
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
            SprenEventManager.unsubscribe(SprenEvent.COMPLIANCE, ::complianceListener).subscribe(
                SprenEvent.COMPLIANCE, ::complianceListener
            )

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
                            it.cameraControl.enableTorch(true)
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
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setTargetResolution(Size(width, height))
            .setTargetRotation(ROTATION_90)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .setImageQueueDepth(IMAGE_QUEUE_DEPTH)

        val camera2InterOp = Camera2Interop.Extender(builder)
            .setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                highestAvailableFPS
            )

        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.CONTROL_AWB_MODE,
            CaptureRequest.CONTROL_AWB_STATE_LOCKED
        ).setCaptureRequestOption(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_OFF
        ).setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_OFF
        )

        val cameraManager =
            activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)

        // Aperture (max as min)
        val aperturesAvailable =
            characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)
        if (aperturesAvailable != null && aperturesAvailable.isNotEmpty()) {
            camera2InterOp.setCaptureRequestOption(
                CaptureRequest.LENS_APERTURE,
                aperturesAvailable.minOrNull() ?: 0f
            )
        }

        // ISO (min)
        val sensorSensitivityRange =
            characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
        if (sensorSensitivityRange != null) {
            camera2InterOp.setCaptureRequestOption(
                CaptureRequest.SENSOR_SENSITIVITY,
                sensorSensitivityRange.lower
            )
        }

        // FRAME_DURATION >= EXPOSURE_TIME
        val frameDuration = (1000 * 1000 * 1000 / frameRate).toLong()
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.SENSOR_FRAME_DURATION,
            frameDuration
        )

        if (isOverExposed) {
            isOverExposed = false
            // If 60 FPS -> reduce exposure by 1.5m
            // If 30 FPS -> reduce exposure by 3m
            currentExposure -= frameDuration * 0.09
        }

        // Exposure (max or readjusted when overexposed)
        val exposureTimeRange =
            characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
        if (exposureTimeRange != null && currentExposure >= exposureTimeRange.lower && currentExposure <= exposureTimeRange.upper) {
            camera2InterOp.setCaptureRequestOption(
                CaptureRequest.SENSOR_EXPOSURE_TIME,
                currentExposure.toLong()
            )
        }

        // Minimum focus (the number is max)
        val minimumFocusDistance =
            characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.LENS_FOCUS_DISTANCE,
            minimumFocusDistance ?: 0f
        )

        // Default FAST
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.COLOR_CORRECTION_MODE,
            CameraCharacteristics.COLOR_CORRECTION_MODE_FAST
        )

        // Default 60Hz
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_ANTIBANDING_MODE,
            CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_OFF
        )

        // Default false. Starts with default manual exposure time, with black level being locked
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.BLACK_LEVEL_LOCK,
            false
        )

        // Default FAST
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE,
            CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_OFF
        )

        // Default FACE_PRIORITY
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.CONTROL_SCENE_MODE,
            CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED
        )

        try {
            // Zoom API 21+
            val focalLength =
                characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
            if (focalLength != null && focalLength.isNotEmpty()) {
                camera2InterOp.setCaptureRequestOption(
                    CaptureRequest.LENS_FOCAL_LENGTH,
                    focalLength[0]
                )
            }

        } catch (e: Throwable) {
            Log.d("Focal length", "Exception")
        }

        // Default ON
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
            CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_OFF
        )

        // Default FAST
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.NOISE_REDUCTION_MODE,
            CameraCharacteristics.NOISE_REDUCTION_MODE_OFF
        )

        // Default FAST
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.SHADING_MODE,
            CameraCharacteristics.SHADING_MODE_OFF
        )

        // Default TONEMAP_MODE_FAST
        camera2InterOp.setCaptureRequestOption(
            CaptureRequest.TONEMAP_MODE,
            CameraCharacteristics.TONEMAP_MODE_FAST
        )

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
            defaultExposure = 1000 * 1000 * 1000 / frameRate.toDouble()
            currentExposure = defaultExposure
        }
        return Pair(cameraId, highestAvailableFps)
    }

    private fun findIndex(arr: Array<Int>, item: Int) = arr.indexOf(item)

    private fun complianceListener(values: HashMap<String, Any>) {
        val name = values["name"] as ComplianceCheck.Name
        val compliant = values["isCompliant"] as Boolean
        if (name == ComplianceCheck.Name.EXPOSURE && !compliant) {
            handleOverExposure()
        }
    }

    private fun handleOverExposure() {
        isOverExposed = true
        stop()
        activity.runOnUiThread { start() }
    }

    // MARK: - public API

    // It returns false when the camera does not support at least 30 FPS
    fun start() = startCamera()

    fun stop() {
        activity.runOnUiThread { cameraProvider?.unbindAll() }
        cameraExecutor?.shutdown()
        SprenEventManager.unsubscribe(SprenEvent.COMPLIANCE, ::complianceListener)
    }

    fun turnFlashOn() =
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                it.cameraControl.enableTorch(true)
            }
        }

    fun reset() {
        Spren.autoStart = true
        currentExposure = defaultExposure
        stop()
        activity.runOnUiThread { start() }
    }

    @Deprecated("This API method will be removed in the next releases. Replace with turnFlashOn()")
    fun setTorchMode(torch: Boolean): Boolean {
        if (torch) {
            camera?.let {
                if (it.cameraInfo.hasFlashUnit()) {
                    it.cameraControl.enableTorch(true)
                }
            }
        }
        return true
    }

    @Deprecated("This API method will be removed in the next releases", ReplaceWith("true"))
    fun dropComplexity() = true

}