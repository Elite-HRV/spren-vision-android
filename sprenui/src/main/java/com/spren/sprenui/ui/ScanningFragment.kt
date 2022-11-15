package com.spren.sprenui.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.spren.sprencapture.SprenCapture
import com.spren.sprencore.*
import com.spren.sprencore.event.SprenEvent
import com.spren.sprencore.event.SprenEventManager
import com.spren.sprencore.finger.compliance.ComplianceCheck
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentScanningBinding
import com.spren.sprenui.util.Dialog
import com.spren.sprenui.util.HardwareAlert
import com.spren.sprenui.util.ReadingState
import kotlinx.coroutines.*

class ScanningFragment : Fragment() {

    private var _binding: FragmentScanningBinding? = null
    private var sprenCapture: SprenCapture? = null
    private var readingState: ReadingState = ReadingState.PRE_READING
    private var progress: Int = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            updateProgressText()
            if (value < 100) {
                binding.percentageText.text = "$value%"
            } else {
                binding.percentageText.visibility = View.GONE
                binding.checkImage.visibility = View.VISIBLE
            }
            binding.progressCircular.progress = value
        }
    private var showAlert = false
    private lateinit var dialog: android.app.Dialog
    private var alertTitle: String = ""
    private lateinit var hardwareAlert: HardwareAlert

    private val binding get() = _binding!!

    class AppLifecycleListener(var isFirstTime: Boolean = true) : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            if (!isFirstTime) {
                Spren.cancelReading()
            } else {
                isFirstTime = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())

        _binding = FragmentScanningBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hardwareAlert = HardwareAlert(requireActivity())
        init()
        binding.progressCircular.setProgressCompat(progress, true)
        binding.closeImage.setOnClickListener {
            showCancelAlert()
        }
        dialog = Dialog.create(requireContext())
    }

    override fun onResume() {
        super.onResume()
        turnFlashOn()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        SprenEventManager.unsubscribe(SprenEvent.STATE, ::stateListener)
        SprenEventManager.unsubscribe(SprenEvent.COMPLIANCE, ::complianceListener)
        SprenEventManager.unsubscribe(SprenEvent.PROGRESS, ::progressListener)
        sprenCapture?.stop()
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun init() {

        // MARK: - SprenCapture

        sprenCapture =
            SprenCapture(requireActivity(), binding.viewFinder.surfaceProvider)
        val cameraHasStarted = sprenCapture?.start()
        if (cameraHasStarted != null && cameraHasStarted) {
            print("SprenCapture started")

            // MARK: - Spren config

            SprenEventManager.unsubscribe(SprenEvent.STATE, ::stateListener)
                .subscribe(SprenEvent.STATE, ::stateListener)
            SprenEventManager.unsubscribe(SprenEvent.COMPLIANCE, ::complianceListener)
                .subscribe(SprenEvent.COMPLIANCE, ::complianceListener)
            SprenEventManager.unsubscribe(SprenEvent.PROGRESS, ::progressListener)
                .subscribe(SprenEvent.PROGRESS, ::progressListener)

            reset()
        } else {
            hardwareAlert.show()
        }
    }

    private fun reset() {
        readingState = ReadingState.PRE_READING
        progress = 0
        binding.percentageText.visibility = View.VISIBLE
        binding.checkImage.visibility = View.GONE
        showAlert = false
        sprenCapture?.reset()
    }

    // MARK: Spren state transitions

    private fun stateListener(values: HashMap<String, Any>) {
        if (view != null) {
            viewLifecycleOwner.lifecycleScope.async(context = Dispatchers.Main) { //replace it to Dispatchers.Main in real Android application
                when (values["state"]) {
                    SprenState.STARTED -> startState()
                    SprenState.FINISHED -> finishState()
                    SprenState.CANCELLED -> cancelState()
                    SprenState.ERROR -> errorState()
                    else -> return@async
                }
            }
        }
    }

    private fun complianceListener(values: HashMap<String, Any>) {

        if (view != null) {
            viewLifecycleOwner.lifecycleScope.async(context = Dispatchers.Main) {
                when (values["name"] as ComplianceCheck.Name) {
                    ComplianceCheck.Name.LENS_COVERAGE ->
                        handleLensCoverageCompliance()
                    ComplianceCheck.Name.EXPOSURE ->
                        handleExposureCompliance()
                    else ->
                        return@async
                }
            }
        }
    }

    private fun progressListener(values: HashMap<String, Any>) {
        if (view != null) {
            viewLifecycleOwner.lifecycleScope.async(context = Dispatchers.Main) {
                this@ScanningFragment.progress = values["progress"] as Int
            }
        }
    }

    private fun startState() {
        readingState = ReadingState.READING
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
        }
    }

    private fun finishState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
        }
        findNavController().navigate(R.id.action_ScanningFragment_to_CalculatingFragment)
    }

    private fun cancelState() {
        reset()
        SprenUI.Config.onCancel?.let {
            it.invoke()
            return
        }
        findNavController().navigate(R.id.action_ScanningFragment_to_GreetingFragment)
    }

    private fun errorState() = showErrorAlert()

    // MARK: - handle compliance checks

    private fun handleLensCoverageCompliance() {}

    private fun handleExposureCompliance() {}

    private fun updateProgressText() {

        val progressText = when (progress) {
            in 0..0 -> "Place your fingertip over the rear-facing camera lens."
            in 1..29 -> "Detecting your pulse. Keep your hand still and apply gentle pressure."
            in 30..49 -> "Measuring your heart rate. Please relax and hold still."
            in 50..69 -> "Scanning in progress. Please hold still."
            in 70..84 -> "Measuring your heart rate. Please hold still."
            in 85..99 -> "Almost there."
            else -> "Measurement complete!"
        }
        binding.progressText.text = progressText
    }

    // MARK: - user input

    private fun turnFlashOn() = sprenCapture?.turnFlashOn()

    // MARK: - alerts

    private fun showErrorAlert() {
        alertTitle = "Please fully cover the camera lens"
        val alertMessage =
            "Please hold gentle pressure on the camera lens throughout the entire measurement."
        val alertPrimaryButtonText = "Try again"
        val alertOnPrimaryButtonTap: ((View) -> Unit) = { _ ->
            dialog.dismiss()
            reset()
        }
        val alertSecondaryButtonText = null
        val alertOnSecondaryButtonTap = null
        Dialog.setUp(
            requireContext(),
            dialog,
            alertTitle,
            alertMessage,
            alertPrimaryButtonText,
            alertSecondaryButtonText,
            alertOnPrimaryButtonTap,
            alertOnSecondaryButtonTap
        )
        dialog.show()
        showAlert = true
    }

    private fun showCancelAlert() {
        alertTitle = "Your measurement is not complete"
        val alertMessage = "Continue measurement in order to see your reading results."
        val alertPrimaryButtonText = "Stop measurement"
        val alertOnPrimaryButtonTap: ((View) -> Unit) = { _ ->
            dialog.dismiss()
            Spren.cancelReading()
        }
        val alertSecondaryButtonText = "Continue Measurement"
        val alertOnSecondaryButtonTap: ((View) -> Unit) = { _ ->
            dialog.dismiss()
            showAlert = false
        }
        Dialog.setUp(
            requireContext(),
            dialog,
            alertTitle,
            alertMessage,
            alertPrimaryButtonText,
            alertSecondaryButtonText,
            alertOnPrimaryButtonTap,
            alertOnSecondaryButtonTap
        )
        dialog.show()
        showAlert = true
    }
}