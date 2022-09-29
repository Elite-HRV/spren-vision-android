package com.spren.sprenui.ui

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentServerSideErrorBinding

class ServerSideErrorFragment : Fragment() {

    private var _binding: FragmentServerSideErrorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentServerSideErrorBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.serverSideErrorTitle.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.serverSideErrorText.typeface = font
        binding.tryAgainButton.typeface = font
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ServerSideErrorFragment_to_MeasureHRVHomeFragment)
        }
        binding.tryAgainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ServerSideErrorFragment_to_ScanningFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}