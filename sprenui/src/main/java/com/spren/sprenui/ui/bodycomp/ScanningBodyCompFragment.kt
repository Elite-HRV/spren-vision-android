package com.spren.sprenui.ui.bodycomp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.Surface.ROTATION_90
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentScanningBodyCompBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanningBodyCompFragment : Fragment() {

    private var _binding: FragmentScanningBodyCompBinding? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var height = 0
    private var width = 0
    private var flashEnabled = false
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var timerShown = false
    private var timerSelected = TimerSelected.OFF
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var poseDetector: PoseDetector

    private val binding get() = _binding!!

    private enum class TimerSelected {
        OFF, ON_5S, ON_10S
    }

    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, 90)
                poseDetector.process(image)
                    .addOnSuccessListener { results ->
                        // Ankles
                        val rightAnkle = results.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
                        val leftAnkle = results.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
                        val proportion = height.toDouble() / imageProxy.width
                        val offset = (imageProxy.height * proportion - width) / 2
                        if (rightAnkle != null && leftAnkle != null) {
                            val xImageTranslatedRightAnkle =
                                rightAnkle.position.x * proportion - offset
                            val yImageTranslatedRightAnkle = rightAnkle.position.y * proportion
                            val xImageTranslatedLeftAnkle =
                                leftAnkle.position.x * proportion - offset
                            val yImageTranslatedLeftAnkle = leftAnkle.position.y * proportion
                            val bottom =
                                if (lensFacing == CameraSelector.LENS_FACING_FRONT) height - binding.feetFrame.top else binding.feetFrame.bottom
                            val top =
                                if (lensFacing == CameraSelector.LENS_FACING_FRONT) height - binding.feetFrame.bottom else binding.feetFrame.top
                            if (xImageTranslatedRightAnkle >= binding.feetFrame.left && xImageTranslatedLeftAnkle <= binding.feetFrame.right &&
                                yImageTranslatedRightAnkle >= top && yImageTranslatedRightAnkle <= bottom &&
                                yImageTranslatedLeftAnkle >= top && yImageTranslatedLeftAnkle <= bottom
                            ) {
                                binding.feetFrame.setImageResource(R.drawable.ic_feet_frame_green)
                                binding.leftFootPositionMark.setImageResource(R.drawable.ic_foot_position_mark_green)
                                binding.rightFootPositionMark.setImageResource(R.drawable.ic_foot_position_mark_green)
                            } else {
                                binding.feetFrame.setImageResource(R.drawable.ic_feet_frame)
                                binding.leftFootPositionMark.setImageResource(R.drawable.ic_foot_position_mark)
                                binding.rightFootPositionMark.setImageResource(R.drawable.ic_foot_position_mark)
                            }
                        } else {
                            binding.feetFrame.setImageResource(R.drawable.ic_feet_frame)
                            binding.leftFootPositionMark.setImageResource(R.drawable.ic_foot_position_mark)
                            binding.rightFootPositionMark.setImageResource(R.drawable.ic_foot_position_mark)
                        }
                    }
                    .addOnFailureListener { }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanningBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjust resolution of preview an analysis to match
        getHeightAndWidth()

        startCamera()

        binding.switchImage.setColorFilter(requireContext().getColor(R.color.progress_red))

        binding.switchImage.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                binding.switchImage.setColorFilter(requireContext().getColor(R.color.progress_red))
                CameraSelector.LENS_FACING_FRONT
            } else {
                binding.switchImage.setColorFilter(requireContext().getColor(R.color.white))
                CameraSelector.LENS_FACING_BACK
            }
            startCamera()
        }

        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                camera?.let {
                    it.cameraInfo.zoomState.value?.let { zoomState ->
                        val scale = zoomState.zoomRatio * detector.scaleFactor
                        it.cameraControl.setZoomRatio(scale)
                        return true
                    }
                }
                return false
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)
        binding.viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }

        var takingPicture = false
        var timer: CountDownTimer? = null
        binding.cameraImage.setOnClickListener {
            if (takingPicture) {
                timer?.cancel()
                binding.timerText.text = ""
                binding.cameraImage.setImageResource(R.drawable.ic_camera_body_comp)
                takingPicture = false
            } else {
                binding.timerImage.setColorFilter(requireContext().getColor(R.color.white))
                binding.timerContainer.visibility = View.GONE
                timerShown = false
                if (timerSelected == TimerSelected.OFF) {
                    takePhoto()
                } else {
                    takingPicture = true
                    binding.cameraImage.setImageResource(R.drawable.ic_camera_body_comp_2)
                    val timerMillis = if (timerSelected == TimerSelected.ON_5S) 5000L else 10000L
                    binding.timerText.text = (timerMillis / 1000).toString()
                    timer = object : CountDownTimer(timerMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val seconds = (millisUntilFinished / 1000)
                            if (seconds != 0L) {
                                binding.timerText.text = seconds.toString()
                            }
                        }

                        override fun onFinish() {
                            binding.timerText.text = ""
                            binding.cameraImage.setImageResource(R.drawable.ic_camera_body_comp)
                            takePhoto()
                            takingPicture = false
                        }
                    }
                    timer?.start()
                }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(options)

        binding.flashImage.setOnClickListener {
            camera?.let {
                if (it.cameraInfo.hasFlashUnit()) {
                    flashEnabled = !flashEnabled
                    it.cameraControl.enableTorch(flashEnabled)
                    if (flashEnabled) {
                        binding.flashImage.setImageResource(R.drawable.ic_flash_on)
                    } else {
                        binding.flashImage.setImageResource(R.drawable.ic_flash_off)
                    }
                }
            }
        }

        setUpTimerSelectedColor()

        binding.timerImage.setOnClickListener {
            if (!timerShown) {
                binding.timerImage.setColorFilter(requireContext().getColor(R.color.progress_red))
                binding.timerContainer.visibility = View.VISIBLE
            } else {
                binding.timerImage.setColorFilter(requireContext().getColor(R.color.white))
                binding.timerContainer.visibility = View.GONE
            }
            timerShown = !timerShown
        }

        binding.timerOffText.setOnClickListener {
            timerSelected = TimerSelected.OFF
            setUpTimerSelectedColor()
        }

        binding.timer5sText.setOnClickListener {
            timerSelected = TimerSelected.ON_5S
            setUpTimerSelectedColor()
        }

        binding.timer10sText.setOnClickListener {
            timerSelected = TimerSelected.ON_10S
            setUpTimerSelectedColor()
        }

        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ScanningBodyCompFragment_to_GreetingBodyCompFragment)
        }

        val dialog = Dialog(requireContext(), R.style.Dialog)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.guided_indicator_layout)
        dialog.show()

        val guidedIndicatorTimer = object : CountDownTimer(5000, 5000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                dialog.dismiss()
            }
        }
        guidedIndicatorTimer.start()
    }

    private fun setUpTimerSelectedColor() {
        when (timerSelected) {
            TimerSelected.OFF -> {
                binding.timerOffText.setTextColor(
                    requireContext().getColor(
                        R.color.progress_red
                    )
                )
                binding.timer5sText.setTextColor(
                    requireContext().getColor(
                        R.color.white
                    )
                )
                binding.timer10sText.setTextColor(
                    requireContext().getColor(
                        R.color.white
                    )
                )
            }
            TimerSelected.ON_5S -> {
                binding.timer5sText.setTextColor(
                    requireContext().getColor(
                        R.color.progress_red
                    )
                )
                binding.timer10sText.setTextColor(
                    requireContext().getColor(
                        R.color.white
                    )
                )
                binding.timerOffText.setTextColor(
                    requireContext().getColor(
                        R.color.white
                    )
                )
            }
            TimerSelected.ON_10S -> {
                binding.timer10sText.setTextColor(
                    requireContext().getColor(
                        R.color.progress_red
                    )
                )
                binding.timer5sText.setTextColor(
                    requireContext().getColor(
                        R.color.white
                    )
                )
                binding.timerOffText.setTextColor(
                    requireContext().getColor(
                        R.color.white
                    )
                )
            }
        }
    }

    private fun getHeightAndWidth() {
        // Getting screen resolution
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
            // Gets all excluding insets
            // Gets all excluding insets
            val windowInsets = metrics.windowInsets
            val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars()
                        or WindowInsets.Type.displayCutout()
            )

            val insetsHeight: Int = insets.top + insets.bottom
            val insetsWidth: Int = insets.left + insets.right

            // Legacy size that Display#getSize reports

            // Legacy size that Display#getSize reports
            val bounds: Rect = metrics.bounds
            height = bounds.height() - insetsHeight
            width = bounds.width() - insetsWidth
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILE_NAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SprenBodyComp")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this.requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(this::class.simpleName, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val image = output.savedUri
                    val msg = "Photo capture succeeded: $image"
                    Log.d(this@ScanningBodyCompFragment::class.simpleName, msg)
                    // Redirect to Confirmation Screen
                    image?.let {
                        poseDetector.close()
                        val direction =
                            ScanningBodyCompFragmentDirections.actionScanningBodyCompFragmentToConfirmBodyCompFragment(
                                image.toString()
                            )
                        findNavController().navigate(direction)
                    } ?: run {
                        Log.e(this::class.simpleName, "Photo capture failed due to unknown reason")
                    }
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Capture
            imageCapture = ImageCapture.Builder()
                .build()

            // Analysis
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetRotation(ROTATION_90)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }

            // Select back camera as a default
            val cameraSelector =
                if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(this::class.simpleName, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        poseDetector.close()
        // If next line is uncommented - getting Null pointer exception
        // when navigate back from Server Error Fragment
        // _binding = null
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}