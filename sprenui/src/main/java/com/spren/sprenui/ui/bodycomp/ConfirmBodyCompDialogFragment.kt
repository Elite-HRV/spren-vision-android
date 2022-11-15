package com.spren.sprenui.ui.bodycomp

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentConfirmationBodyCompDialogBinding
import com.spren.sprenui.util.SharedPreferences
import com.spren.sprenui.util.debounce

class ConfirmBodyCompDialogFragment(private val callbackListener: ConfirmBodyCompDialogCallbackListener) : DialogFragment() {
    private var _binding: FragmentConfirmationBodyCompDialogBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = FragmentConfirmationBodyCompDialogBinding.inflate(inflater, container, false)

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
        binding.closeButton.setOnClickListener {
            // Save preferences
            val sharedPref = SharedPreferences.getSharedPreferences(activity)
            with(sharedPref.edit()) {
                // Weight
                val weight = binding.weightInput.text.toString().toFloat()
                putFloat(WeightBodyCompFragment.WEIGHT, weight)

                // Height
                val heightMetric = sharedPref.getString(HeightBodyCompFragment.HEIGHT_METRIC, null)

                when (heightMetric) {
                    HeightBodyCompFragment.HEIGHT_METRIC_FT -> {
                        val feet = (binding.heightFeetInput.editText as AutoCompleteTextView).text.toString().toInt()
                        val inches = (binding.heightInchesInput.editText as AutoCompleteTextView).text.toString().toInt()
                        putInt(HeightBodyCompFragment.HEIGHT_FT, feet)
                        putInt(HeightBodyCompFragment.HEIGHT_IN, inches)
                    }
                    HeightBodyCompFragment.HEIGHT_METRIC_CM -> {
                        val cm = binding.heightCmInput.text.toString().toFloat()
                        putFloat(HeightBodyCompFragment.HEIGHT_CM, cm)
                    }
                    else -> Log.w(this::class.simpleName, "nextButton no default value")
                }

                // Age
                val age = binding.ageInput.text.toString().toInt()
                putInt(AgeBodyCompFragment.AGE, age)

                // Gender
                val gender = (binding.genderInput.editText as AutoCompleteTextView).text.toString()
                putString(GenderBodyCompFragment.GENDER, gender)

                // Activity Level
                val activityLevel = (binding.recentActivityLevelInput.editText as AutoCompleteTextView).text.toString()
                putString(ActivityLevelBodyCompFragment.ACTIVITY_LEVEL, activityLevel)

                apply()
            }
            callbackListener.onClose()
            dismiss()
        }
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        val weight = sharedPref.getFloat(WeightBodyCompFragment.WEIGHT, 0F)
        val weightMetric = sharedPref.getString(WeightBodyCompFragment.WEIGHT_METRIC, null)
        val age = sharedPref.getInt(AgeBodyCompFragment.AGE, 0)
        val activityLevel = sharedPref.getString(ActivityLevelBodyCompFragment.ACTIVITY_LEVEL, null)
        val heightMetric = sharedPref.getString(HeightBodyCompFragment.HEIGHT_METRIC, null)
        val heightFT = sharedPref.getInt(HeightBodyCompFragment.HEIGHT_FT, 0)
        val heightIN = sharedPref.getInt(HeightBodyCompFragment.HEIGHT_IN, 0)
        val heightCM = sharedPref.getFloat(HeightBodyCompFragment.HEIGHT_CM, 0F)
        val gender = sharedPref.getString(GenderBodyCompFragment.GENDER, null)

        if (weight <= 0
            || weightMetric == null
            || age <= 0
            || activityLevel == null
            || heightMetric == null
            || (heightFT <= 0 && heightIN <= 0 && heightCM <= 0)
            || gender == null
        ) {
            Log.w(this::class.simpleName, "Not set all values weight: ${weight}, weightMetric: ${weightMetric}, ")
            return
        }

        // Weight
        binding.weightLabel.text = weightMetric
        binding.weightInput.setText(weight.toString())

        // Height
        if (heightMetric == HeightBodyCompFragment.HEIGHT_METRIC_FT) {
            binding.heightFTConstraintLayout.visibility = View.VISIBLE
            binding.heightINConstraintLayout.visibility = View.VISIBLE
            binding.heightCMConstraintLayout.visibility = View.GONE
        } else if (heightMetric == HeightBodyCompFragment.HEIGHT_METRIC_CM) {
            binding.heightCMConstraintLayout.visibility = View.VISIBLE
            binding.heightFTConstraintLayout.visibility = View.GONE
            binding.heightINConstraintLayout.visibility = View.GONE
        } else Log.w(this::class.simpleName, "heightMetric no default value")

        val feetDropdown = binding.heightFeetInput.editText as AutoCompleteTextView
        feetDropdown.setText(heightFT.toString(), false)
        val inchesDropdown = binding.heightInchesInput.editText as AutoCompleteTextView
        inchesDropdown.setText(heightIN.toString(), false)
        binding.heightCmInput.setText(heightCM.toString())

        // Age
        binding.ageInput.setText(age.toString())

        // Gender
        val genderDropdown = binding.genderInput.editText as AutoCompleteTextView
        genderDropdown.setText(gender, false)

        // Activity Level
        val activityLevelDropdown = binding.recentActivityLevelInput.editText as AutoCompleteTextView
        activityLevelDropdown.setText(activityLevel, false)
    }

    private fun setDropdownAdapters() {
        val adapterFt = ArrayAdapter(requireContext(), R.layout.list_item, HeightBodyCompFragment.HEIGHT_FEET)
        val adapterIn = ArrayAdapter(requireContext(), R.layout.list_item, HeightBodyCompFragment.HEIGHT_INCHES)
        val genders = ArrayAdapter(requireContext(), R.layout.list_item, GenderBodyCompFragment.GENDERS)
        val adapterActivityLevels = ArrayAdapter(requireContext(), R.layout.list_item, ActivityLevelBodyCompFragment.ACTIVITY_LEVELS)

        val feetDropdown = binding.heightFeetInput.editText as AutoCompleteTextView
        val inchesDropdown = binding.heightInchesInput.editText as AutoCompleteTextView
        val gendersDropdown = binding.genderInput.editText as AutoCompleteTextView
        val adapterActivityLevelsDropdown = binding.recentActivityLevelInput.editText as AutoCompleteTextView

        feetDropdown.setAdapter(adapterFt)
        inchesDropdown.setAdapter(adapterIn)
        gendersDropdown.setAdapter(genders)
        adapterActivityLevelsDropdown.setAdapter(adapterActivityLevels)
    }
}