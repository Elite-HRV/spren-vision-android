package com.spren.sprenui.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentGreetingBinding

class GreetingFragment : Fragment() {

    private var _binding: FragmentGreetingBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGreetingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            requireActivity().onBackPressed()
        }

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = requireContext().getColor(R.color.background)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.GREETING_1] != null) {
            binding.measureHrvImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.measureHrvImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.GREETING_1]!!)
        }

        val sharedPreference =
            requireActivity().getSharedPreferences("READING", Context.MODE_PRIVATE)
        val subsequentReadingFlow = sharedPreference.getBoolean("subsequentReadingFlow", false)
        if (subsequentReadingFlow) {
            binding.nextButton.setOnClickListener {
                findNavController().navigate(R.id.action_GreetingFragment_to_PlaceFingerFragment)
            }
            binding.measureHrvTitle.text = getString(R.string.measure_hrv_title_2)
            binding.measureHrvText.visibility = View.GONE
        } else {
            binding.measureHrvTitle.text = getString(R.string.measure_hrv_title_1)
            binding.thirdBullet.visibility = View.GONE
            binding.thirdBulletText.visibility = View.GONE
            binding.nextButton.setOnClickListener {
                findNavController().navigate(R.id.action_GreetingFragment_to_AllowCameraFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}