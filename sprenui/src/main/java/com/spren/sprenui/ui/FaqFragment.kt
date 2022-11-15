package com.spren.sprenui.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spren.sprenui.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }

        arguments?.let {
            val title1Text = FaqFragmentArgs.fromBundle(it).title1Text
            val title2Text = FaqFragmentArgs.fromBundle(it).title2Text
            val subtitleText = FaqFragmentArgs.fromBundle(it).subtitleText
            val firstText = FaqFragmentArgs.fromBundle(it).firstText
            val secondText = FaqFragmentArgs.fromBundle(it).secondText

            if (title1Text != 0) binding.title1Text.text = getString(title1Text)
            if (title2Text != 0) binding.title2Text.text = getString(title2Text)
            if (subtitleText != 0) binding.subtitle.text = getString(subtitleText)
            if (firstText != 0) binding.firstText.text = getString(firstText)
            if (secondText != 0) binding.secondText.text = getString(secondText)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}