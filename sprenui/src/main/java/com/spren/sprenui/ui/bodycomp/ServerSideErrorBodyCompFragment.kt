package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentServerSideErrorBodyCompBinding

class ServerSideErrorBodyCompFragment : Fragment() {
    private var _binding: FragmentServerSideErrorBodyCompBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServerSideErrorBodyCompBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Bind action listeners
        setActionListeners()
    }

    private fun setActionListeners() {
        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ServerSideErrorBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.tryNowButton.setOnClickListener {
            findNavController().navigate(R.id.action_ServerSideErrorBodyCompFragment_to_ScanningBodyCompFragment)
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.SERVER_ERROR] != null) {
            binding.greetingBodyCompImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.greetingBodyCompImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.SERVER_ERROR]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}