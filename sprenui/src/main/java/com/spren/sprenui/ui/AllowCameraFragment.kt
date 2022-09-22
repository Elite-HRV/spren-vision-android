package com.spren.sprenui.ui

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.util.Permissions.REQUIRED_PERMISSIONS
import com.spren.sprenui.util.Permissions.allPermissionsGranted
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentAllowCameraBinding

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

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.placeYourFingertipTitle.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.placeYourFingertipText.typeface = font
        binding.nextButton.typeface = font
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_AllowCameraFragment_to_MeasureHRVHomeFragment)
        }
        binding.nextButton.setOnClickListener {
            if (allPermissionsGranted(requireActivity().baseContext)) {
                redirectToPlaceFingerFragment()
            } else {
                permissionsLauncher.launch(REQUIRED_PERMISSIONS)
            }
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