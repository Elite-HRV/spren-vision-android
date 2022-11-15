package com.spren.sprenui.ui.bodycomp

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentAnalyzingBodyCompBinding
import com.spren.sprenui.network.RetrofitHelper
import com.spren.sprenui.network.service.InsightsApi
import com.spren.sprenui.ui.bodycomp.analyze.BodyCompApiClient
import com.spren.sprenui.ui.bodycomp.analyze.SegmenterProcessor
import com.spren.sprenui.util.SharedPreferences
import com.spren.sprenui.util.Units
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnalyzingBodyCompFragment : Fragment() {

    private var _binding: FragmentAnalyzingBodyCompBinding? = null
    private val segmenterProcessor = SegmenterProcessor()
    private lateinit var imageThumbnail: Bitmap
    private lateinit var imageUri: Uri
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzingBodyCompBinding.inflate(inflater, container, false)
        setFragmentResultListener("requestKey") { _, bundle ->
            imageThumbnail = bundle.get("imageThumbnail") as Bitmap
            imageUri = bundle.get("imageUri") as Uri

            binding.bg.clipToOutline = true
            val lineHeight = Units.pxFromDp(context, 4)
            processInputImage(imageThumbnail)
                .addOnSuccessListener { segmentationMask ->
                    CoroutineScope(Dispatchers.IO).launch {

                        val bitmap = segmenterProcessor.removeBackgroundImage(
                            imageThumbnail,
                            segmentationMask,
                            if (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) Color.rgb(0, 0, 0)
                            else Color.rgb(232, 232, 232)
                        )
                        activity?.runOnUiThread {
                            // Background
                            binding.image.setImageBitmap(bitmap)
                            // Animation
                            val animationBackgroundHeight = binding.animationBg.height
                            val layoutParams =
                                RelativeLayout.LayoutParams(binding.image.width, lineHeight)
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            binding.animationBg.layoutParams = layoutParams
                            binding.animationBg.visibility = View.VISIBLE
                            animate(binding.animationBg, animationBackgroundHeight, lineHeight)
                            // Progress Bar
                            binding.circularProgressIndicator.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { e ->
                    segmenterProcessor.onFailure(e)
                }

            // Upload image
            CoroutineScope(Dispatchers.IO).launch {
                BodyCompApiClient.upload(
                    RetrofitHelper.getInstance().create(InsightsApi::class.java),
                    imageUri,
                    requireContext().contentResolver,
                    SharedPreferences.getSharedPreferences(activity)
                ).addOnSuccessListener {
                    binding.uploadDoneIcon.visibility = View.VISIBLE
                    binding.uploadProgressCircular.visibility = View.GONE
                    CoroutineScope(Dispatchers.IO).launch(context = Dispatchers.Main) {
                        delay(1000)
                        // Save Result
                        val sharedPref = SharedPreferences.getSharedPreferences(activity)
                        with(sharedPref.edit()) {
                            val result = Gson().toJson(it)
                            putString(ResultBodyCompFragment.SCAN_RESULT_KEY, result)
                            apply()
                        }
                        findNavController().navigate(R.id.action_AnalyzingBodyCompFragment_to_ResultBodyCompFragment)
                    }
                }
                    .addOnFailureListener {
                        when (it.message) {
                            BodyCompApiClient.ERROR_SERVER_ERROR -> findNavController().navigate(R.id.action_AnalyzingBodyCompFragment_to_ServerSideErrorBodyCompFragment)
                            BodyCompApiClient.ERROR_INTERNET_CONNECTION_ERROR -> showNetworkConnectionErrorDialog()
                            else -> findNavController().navigate(R.id.action_AnalyzingBodyCompFragment_to_BodyPositionErrorBodyCompFragment)
                        }
                    }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun processInputImage(bitmap: Bitmap): Task<SegmentationMask> {
        val inputImage = getInputImage(bitmap)
        return segmenterProcessor.process(inputImage)
    }

    private fun getInputImage(bitmap: Bitmap): InputImage {
        return InputImage.fromBitmap(bitmap, 0)
    }

    private fun showNetworkConnectionErrorDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Network Connection Error")
        builder.setMessage("Please check your internet connection and try again.")

        builder.setPositiveButton("Try again") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_AnalyzingBodyCompFragment_to_ScanningBodyCompFragment)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            findNavController().popBackStack(R.id.GreetingBodyCompFragment, false)
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val TAG = "AnalyzingBodyCompFragment"
        private const val ANIMATION_DURATION = 2000L

        private fun animate(view: View, newHeight: Int, zeroHeight: Int) {
            val listener: ((animation: ValueAnimator) -> Unit) = { animation: ValueAnimator ->
                val value = animation.animatedValue as Int
                view.layoutParams.height = value
                view.requestLayout()
            }
            val animationUp = ValueAnimator
                .ofInt(zeroHeight, newHeight)
                .setDuration(ANIMATION_DURATION)
            val animationDown = ValueAnimator
                .ofInt(newHeight, zeroHeight)
                .setDuration(ANIMATION_DURATION)

            animationUp.addUpdateListener(listener)
            animationDown.addUpdateListener(listener)

            val animationSet = AnimatorSet()
            animationSet.interpolator = AccelerateDecelerateInterpolator()
            animationSet.playSequentially(animationUp, animationDown)
            animationSet.start()
            animationSet.doOnEnd {
                animationSet.start()
            }
        }
    }
}