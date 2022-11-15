package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentPrivacyBodyCompBinding

class PrivacyBodyCompFragment : Fragment() {

    private var _binding: FragmentPrivacyBodyCompBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPrivacyBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_PrivacyBodyCompFragment_to_GreetingBodyCompFragment)
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.PRIVACY] != null) {
            binding.privacyBodyCompImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.privacyBodyCompImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.PRIVACY]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}