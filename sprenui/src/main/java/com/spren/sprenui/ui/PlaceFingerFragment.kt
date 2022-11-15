package com.spren.sprenui.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentPlaceFingerBinding

class PlaceFingerFragment : Fragment() {

    private var _binding: FragmentPlaceFingerBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceFingerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_PlaceFingerFragment_to_GreetingFragment)
        }
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_PlaceFingerFragment_to_ScanningFragment)
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.FINGER_ON_CAMERA] != null) {
            binding.placeFingerImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.placeFingerImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.FINGER_ON_CAMERA]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}