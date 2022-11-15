package com.spren.sprenui.ui.bodycomp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentSetupBodyCompBinding
import com.spren.sprenui.util.Permissions.CAMERA_PERMISSIONS
import com.spren.sprenui.util.Permissions.EXTERNAL_STORAGE_PERMISSIONS
import com.spren.sprenui.util.Permissions.allPermissionsGranted

class SetupBodyCompFragment : Fragment() {

    private var _binding: FragmentSetupBodyCompBinding? = null

    private val binding get() = _binding!!

    private var cameraPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (allPermissionsGranted(requireActivity().baseContext)) {
                findNavController().navigate(R.id.action_SetupBodyCompFragment_to_ScanningBodyCompFragment)
            } else {
                val direction =
                    SetupBodyCompFragmentDirections.actionSetupBodyCompFragmentToDeniedPermissionsBodyCompFragment(
                        true
                    )
                findNavController().navigate(direction)
            }
        }

    private var externalStoragePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (allPermissionsGranted(
                    requireActivity().baseContext,
                    EXTERNAL_STORAGE_PERMISSIONS
                )
            ) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(galleryIntent)
            } else {
                val direction =
                    SetupBodyCompFragmentDirections.actionSetupBodyCompFragmentToDeniedPermissionsBodyCompFragment(
                        false
                    )
                findNavController().navigate(direction)
            }
        }

    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val direction =
                SetupBodyCompFragmentDirections.actionSetupBodyCompFragmentToConfirmBodyCompFragment(
                    it.data!!.data.toString()
                )
            findNavController().navigate(direction)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSetupBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_SetupBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.startButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            alertDialogBuilder.setTitle("Take a photo or upload one you already have")
            val pictureDialogItems = arrayOf("Take photo", "Upload from my device")
            alertDialogBuilder.setItems(
                pictureDialogItems
            ) { _, which ->
                if (which == 0) {
                    if (allPermissionsGranted(requireActivity().baseContext)) {
                        findNavController().navigate(R.id.action_SetupBodyCompFragment_to_ScanningBodyCompFragment)
                    } else {
                        cameraPermissionsLauncher.launch(CAMERA_PERMISSIONS)
                    }
                } else if (which == 1) {
                    if (allPermissionsGranted(
                            requireActivity().baseContext,
                            EXTERNAL_STORAGE_PERMISSIONS
                        )
                    ) {
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryLauncher.launch(galleryIntent)
                    } else {
                        externalStoragePermissionsLauncher.launch(EXTERNAL_STORAGE_PERMISSIONS)
                    }
                }
            }
            alertDialogBuilder.show()
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.SETUP_GUIDE] != null) {
            binding.setupBodyComp.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.setupBodyComp.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.SETUP_GUIDE]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}