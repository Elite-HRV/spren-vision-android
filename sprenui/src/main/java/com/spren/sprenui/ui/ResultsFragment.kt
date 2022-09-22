package com.spren.sprenui.ui

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentResultsBinding
import kotlin.math.roundToInt

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.secondary_background)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.doneText.typeface = font
        binding.resultsText.typeface = font
        binding.recoveryScoreTitle.typeface = font
        binding.recoveryScoreSecondTitle.typeface = font
        binding.recoveryValue.typeface = font
        binding.recoveryText.typeface = font
        binding.ansScoreTitle.typeface = font
        binding.hrvScoreValue.typeface = font
        binding.hrScoreValue.typeface = font
        binding.respirationScoreValue.typeface = font
        binding.faqTitleText.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.recoveryScoreText.typeface = font
        binding.recoveryLeftText.typeface = font
        binding.recoveryRightText.typeface = font
        binding.ansScoreText.typeface = font
        binding.ansScoreSympathetic.typeface = font
        binding.ansScoreParasympathetic.typeface = font
        binding.hrvScoreTitle.typeface = font
        binding.hrScoreTitle.typeface = font
        binding.respirationScoreTitle.typeface = font
        binding.firstQuestionText.typeface = font
        binding.secondQuestionText.typeface = font
        binding.thirdQuestionText.typeface = font
        val openHRVInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    R.string.hrv_score_bar_title,
                    R.string.hrv_score_title,
                    R.string.hrv_score_first_text,
                    0,
                    0
                )
            findNavController().navigate(direction)
        }
        /*binding.firstQuestionText.setOnClickListener { openHRVInformation.invoke() }
        binding.firstQuestionImage.setOnClickListener { openHRVInformation.invoke() }*/
        binding.hrvScoreCard.setOnClickListener { openHRVInformation.invoke() }
        val openRecoveryInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    R.string.recovery_bar_title,
                    0,
                    R.string.recovery_first_text,
                    0,
                    R.drawable.ic_metric
                )
            findNavController().navigate(direction)
        }
        /*binding.secondQuestionText.setOnClickListener { openRecoveryInformation.invoke() }
        binding.secondQuestionImage.setOnClickListener { openRecoveryInformation.invoke() }*/
        binding.recoveryScoreImage.setOnClickListener { openRecoveryInformation.invoke() }
        binding.progressCircularBackground.progress = 70
        binding.progressCircularBackground.rotation = 126f
        binding.progressCircular.rotation = 234f
        val openANSInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    R.string.ans_balance_bar_title,
                    0,
                    R.string.ans_balance_first_text,
                    R.string.ans_balance_second_text,
                    R.drawable.ic_ans_balance
                )
            findNavController().navigate(direction)
        }
        /*binding.thirdQuestionText.setOnClickListener { openANSInformation.invoke() }
        binding.thirdQuestionImage.setOnClickListener { openANSInformation.invoke() }*/
        binding.ansScoreImage.setOnClickListener { openANSInformation.invoke() }
        val openHRInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    R.string.heart_rate_bar_title,
                    R.string.heart_rate_title,
                    R.string.heart_rate_first_text,
                    0,
                    0
                )
            findNavController().navigate(direction)
        }
        binding.hrScoreCard.setOnClickListener { openHRInformation.invoke() }
        val openRespirationInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    R.string.respiration_rate_bar_title,
                    R.string.respiration_rate_title,
                    R.string.respiration_rate_first_text,
                    0,
                    0
                )
            findNavController().navigate(direction)
        }
        binding.respirationScoreCard.setOnClickListener { openRespirationInformation.invoke() }
        var hr = 0f
        var hrvScore = 0f
        val rmssd = 0f
        var breathingRate = 0f
        var readiness = 0f
        var ansBalance = 0f
        var signalQuality = 0f
        arguments?.let {
            hr = ResultsFragmentArgs.fromBundle(it).hr
            hrvScore = ResultsFragmentArgs.fromBundle(it).hrvScore
            breathingRate = ResultsFragmentArgs.fromBundle(it).breathingRate
            binding.hrScoreValue.text = hr.roundToInt().toString()
            binding.hrvScoreValue.text = hrvScore.roundToInt().toString()
            binding.respirationScoreValue.text = breathingRate.roundToInt().toString()
            readiness = ResultsFragmentArgs.fromBundle(it).readiness
            if (readiness != 0f) {
                val readinessRounded = readiness.roundToInt()
                binding.recoveryValue.text = readinessRounded.toString()
                binding.progressCircular.progress = (readiness / 10 * 70).toInt()
                val scoreSecondTitle = arrayListOf(
                    "Prioritize Recovery",
                    "Give yourself a break",
                    "It's all about balance",
                    "Take it easier today",
                    "Great day to check in",
                    "Listen to your body",
                    "Do what feels good",
                    "Going strong",
                    "How are you feeling?",
                    "Bring it on!",
                    "Today’s your day",
                    "Remarkable Recovery"
                )
                val scoreText = arrayListOf(
                    "Your recovery is low today. Focus on active recovery, hydration, supportive nutrition, and a solid night's sleep.",
                    "Your nervous system activity is abnormally elevated. Overtraining, or under recovering, negatively impacts your performance. Rest to restore.",
                    "Your recovery is abnormally low. Allowing yourself adequate recovery time will lead to increased performance. Prioritize rest and recovery today.",
                    "Your HRV balance indicates your body is responding to more stress today. Focus on lower intensity exercises and prioritize recovery.",
                    "Your body's nervous system activity is elevated. It's critical to pay attention to both how you feel and your body's response to your workouts.",
                    "You have lower recovery than normal. Focus today on active recovery which helps reduce muscle soreness, improve flexibility and other physiological factors.",
                    "Your overall recovery is good. Keep it up! Healthy sleep, nutrition, and mental wellness are critical in preventing overtraining.",
                    "Your recovery was good but with slightly elevated nervous system activity. Today is a good day to focus on form over intensity to not overdo it.",
                    "Your overall recovery is at a nice level. Listen to your body, and do what feels good today!",
                    "Your HRV balance indicates that you have recovered well and are ready for today’s training with moderate to high intensity.",
                    "Compared to your recent baseline, your nervous system is well balanced. Tackle your workout based on how you feel.",
                    "You are well recovered! Your HRV balance indicates you can give it your all and will likely to recover quickly. Make today awesome!"
                )
                when (readinessRounded) {
                    in 1..3 -> {
                        binding.progressCircular.setIndicatorColor(requireContext().getColor(R.color.progress_red))
                        binding.recoveryText.text = "Poor"
                        val random = (1..3).random()
                        binding.recoveryScoreSecondTitle.text = scoreSecondTitle[random - 1]
                        binding.recoveryScoreText.text = scoreText[random - 1]

                    }
                    in 4..6 -> {
                        binding.progressCircular.setIndicatorColor(requireContext().getColor(R.color.progress_amber))
                        binding.recoveryText.text = "Pay attention"
                        val random = (4..6).random()
                        binding.recoveryScoreSecondTitle.text = scoreSecondTitle[random - 1]
                        binding.recoveryScoreText.text = scoreText[random - 1]
                    }
                    in 7..8 -> {
                        binding.progressCircular.setIndicatorColor(requireContext().getColor(R.color.progress_green))
                        binding.recoveryText.text = "Good"
                        val random = (7..9).random()
                        binding.recoveryScoreSecondTitle.text = scoreSecondTitle[random - 1]
                        binding.recoveryScoreText.text = scoreText[random - 1]
                    }
                    else -> {
                        binding.progressCircular.setIndicatorColor(requireContext().getColor(R.color.progress_green))
                        binding.recoveryText.text = "Optimal"
                        val random = (10..12).random()
                        binding.recoveryScoreSecondTitle.text = scoreSecondTitle[random - 1]
                        binding.recoveryScoreText.text = scoreText[random - 1]
                    }
                }
            } else {
                binding.resultsText.visibility = View.VISIBLE
                binding.recoveryScoreTitle.text = "Readiness"
                binding.recoveryValue.text = "N/A"
                binding.recoveryValue.typeface = font
                binding.recoveryValue.textSize = 50f
                val params =
                    binding.recoveryValue.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(
                    0, (TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40f,
                        resources.displayMetrics
                    )).toInt(), 0, 0
                )
                binding.recoveryValue.layoutParams = params
                binding.recoveryScoreSecondTitle.text = "Build your baseline"
                binding.recoveryScoreText.text =
                    "You haven’t taken enough readings recently to generate your personal baseline. Take one more HRV Readiness reading tomorrow to receive a Readiness score and guidance."
            }
            ansBalance = ResultsFragmentArgs.fromBundle(it).ansBalance
            if (ansBalance != 0f) {
                when (ansBalance.roundToInt()) {
                    1 -> {
                        binding.ansScoreIndicator.setColorFilter(requireContext().getColor(R.color.progress_red))
                        binding.ansScoreText.text =
                            "Your Sympathetic Nervous System is more strongly engaged, meaning your body is experiencing deeper levels of stress or fatigue."
                    }
                    2 -> {
                        binding.ansScoreIndicator.setColorFilter(requireContext().getColor(R.color.yellow))
                        binding.ansScoreText.text =
                            "Your Sympathetic Nervous System is more engaged, indicating greater than usual stress (from all sources)."
                    }
                    3 -> {
                        binding.ansScoreIndicator.setColorFilter(requireContext().getColor(R.color.progress_green))
                        binding.ansScoreText.text =
                            "Your autonomic nervous system is relatively balanced today, meaning you are more rested and recovered."
                    }
                    4 -> {
                        binding.ansScoreIndicator.setColorFilter(requireContext().getColor(R.color.yellow))
                        binding.ansScoreText.text =
                            "Your Parasympathetic Nervous System is more engaged indicating your recovery activity is slightly elevated in response to recent stressors."
                    }
                    else -> {
                        binding.ansScoreIndicator.setColorFilter(requireContext().getColor(R.color.progress_red))
                        binding.ansScoreText.text =
                            "Your Parasympathetic Nervous System is strongly engaged indicating excessive recovery activity, often in response to cumulative stress or elevated immune system activity."
                    }
                }
                binding.ansScoreScale.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val width: Int = binding.ansScoreScale.width
                        val margin = (width * (ansBalance / 5)).toInt()

                        val half = if (ansBalance < 0.2) 0f else if (ansBalance > 4.8) 11f else 5.5f
                        val indicatorMargin = margin - (TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            half,
                            resources.displayMetrics
                        )).toInt()
                        val params =
                            binding.ansScoreIndicator.layoutParams as ConstraintLayout.LayoutParams
                        params.setMargins(
                            indicatorMargin, (TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                12f,
                                resources.displayMetrics
                            )).toInt(), 0, 0
                        )
                        binding.ansScoreIndicator.layoutParams = params
                        binding.ansScoreScale.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            } else {
                binding.ansScoreCard.visibility = View.GONE
            }
            signalQuality = ResultsFragmentArgs.fromBundle(it).signalQuality
        }
        binding.doneText.setOnClickListener {
            SprenUI.Config.onFinish?.let {
                it.invoke(
                    SprenUI.LatestRequest.guid,
                    hr,
                    hrvScore,
                    rmssd,
                    breathingRate,
                    if (readiness != 0f) readiness else null,
                    if (ansBalance != 0f) ansBalance else null,
                    signalQuality
                )
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ResultsFragment_to_MeasureHRVHomeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}