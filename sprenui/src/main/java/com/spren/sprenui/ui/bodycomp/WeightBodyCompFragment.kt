package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentWeightBodyCompBinding
import com.spren.sprenui.util.SharedPreferences

class WeightBodyCompFragment : Fragment() {
    companion object {
        // "lbs" is a default metric
        const val WEIGHT_METRIC_LBS = "lbs"
        const val WEIGHT_METRIC_KG = "kg"
        const val WEIGHT_METRIC = "com.spren.bodycomp.key.userWeightMetric"
        const val WEIGHT = "com.spren.bodycomp.key.userWeight"
    }
    private var _binding: FragmentWeightBodyCompBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeightBodyCompBinding.inflate(inflater, container, false)

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
            SprenUI.Config.onCancel?.let {
                it.invoke()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_WeightBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.nextButton.setOnClickListener {
            // Save preferences
            val sharedPref = SharedPreferences.getSharedPreferences(activity)
            with(sharedPref.edit()) {
                val checkedChipId = binding.weightBodyCompChips.checkedChipIds.first()
                val chip = activity?.findViewById<Chip>(checkedChipId)
                    ?: throw RuntimeException("${this::class.simpleName}: Chip is not found")
                val weightMetric = chip.text.toString()
                val weight = binding.textInput.text.toString().toFloat()

                putString(WEIGHT_METRIC, weightMetric)
                putFloat(WEIGHT, weight)
                apply()
            }
            // Navigate
            findNavController().navigate(R.id.action_WeightBodyCompFragment_to_HeightBodyCompFragment)
        }

        binding.textInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.nextButton.isEnabled = s.isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        // Weight metric
        when (sharedPref.getString(WEIGHT_METRIC, WEIGHT_METRIC_LBS)) {
            WEIGHT_METRIC_LBS -> binding.chipLbs.isChecked = true
            WEIGHT_METRIC_KG -> binding.chipKgs.isChecked = true
            else -> Log.w(this::class.simpleName, "weightMetric no default value")
        }

        // Weight
        val weight = sharedPref.getFloat(WEIGHT, 0F)
        if (weight > 0) {
            binding.nextButton.isEnabled = true
            binding.textInput.setText(weight.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}