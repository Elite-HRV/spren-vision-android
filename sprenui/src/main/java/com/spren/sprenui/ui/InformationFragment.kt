package com.spren.sprenui.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.background)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.barText.typeface = font
        binding.titleText.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        arguments?.let {
            val barText = getString(InformationFragmentArgs.fromBundle(it).barText)
            binding.barText.text = barText
            val title = InformationFragmentArgs.fromBundle(it).title
            if (title != 0) {
                binding.titleText.text = getString(title)
                binding.titleText.typeface = font
            } else {
                binding.titleText.text = barText
            }
            binding.firstText.text = getString(InformationFragmentArgs.fromBundle(it).firstText)
            val image = InformationFragmentArgs.fromBundle(it).image
            if (image != 0) {
                binding.image.setImageResource(image)
            } else {
                binding.image.visibility = View.GONE
            }
            val secondText = InformationFragmentArgs.fromBundle(it).secondText
            if (secondText != 0) {
                binding.secondText.text = getString(secondText)
            } else {
                binding.secondText.visibility = View.GONE
            }
        }
        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
