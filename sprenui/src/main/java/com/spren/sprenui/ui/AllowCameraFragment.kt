package com.spren.sprenui.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.util.Permissions.allPermissionsGranted
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentAllowCameraBinding
import com.spren.sprenui.util.Permissions.CAMERA_PERMISSIONS

class AllowCameraFragment : Fragment() {

    private var _binding: FragmentAllowCameraBinding? = null

    private val binding get() = _binding!!

    private var permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (allPermissionsGranted(requireActivity().baseContext)) {
                redirectToPlaceFingerFragment()
            } else {
                findNavController()
                    .navigate(R.id.action_AllowCameraFragment_to_DeniedPermissionsFragment)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAllowCameraBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_AllowCameraFragment_to_GreetingFragment)
        }
        binding.nextButton.setOnClickListener {
            if (allPermissionsGranted(requireActivity().baseContext)) {
                redirectToPlaceFingerFragment()
            } else {
                permissionsLauncher.launch(CAMERA_PERMISSIONS)
            }
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.GREETING_2] != null) {
            binding.placeYourFingertipImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.placeYourFingertipImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.GREETING_2]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun redirectToPlaceFingerFragment() {
        findNavController()
            .navigate(R.id.action_AllowCameraFragment_to_PlaceFingerFragment)
    }
}