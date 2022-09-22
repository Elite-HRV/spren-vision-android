package com.spren.sprenui.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentHomeBinding
import com.spren.sprenui.util.getThemeId

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.homeTitle.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.hrvCardTitle.typeface = font
        binding.hrvCardText.typeface = font
        binding.hrvCardTry.typeface = font
        binding.hrvCard.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_MeasureHRVHomeFragment)
        }
        binding.hrvCardTry.setTextAppearance(requireContext().getThemeId())

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.home_background)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}