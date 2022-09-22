package com.spren.sprenui.ui

import android.app.ActivityManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentMeasureHrvHomeBinding
import com.spren.sprenui.util.HardwareAlert

class MeasureHRVHomeFragment : Fragment() {

    private var _binding: FragmentMeasureHrvHomeBinding? = null
    private lateinit var hardwareAlert: HardwareAlert

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMeasureHrvHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.advanceHrvTitle.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.advanceHrvText.typeface = font
        binding.tryButton.typeface = font
        binding.tryButton.setOnClickListener {
            findNavController().navigate(R.id.action_MeasureHRVHomeFragment_to_GreetingFragment)
        }
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_MeasureHRVHomeFragment_to_HomeFragment)
        }

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.background)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val activityManager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val isHighPerformingDevice = !activityManager.isLowRamDevice && Runtime.getRuntime()
            .availableProcessors() >= 8 && activityManager.memoryClass >= 192
        hardwareAlert = HardwareAlert(requireActivity())
        if (!isHighPerformingDevice) {
            hardwareAlert.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}