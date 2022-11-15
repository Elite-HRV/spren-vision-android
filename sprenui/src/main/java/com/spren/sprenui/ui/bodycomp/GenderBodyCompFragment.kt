package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentGenderBodyCompBinding
import com.spren.sprenui.util.SharedPreferences

class GenderBodyCompFragment : Fragment() {
    companion object {
        val GENDERS = listOf("Male", "Female", "Other")

        val GENDER_MALE = GENDERS[0]
        val GENDER_FEMALE = GENDERS[1]
        val GENDER_OTHER = GENDERS[2]

        const val GENDER = "com.spren.bodycomp.key.userGender"
    }
    private var _binding: FragmentGenderBodyCompBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGenderBodyCompBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            findNavController().navigate(R.id.action_GenderBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.genderBodyCompChips.setOnCheckedChangeListener { _, _ ->
            enableNextButton()
        }
        binding.nextButton.setOnClickListener {
            // Save preferences
            val sharedPref = SharedPreferences.getSharedPreferences(activity)
            with(sharedPref.edit()) {
                val checkedChipId = binding.genderBodyCompChips.checkedChipIds.first()
                val chip = activity?.findViewById<Chip>(checkedChipId)
                    ?: throw RuntimeException("${this::class.simpleName}: Chip is not found")
                val gender = chip.text.toString()

                putString(GENDER, gender)
                apply()
            }
            // Navigate
            findNavController().navigate(R.id.action_GenderBodyCompFragment_to_ActivityLevelBodyCompFragment)
        }
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        // Gender
        when (sharedPref.getString(GENDER, null)) {
            GENDER_MALE -> binding.genderMale.isChecked = true
            GENDER_FEMALE -> binding.genderFemale.isChecked = true
            GENDER_OTHER -> binding.genderOther.isChecked = true
        }
        enableNextButton()
    }

    private fun enableNextButton() {
        binding.nextButton.isEnabled = binding.genderBodyCompChips.checkedChipIds.size > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}