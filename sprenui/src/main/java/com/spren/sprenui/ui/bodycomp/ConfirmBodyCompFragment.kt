package com.spren.sprenui.ui.bodycomp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentConfirmationBodyCompBinding
import com.spren.sprenui.util.Image
import com.spren.sprenui.util.SharedPreferences
import com.spren.sprenui.util.debounce

class ConfirmBodyCompFragment : Fragment(), ConfirmBodyCompDialogCallbackListener {

    private var _binding: FragmentConfirmationBodyCompBinding? = null

    private val binding get() = _binding!!
    private lateinit var inputParam: Pair<Bitmap, Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConfirmationBodyCompBinding.inflate(inflater, container, false)

        inputParam = getInputArgs()

        // Image
        binding.image.clipToOutline = true
        binding.image.setImageBitmap(inputParam.first)
        binding.image.visibility = View.VISIBLE

        // Progress Bar
        binding.circularProgressIndicator.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set default values
        setDefaultValues()

        // Bind action listeners
        setActionListeners(view)
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        val weight = sharedPref.getFloat(WeightBodyCompFragment.WEIGHT, 0F)
        val weightMetric = sharedPref.getString(WeightBodyCompFragment.WEIGHT_METRIC, null)
        val age = sharedPref.getInt(AgeBodyCompFragment.AGE, 0)
        val activityLevel = sharedPref.getString(ActivityLevelBodyCompFragment.ACTIVITY_LEVEL, null)
        val heightMetric = sharedPref.getString(HeightBodyCompFragment.HEIGHT_METRIC, null)
        val heightFT = sharedPref.getInt(HeightBodyCompFragment.HEIGHT_FT, 0)
        val heightIN = sharedPref.getInt(HeightBodyCompFragment.HEIGHT_IN, 0)
        val heightCM = sharedPref.getFloat(HeightBodyCompFragment.HEIGHT_CM, 0F)
        val gender = sharedPref.getString(GenderBodyCompFragment.GENDER, null)

        if (weight <= 0
            || weightMetric == null
            || age <= 0
            || activityLevel == null
            || heightMetric == null
            || (heightFT <= 0 && heightIN <= 0 && heightCM <= 0)
            || gender == null
        ) {
            Log.w(this::class.simpleName, "Not set all values weight: ${weight}, weightMetric: ${weightMetric}, ")
            return
        }

        binding.weightTextView.text = String.format("%.2f %s", weight, weightMetric)
        binding.ageTextView.text = age.toString()
        binding.activityLevelTextView.text = String.format("%s days per week", activityLevel)
        val heightViewText =
            if (heightMetric == HeightBodyCompFragment.HEIGHT_METRIC_FT)
                String.format("%d feet %d inches", heightFT, heightIN)
            else String.format("%.2f cm", heightCM)
        binding.heightTextView.text = heightViewText
        binding.genderTextView.text = gender
    }

    private fun setActionListeners(view: View) {
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_ConfirmBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.retakeButton.setOnClickListener {
            findNavController().navigate(R.id.action_ConfirmBodyCompFragment_to_ScanningBodyCompFragment)
        }
        binding.seeMyResultsButton.setOnClickListener {
            setFragmentResult(
                "requestKey", bundleOf(
                    "imageThumbnail" to inputParam.first,
                    "imageUri" to inputParam.second,
                )
            )
            findNavController().navigate(
                ConfirmBodyCompFragmentDirections.actionConfirmBodyCompFragmentToAnalyzingBodyCompFragment()
            )
        }
        val scope = ViewTreeLifecycleOwner.get(view)!!.lifecycleScope
        val clickWithDebounce: (view: View) -> Unit =
            debounce(scope = scope) {
                showDialog()
            }
        binding.editButton.setOnClickListener(clickWithDebounce)
    }

    private fun showDialog() {
        val dialogFragment = ConfirmBodyCompDialogFragment(this)
        dialogFragment.show(parentFragmentManager, "signature")
    }


    override fun onClose() {
        setDefaultValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getInputArgs(): Pair<Bitmap, Uri> {
        arguments?.let {
            val img = ConfirmBodyCompFragmentArgs.fromBundle(it).image
            val image = Uri.parse(img)
            val fis = context?.contentResolver?.openFileDescriptor(image, "r")?.fileDescriptor

            // Get Image Size
            val opt = BitmapFactory.Options()
            opt.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fis, null, opt)

//            var bitmapOriginal = BitmapFactory.decodeFileDescriptor(fis)
            val dimension = Math.max(opt.outWidth, opt.outHeight).toFloat()
            val ratio = Math.max((dimension / IMAGE_MAX_DIMENSION).toInt(), 1)

            val options = BitmapFactory.Options()
            // Set image output size
            options.inSampleSize = ratio
            options.inMutable = true
            var bitmap = BitmapFactory.decodeFileDescriptor(fis, null, options)

            //Rotate image if required
            fis?.let {
                bitmap = Image.rotateImageIfRequired(fis, bitmap)
            } ?: run {
                Log.e(this::class.simpleName, "No fileDescriptor retrieved")
            }

            return Pair(bitmap, image)
        } ?: run {
            Log.e(this::class.simpleName, "No arguments retrieved")
            throw RuntimeException("No arguments retrieved")
        }
    }
    companion object {
        private const val TAG = "ConfirmBodyCompFragment"
        private const val IMAGE_MAX_DIMENSION = 500.0F
    }
}