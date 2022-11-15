package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentAgeBodyCompBinding
import com.spren.sprenui.util.SharedPreferences

class AgeBodyCompFragment : Fragment() {
    companion object {
        const val AGE = "com.spren.bodycomp.key.userAge"
    }

    private var _binding: FragmentAgeBodyCompBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgeBodyCompBinding.inflate(inflater, container, false)

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
            findNavController().navigate(R.id.action_AgeBodyCompFragment_to_GreetingBodyCompFragment)
        }
        binding.nextButton.setOnClickListener {
            // Save preferences
            val sharedPref = SharedPreferences.getSharedPreferences(activity)
            with(sharedPref.edit()) {
                val age = binding.ageTextInputEditText.text.toString().toInt()

                putInt(AGE, age)
                apply()
            }
            // Navigate
            findNavController().navigate(R.id.action_AgeBodyCompFragment_to_GenderBodyCompFragment)
        }
        binding.ageTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.nextButton.isEnabled = s.isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        // Age
        val age = sharedPref.getInt(AGE, 0)
        if (age > 0) {
            binding.nextButton.isEnabled = true
            binding.ageTextInputEditText.setText(age.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}