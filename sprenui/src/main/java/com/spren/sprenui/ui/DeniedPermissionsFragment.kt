package com.spren.sprenui.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.util.Permissions.allPermissionsGranted
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentDeniedPermissionsBinding

class DeniedPermissionsFragment : Fragment() {

    private var _binding: FragmentDeniedPermissionsBinding? = null

    private val binding get() = _binding!!

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (allPermissionsGranted(requireActivity().baseContext)) {
                findNavController().navigate(R.id.action_DeniedPermissionsFragment_to_PlaceFingerFragment)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDeniedPermissionsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.deniedPermissionsTitle.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.deniedPermissionsText.typeface = font
        binding.enableButton.typeface = font
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_DeniedPermissionsFragment_to_MeasureHRVHomeFragment)
        }
        binding.enableButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            resultLauncher.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}