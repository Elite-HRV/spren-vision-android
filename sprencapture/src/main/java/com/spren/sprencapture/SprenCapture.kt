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
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.spren.sprencore.FrameInformation
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
    private var defaultExposure: Double = 0.0
    private var currentExposure: Double = 0.0
    private val phoneModelWithDifferentDefaultExposure = arrayOf("SM-G950", "SM-G955")
    private val phoneModelWithDifferentDefaultLens = arrayOf(
        "Pixel 6",
        "Pixel 7",
        "moto g(8) power",
        "SM-N950U"
    )

    companion object {
        private const val TAG = "SprenCapture"
        private const val IMAGE_QUEUE_DEPTH = 30
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
            .sortedByDescending { it.width }
        width = checkSizesAvailableOrdered.last().width
        height = checkSizesAvailableOrdered.last().height

        Spren.init(activity)
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
                Spren.process(
                    bitmap,
                    FrameInformation(
                        imageProxy.width,
                        imageProxy.height,
                        imageProxy.imageInfo.timestamp
                    )
                )
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

                val cameraSelector = CameraSelector.Builder()
                    .addCameraFilter { cameraInfoList ->
                        cameraInfoList.filter { Camera2CameraInfo.from(it).cameraId == bestCameraValues.first }
                    }.build()

                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()

                // Binding to set capture request options
                camera =
                    cameraProvider?.bindToLifecycle(activity as LifecycleOwner, cameraSelector)

                val imageAnalyzer = buildImageAnalysis(bestCameraValues.second)

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

        camera?.let {
            val cameraRequestBuilder = CaptureRequestOptions.Builder()

            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                highestAvailableFPS
            )

            cameraRequestBuilder.setCaptureRequestOption(
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
                cameraRequestBuilder.setCaptureRequestOption(
                    CaptureRequest.LENS_APERTURE,
                    aperturesAvailable.minOrNull() ?: 0f
                )
            }

            // ISO (min)
            val sensorSensitivityRange =
                characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
            if (sensorSensitivityRange != null) {
                cameraRequestBuilder.setCaptureRequestOption(
                    CaptureRequest.SENSOR_SENSITIVITY,
                    sensorSensitivityRange.lower
                )
            }

            // FRAME_DURATION >= EXPOSURE_TIME
            val frameDuration = (1000 * 1000 * 1000 / frameRate).toLong()
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.SENSOR_FRAME_DURATION,
                frameDuration
            )

            // Exposure (max or readjusted when overexposed)
            val exposureTimeRange =
                characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
            if (exposureTimeRange != null && currentExposure >= exposureTimeRange.lower && currentExposure <= exposureTimeRange.upper) {
                cameraRequestBuilder.setCaptureRequestOption(
                    CaptureRequest.SENSOR_EXPOSURE_TIME,
                    currentExposure.toLong()
                )
            }

            // Minimum focus (the number is max)
            val minimumFocusDistance =
                characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.LENS_FOCUS_DISTANCE,
                minimumFocusDistance ?: 0f
            )

            // Default FAST
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.COLOR_CORRECTION_MODE,
                CameraCharacteristics.COLOR_CORRECTION_MODE_FAST
            )

            // Default 60Hz
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_ANTIBANDING_MODE,
                CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_OFF
            )

            // Default false. Starts with default manual exposure time, with black level being locked
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.BLACK_LEVEL_LOCK,
                false
            )

            // Default FAST
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE,
                CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_OFF
            )

            // Default FACE_PRIORITY
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.CONTROL_SCENE_MODE,
                CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && phoneModelWithDifferentDefaultLens.any { phoneModel ->
                    Build.MODEL.startsWith(
                        phoneModel,
                        true
                    )
                }) {
                try {
                    // Zoom API 30+
                    val zoomRanges =
                        characteristics.get(CameraCharacteristics.CONTROL_ZOOM_RATIO_RANGE)
                    if (zoomRanges != null) {
                        cameraRequestBuilder.setCaptureRequestOption(
                            CaptureRequest.CONTROL_ZOOM_RATIO,
                            zoomRanges.lower
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("Zoom", "Exception")
                }
            }

            try {
                // Focal Length API 21+
                val focalLength =
                    characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                if (focalLength != null && focalLength.isNotEmpty()) {
                    cameraRequestBuilder.setCaptureRequestOption(
                        CaptureRequest.LENS_FOCAL_LENGTH,
                        focalLength[0]
                    )
                }

            } catch (e: Throwable) {
                Log.d("Focal length", "Exception")
            }

            // Default ON
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
                CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_OFF
            )

            // Default FAST
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.NOISE_REDUCTION_MODE,
                CameraCharacteristics.NOISE_REDUCTION_MODE_OFF
            )

            // Default FAST
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.SHADING_MODE,
                CameraCharacteristics.SHADING_MODE_OFF
            )

            // Default TONEMAP_MODE_FAST
            cameraRequestBuilder.setCaptureRequestOption(
                CaptureRequest.TONEMAP_MODE,
                CameraCharacteristics.TONEMAP_MODE_FAST
            )
            Camera2CameraControl.from(it.cameraControl)
                .setCaptureRequestOptions(cameraRequestBuilder.build())
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
        val sortedHwLevels =
            arrayOf(
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
            )
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
            defaultExposure =
                if (phoneModelWithDifferentDefaultExposure.any { Build.MODEL.startsWith(it, true) })
                    16000000.0
                else
                    1000 * 1000 * 1000 / frameRate.toDouble()
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

    @SuppressLint("UnsafeOptInUsageError")
    private fun handleOverExposure() {
        camera?.let {
            val frameDuration = (1000 * 1000 * 1000 / frameRate).toLong()
            // If 60 FPS -> reduce exposure by 1.5m
            // If 30 FPS -> reduce exposure by 3m
            currentExposure -= frameDuration * 0.09
            Camera2CameraControl.from(it.cameraControl).addCaptureRequestOptions(
                CaptureRequestOptions.Builder()
                    .setCaptureRequestOption(
                        CaptureRequest.SENSOR_EXPOSURE_TIME,
                        currentExposure.toLong()
                    )
                    .build()
            )
        }
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

    @SuppressLint("UnsafeOptInUsageError")
    fun reset() {
        Spren.autoStart = true
        camera?.let {
            currentExposure = defaultExposure
            Camera2CameraControl.from(it.cameraControl).addCaptureRequestOptions(
                CaptureRequestOptions.Builder()
                    .setCaptureRequestOption(
                        CaptureRequest.SENSOR_EXPOSURE_TIME,
                        currentExposure.toLong()
                    )
                    .build()
            )
        }
    }
}