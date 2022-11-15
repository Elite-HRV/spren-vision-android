package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentHeightBodyCompBinding
import com.spren.sprenui.util.SharedPreferences

class HeightBodyCompFragment : Fragment() {
    companion object {
        val HEIGHT_FEET = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        val HEIGHT_INCHES = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        // "ft/in" is a default metric
        const val HEIGHT_METRIC_FT = "ft/in"
        const val HEIGHT_METRIC_CM = "cm"
        const val HEIGHT_METRIC = "com.spren.bodycomp.key.userHeightMetric"
        const val HEIGHT_FT = "com.spren.bodycomp.key.userHeightFT.value"
        const val HEIGHT_IN = "com.spren.bodycomp.key.userHeightIN.value"
        const val HEIGHT_CM = "com.spren.bodycomp.key.userHeightCM.value"
    }

    private var _binding: FragmentHeightBodyCompBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHeightBodyCompBinding.inflate(inflater, container, false)
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
            findNavController().navigate(R.id.action_HeightBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.nextButton.setOnClickListener {
            // Save preferences
            val sharedPref = SharedPreferences.getSharedPreferences(activity)
            with(sharedPref.edit()) {
                val checkedChipId = binding.heightBodyCompChips.checkedChipIds.first()
                val chip = activity?.findViewById<Chip>(checkedChipId)
                    ?: throw RuntimeException("${this::class.simpleName}: Chip is not found")
                val heightMetric = chip.text.toString()

                putString(HEIGHT_METRIC, heightMetric)
                when (heightMetric) {
                    HEIGHT_METRIC_FT -> {
                        val feet = (binding.feetTextInputLayout.editText as AutoCompleteTextView).text.toString().toInt()
                        val inches = (binding.inchesTextInputLayout.editText as AutoCompleteTextView).text.toString().toInt()
                        putInt(HEIGHT_FT, feet)
                        putInt(HEIGHT_IN, inches)
                    }
                    HEIGHT_METRIC_CM -> {
                        val cm = binding.cmTextInputEditText.text.toString().toFloat()
                        putFloat(HEIGHT_CM, cm)
                    }
                    else -> Log.w(this::class.simpleName, "nextButton no default value")
                }
                apply()
            }
            // Navigate
            findNavController().navigate(R.id.action_HeightBodyCompFragment_to_AgeBodyCompFragment)
        }

        binding.heightBodyCompChips.setOnCheckedChangeListener { _, checkedId ->
            val chip = activity?.findViewById<Chip>(checkedId) ?: throw RuntimeException("${this::class.simpleName}: Chip is not found")
            when {
                chip.text.equals(HEIGHT_METRIC_FT) && chip.isChecked -> setFeetSelected()
                chip.text.equals(HEIGHT_METRIC_CM) && chip.isChecked -> setCmSelected()
                else -> Log.w(this::class.simpleName, "heightMetric incorrect state")
            }
            enableNextButton(chip.text.toString())
        }

        binding.cmTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enableNextButton(HEIGHT_METRIC_CM)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) { }
        })

        val feetDropdown = binding.feetTextInputLayout.editText as AutoCompleteTextView
        val inchesDropdown = binding.inchesTextInputLayout.editText as AutoCompleteTextView

        feetDropdown.setOnItemClickListener { _, _, _, _ ->
            enableNextButton(HEIGHT_METRIC_FT)
        }
        inchesDropdown.setOnItemClickListener { _, _, _, _ ->
            enableNextButton(HEIGHT_METRIC_FT)
        }
    }

    private fun enableNextButton(tab: String) {
        when (tab) {
            HEIGHT_METRIC_FT -> {
                val feet = (binding.feetTextInputLayout.editText as AutoCompleteTextView).text.toString()
                val inches = (binding.inchesTextInputLayout.editText as AutoCompleteTextView).text.toString()

                binding.nextButton.isEnabled = feet.isNotBlank() && inches.isNotBlank()
            }
            HEIGHT_METRIC_CM -> {
                val heightCm = binding.cmTextInputEditText.text.toString()

                binding.nextButton.isEnabled = heightCm.isNotBlank()
            }
            else -> Log.w(this::class.simpleName, "enableNextButton incorrect state")
        }
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        // Height feet
        val heightFT = sharedPref.getInt(HEIGHT_FT, 0)
        if (heightFT > 0) {
            val feetDropdown = binding.feetTextInputLayout.editText as AutoCompleteTextView
            feetDropdown.setText(heightFT.toString(), false)
        }

        // Height inches
        val heightIN = sharedPref.getInt(HEIGHT_IN, 0)
        if (heightIN > 0) {
            val inchesDropdown = binding.inchesTextInputLayout.editText as AutoCompleteTextView
            inchesDropdown.setText(heightIN.toString(), false)
        }

        // Height cm
        val heightCM = sharedPref.getFloat(HEIGHT_CM, 0F)
        if (heightCM > 0) {
            binding.cmTextInputEditText.setText(heightCM.toString())
        }

        // Height metric
        when (sharedPref.getString(HEIGHT_METRIC, HEIGHT_METRIC_FT)) {
            HEIGHT_METRIC_FT -> setFeetSelected()
            HEIGHT_METRIC_CM -> setCmSelected()
            else -> Log.w(this::class.simpleName, "heightMetric no default value")
        }
    }

    private fun setFeetSelected() {
        binding.chipFt.isChecked = true
        binding.feetLinearLayout.visibility = View.VISIBLE
        binding.cmConstraintLayout.visibility = View.GONE
        enableNextButton(HEIGHT_METRIC_FT)
    }

    private fun setCmSelected() {
        binding.chipCm.isChecked = true
        binding.cmConstraintLayout.visibility = View.VISIBLE
        binding.feetLinearLayout.visibility = View.GONE
        enableNextButton(HEIGHT_METRIC_CM)
    }

    private fun setDropdownAdapters() {
        val adapterFt = ArrayAdapter(requireContext(), R.layout.list_item, HEIGHT_FEET)
        val adapterIn = ArrayAdapter(requireContext(), R.layout.list_item, HEIGHT_INCHES)
        val feetDropdown = binding.feetTextInputLayout.editText as AutoCompleteTextView
        val inchesDropdown = binding.inchesTextInputLayout.editText as AutoCompleteTextView
        feetDropdown.setAdapter(adapterFt)
        inchesDropdown.setAdapter(adapterIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}