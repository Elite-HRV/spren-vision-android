package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentGreetingBodyCompBinding

class GreetingBodyCompFragment : Fragment() {

    private var _binding: FragmentGreetingBodyCompBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGreetingBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeImage.setOnClickListener {
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            requireActivity().onBackPressed()
        }
        binding.tryNowButton.setOnClickListener {
            findNavController().navigate(R.id.action_GreetingBodyCompFragment_to_WeightBodyCompFragment)
        }
        binding.greetingBodyCompPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_GreetingBodyCompFragment_to_PrivacyBodyCompFragment)
        }
        binding.greetingBodyCompAccuracy.setOnClickListener {
            findNavController().navigate(R.id.action_GreetingBodyCompFragment_to_AccuracyBodyCompFragment)
        }

        if (SprenUI.Config.graphics != null && SprenUI.Config.graphics!![SprenUI.Graphic.GREETINGS] != null) {
            binding.greetingBodyCompImage.setColorFilter(requireContext().getColor(R.color.transparent))
            binding.greetingBodyCompImage.setImageResource(SprenUI.Config.graphics!![SprenUI.Graphic.GREETINGS]!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}