package com.spren.sprenui.ui.bodycomp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentResultBodyCompBinding
import com.spren.sprenui.network.model.bodycomp.Result
import com.spren.sprenui.util.SharedPreferences
import com.spren.sprenui.util.Units


class ResultBodyCompFragment : Fragment() {
    companion object {
        const val SCAN_RESULT_KEY = "scan_result"
    }

    private var _binding: FragmentResultBodyCompBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResultBodyCompBinding.inflate(inflater, container, false)
        setDefaultValues()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind action listeners
        setActionListeners()
    }

    private fun setDefaultValues() {
        val sharedPref = SharedPreferences.getSharedPreferences(activity)

        val res = sharedPref.getString(SCAN_RESULT_KEY, null)
        if (res == null) {
            Log.e(this::class.simpleName, "no saved result")
            throw RuntimeException("no saved result")
        }

        val result = Gson().fromJson(res, Result::class.java)

        val weightMetric = sharedPref.getString(WeightBodyCompFragment.WEIGHT_METRIC, null)
        val weight = sharedPref.getFloat(WeightBodyCompFragment.WEIGHT, 0F)

        if (weight <= 0 || weightMetric == null) {
            Log.w(this::class.simpleName, "Not set all values weight: ${weight}, weightMetric: ${weightMetric}, ")
            return
        }

        binding.metric = weightMetric
        binding.bodyFatValue = result.bodyFat.value.toInt().toString() // 25

        binding.leanMassValue = String.format("%.2f", Units.convertToImperialIfNeeded(result.leanMass.value, weightMetric)) // "136.15"
        binding.fatMassValue = String.format("%.1f", Units.convertToImperialIfNeeded(result.fatMass.value, weightMetric)) // "18.5"
        binding.weightValue = String.format("%.1f", weight) // 155
        binding.androidFatValue = String.format("%.2f", Units.convertToImperialIfNeeded(result.androidFat.value, weightMetric)) // "2.91"
        binding.gynoidFatValue = String.format("%.2f", Units.convertToImperialIfNeeded(result.gynoidFat.value, weightMetric)) // "1.05"
        binding.androidGynoidValue = String.format("%.1f", result.androidByGynoid.value) // "1.3"
    }

    private fun setActionListeners() {
        binding.infoImage.setOnClickListener {
            findNavController().navigate(R.id.action_ResultBodyCompFragment_to_ResultInfoBodyCompFragment)
        }
        binding.doneButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}