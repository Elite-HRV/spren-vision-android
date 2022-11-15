package com.spren.sprenui.ui.bodycomp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentDeniedPermissionsBodyCompBinding
import com.spren.sprenui.util.Permissions.CAMERA_PERMISSIONS
import com.spren.sprenui.util.Permissions.EXTERNAL_STORAGE_PERMISSIONS
import com.spren.sprenui.util.Permissions.allPermissionsGranted

class DeniedPermissionsBodyCompFragment : Fragment() {

    private var _binding: FragmentDeniedPermissionsBodyCompBinding? = null
    private var cameraPermission = true

    private val binding get() = _binding!!

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (allPermissionsGranted(
                    requireActivity().baseContext,
                    if (cameraPermission) CAMERA_PERMISSIONS else EXTERNAL_STORAGE_PERMISSIONS
                )
            ) {
                findNavController().navigate(R.id.action_DeniedPermissionsBodyCompFragment_to_SetupBodyCompFragment)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDeniedPermissionsBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_DeniedPermissionsBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.enableButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            resultLauncher.launch(intent)
        }
        arguments?.let {
            cameraPermission = DeniedPermissionsBodyCompFragmentArgs.fromBundle(it).camera
            binding.deniedPermissionsBodyCompTitle.text = if (cameraPermission)
                context?.getString(R.string.denied_permissions_body_comp_camera_title) else context?.getString(
                R.string.denied_permissions_body_comp_photos_title
            )
            binding.deniedPermissionsBodyCompText.text = if (cameraPermission)
                context?.getString(R.string.denied_permissions_body_comp_camera_text) else context?.getString(
                R.string.denied_permissions_body_comp_photos_text
            )
            binding.enableButton.text = if (cameraPermission)
                context?.getString(R.string.denied_permissions_body_comp_camera_button) else context?.getString(
                R.string.denied_permissions_body_comp_photos_button
            )
            binding.deniedPermissionsBodyCompImage.setBackgroundResource(
                if (cameraPermission)
                    R.drawable.ic_denied_permissions else R.drawable.ic_denied_photo_permissions
            )
            if (cameraPermission && SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.CAMERA_ACCESS_DENIED] != null) {
                binding.deniedPermissionsBodyCompImage.setColorFilter(requireContext().getColor(R.color.transparent))
                binding.deniedPermissionsBodyCompImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.CAMERA_ACCESS_DENIED]!!)
            }
            if (!cameraPermission && SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.PHOTOS_ACCESS_DENIED] != null) {
                binding.deniedPermissionsBodyCompImage.setColorFilter(requireContext().getColor(R.color.transparent))
                binding.deniedPermissionsBodyCompImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.PHOTOS_ACCESS_DENIED]!!)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}