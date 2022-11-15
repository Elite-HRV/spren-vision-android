package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentActivityLevelBodyCompBinding
import com.spren.sprenui.util.SharedPreferences

class ActivityLevelBodyCompFragment : Fragment() {
    companion object {
        val ACTIVITY_LEVELS = listOf(0, 1, 2, 3, 4, 5, 6, 7)

        const val ACTIVITY_LEVEL = "com.spren.bodycomp.key.userActivityLevel"
    }

    private var _binding: FragmentActivityLevelBodyCompBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActivityLevelBodyCompBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fill dropdowns with items
        setDropdownAdapters()

        // Set default values
        setDefaultValues()

        // Bind action listeners
        setActionListeners()
    }

    private fun setActionListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_ActivityLevelBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.nextButton.setOnClickListener {
            // Save preferences
            val sharedPref = SharedPreferences.getSharedPreferences(activity)
            with(sharedPref.edit()) {
                val activityLevel = (binding.recentActivityLevelInput.editText as AutoCompleteTextView).text.toString()
                putString(ACTIVITY_LEVEL, activityLevel)
                apply()
            }
            // Navigate
            findNavController().navigate(R.id.action_ActivityLevelBodyCompFragment_to_SetupBodyCompFragment)
        }

        val activityLevelDropdown = binding.recentActivityLevelInput.editText as AutoCompleteTextView
        activityLevelDropdown.setOnItemClickListener { _, _, _, _ ->
            enableNextButton()
        }
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        // Activity level
        val activityLevel = sharedPref.getString(ACTIVITY_LEVEL, null)
        if (activityLevel != null) {
            val activityLevelDropdown = binding.recentActivityLevelInput.editText as AutoCompleteTextView
            activityLevelDropdown.setText(activityLevel, false)
        }
        enableNextButton()
    }

    private fun setDropdownAdapters() {
        val adapterActivityLevels = ArrayAdapter(requireContext(), R.layout.list_item, ACTIVITY_LEVELS)
        val adapterActivityLevelsDropdown = binding.recentActivityLevelInput.editText as AutoCompleteTextView
        adapterActivityLevelsDropdown.setAdapter(adapterActivityLevels)
    }

    private fun enableNextButton() {
        val activityLevel = (binding.recentActivityLevelInput.editText as AutoCompleteTextView).text.toString()
        binding.nextButton.isEnabled = activityLevel.isNotBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}