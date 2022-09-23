package com.spren.sprenui.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.spren.sprencore.Spren
import com.spren.sprenui.R
import com.spren.sprenui.databinding.FragmentCalculatingBinding
import com.spren.sprenui.network.RetrofitHelper
import com.spren.sprenui.network.api.getResults
import com.spren.sprenui.network.service.InsightsApi
import com.spren.sprenui.util.ReadingStatus
import kotlinx.coroutines.*

class CalculatingFragment : Fragment() {

    private var _binding: FragmentCalculatingBinding? = null

    private val binding get() = _binding!!

    private lateinit var checkInternetConnection: Job
    private var updateProgress: Job? = null
    private var repetition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalculatingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.calculatingTitle.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.calculatingText.typeface = font
        binding.forInvestigationalUseOnlyText.typeface = font
        binding.closeImage.setOnClickListener {
            findNavController().navigate(R.id.action_CalculatingFragment_to_MeasureHRVHomeFragment)
        }
        checkInternetConnection =
            viewLifecycleOwner.lifecycleScope.launch(context = Dispatchers.Main) { checkInternetConnection() }
        if (isInternetAvailable(requireContext())) {
            updateProgress =
                viewLifecycleOwner.lifecycleScope.launch(context = Dispatchers.Main) { updateProgress() }
            calculateResults()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateResults() {
        val readingData = Spren.getReadingData(requireContext())
        readingData?.let {
            val insightsApi =
                RetrofitHelper.getInstance().create(InsightsApi::class.java)
            viewLifecycleOwner.lifecycleScope.launch(context = Dispatchers.IO) {
                try {
                    val results =
                        getResults(insightsApi, it)
                    viewLifecycleOwner.lifecycleScope.launch(context = Dispatchers.Main) {
                        results?.let {
                            updateProgress?.cancel()
                            binding.calculatingText.text = "Complete!"
                            binding.checkImage.visibility = View.VISIBLE
                            if (it.biomarkers.hr.status == ReadingStatus.COMPLETE.value && it.biomarkers.hrvScore.status == ReadingStatus.COMPLETE.value &&
                                it.biomarkers.hr.value != 0.0 && it.biomarkers.hrvScore.value != 0.0
                            ) {
                                val hr = it.biomarkers.hr.value!!.toFloat()
                                val hrvScore = it.biomarkers.hrvScore.value!!.toFloat()
                                val rmssd = it.biomarkers.rmssd.value?.toFloat()
                                val breathingRate = it.biomarkers.breathingRate.value?.toFloat()
                                val readiness = it.insights.readiness.value?.toFloat()
                                val ansBalance = it.insights.ansBalance.value?.toFloat()
                                val signalQuality = it.signalQuality.value?.toFloat()
                                val direction =
                                    CalculatingFragmentDirections.actionCalculatingFragmentToResultsFragment(
                                        hr,
                                        hrvScore,
                                        rmssd ?: 0f,
                                        breathingRate ?: 0f,
                                        readiness ?: 0f,
                                        ansBalance ?: 0f,
                                        signalQuality ?: 0f
                                    )
                                findNavController().navigate(direction)
                            } else if (it.biomarkers.hr.status == ReadingStatus.ERROR.value || it.biomarkers.hrvScore.status == ReadingStatus.ERROR.value ||
                                it.biomarkers.hr.value == 0.0 || it.biomarkers.hrvScore.value == 0.0
                            ) {
                                findNavController().navigate(R.id.action_CalculatingFragment_to_ServerSideErrorFragment)
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    private suspend fun checkInternetConnection() = coroutineScope {
        while (isActive) {
            if (!isInternetAvailable(requireContext())) {
                AlertDialog.Builder(requireContext())
                    .setTitle(resources.getString(R.string.network_error_title))
                    .setMessage(resources.getString(R.string.network_error_text))
                    .setPositiveButton(
                        resources.getString(R.string.network_error_primary_button_text)
                    ) { _, _ -> findNavController().navigate(R.id.action_CalculatingFragment_to_ScanningFragment) }
                    .setNegativeButton(
                        resources.getString(R.string.network_error_secondary_button_text)
                    ) { _, _ -> findNavController().navigate(R.id.action_CalculatingFragment_to_MeasureHRVHomeFragment) }
                    .setCancelable(false)
                    .show()
                updateProgress?.cancel()
                binding.progressCircular.setProgressCompat(0, true)
                checkInternetConnection.cancel()
            }
            delay(2000)
        }
    }

    private suspend fun updateProgress() = coroutineScope {
        while (isActive) {
            binding.calculatingText.text = when (repetition) {
                0 -> "Syncing data... "
                1 -> "Analyzing heart rate data..."
                2 -> "Performing complex calculations..."
                else -> {
                    repetition = -1
                    "Generating results..."
                }
            }
            repetition++
            delay(2000)
        }
    }
}