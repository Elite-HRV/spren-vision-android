package com.spren.sprenui.ui

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
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ServerSideErrorFragment_to_GreetingFragment)
        }
        binding.tryAgainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ServerSideErrorFragment_to_ScanningFragment)
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.SERVER_ERROR] != null) {
            binding.serverSideErrorImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.serverSideErrorImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.SERVER_ERROR]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}