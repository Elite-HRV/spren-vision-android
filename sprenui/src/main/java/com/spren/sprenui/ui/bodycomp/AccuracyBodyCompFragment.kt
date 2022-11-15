package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentAccuracyBodyCompBinding


class AccuracyBodyCompFragment : Fragment() {

    private var _binding: FragmentAccuracyBodyCompBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccuracyBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sourceString =
            "<b>Disclaimer:</b> ${context?.getString(R.string.accuracy_body_comp_text_4)}"
        binding.accuracyBodyCompText4.text = Html.fromHtml(sourceString)
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_AccuracyBodyCompFragment_to_GreetingBodyCompFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}