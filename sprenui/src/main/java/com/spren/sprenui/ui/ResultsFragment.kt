package com.spren.sprenui.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentResultsBinding
import com.spren.sprenui.network.model.ResultsFingerCamera
import com.spren.sprenui.util.Age
import com.spren.sprenui.util.InformationScreenType
import java.time.ZoneId
import java.util.*
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
        val openRecoveryInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    InformationScreenType.RECOVERY,
                    0
                )
            findNavController().navigate(direction)
        }
        binding.recoveryScoreImage.setOnClickListener { openRecoveryInformation.invoke() }
        binding.progressCircularBackground.progress = 70
        binding.progressCircularBackground.rotation = 126f
        binding.progressCircular.rotation = 234f
        val openANSInformation = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                    InformationScreenType.ANS,
                    0
                )
            findNavController().navigate(direction)
        }
        binding.ansScoreImage.setOnClickListener { openANSInformation.invoke() }
        var hr = 0f
        var hrvScore = 0f
        val rmssd = 0f
        var breathingRate = 0f
        var readiness = 0f
        var ansBalance = 0f
        var signalQuality = 0f

        val openFirstQuestion = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToFaqFragment(
                    R.string.what_is_hrv,
                    R.string.what_is_hrv_answer,
                    R.string.what_does_it_matter,
                    0,
                    R.string.what_does_it_matter_answer
                )
            findNavController().navigate(direction)
        }
        binding.firstQuestionText.setOnClickListener { openFirstQuestion.invoke() }
        binding.firstQuestionImage.setOnClickListener { openFirstQuestion.invoke() }

        val openSecondQuestion = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToFaqFragment(
                    R.string.best_results,
                    R.string.best_results_answer_1,
                    0,
                    R.string.best_results_subtitle,
                    R.string.best_results_answer_2
                )
            findNavController().navigate(direction)
        }
        binding.secondQuestionText.setOnClickListener { openSecondQuestion.invoke() }
        binding.secondQuestionImage.setOnClickListener { openSecondQuestion.invoke() }

        val openThirdQuestion = {
            val direction =
                ResultsFragmentDirections.actionResultsFragmentToFaqFragment(
                    R.string.how_often,
                    R.string.how_often_answer,
                    0,
                    0,
                    0
                )
            findNavController().navigate(direction)
        }
        binding.thirdQuestionText.setOnClickListener { openThirdQuestion.invoke() }
        binding.thirdQuestionImage.setOnClickListener { openThirdQuestion.invoke() }

        arguments?.let {
            hr = ResultsFragmentArgs.fromBundle(it).hr
            hrvScore = ResultsFragmentArgs.fromBundle(it).hrvScore
            breathingRate = ResultsFragmentArgs.fromBundle(it).breathingRate
            val hrRounded = hr.roundToInt()
            val hrvScoreRounded = hrvScore.roundToInt()
            val breathingRateRounded = breathingRate.roundToInt()
            val openHRVInformation = {
                val direction =
                    ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                        InformationScreenType.HRV,
                        hrvScoreRounded
                    )
                findNavController().navigate(direction)
            }
            binding.hrvScoreCard.setOnClickListener { openHRVInformation.invoke() }
            binding.hrvFirstReadingScoreCard.setOnClickListener { openHRVInformation.invoke() }
            val openHRInformation = {
                val direction =
                    ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                        InformationScreenType.HR,
                        hrRounded
                    )
                findNavController().navigate(direction)
            }
            binding.hrScoreCard.setOnClickListener { openHRInformation.invoke() }
            binding.hrFirstReadingScoreCard.setOnClickListener { openHRInformation.invoke() }
            val openRespirationInformation = {
                val direction =
                    ResultsFragmentDirections.actionResultsFragmentToInformationFragment(
                        InformationScreenType.RESPIRATION_RATE,
                        breathingRateRounded
                    )
                findNavController().navigate(direction)
            }
            binding.respirationScoreCard.setOnClickListener { openRespirationInformation.invoke() }
            binding.respirationFirstReadingScoreCard.setOnClickListener { openRespirationInformation.invoke() }
            readiness = ResultsFragmentArgs.fromBundle(it).readiness
            if (readiness != 0f) {
                val sharedPreference =
                    requireActivity().getSharedPreferences("READING", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("subsequentReadingFlow", true)
                editor.apply()
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
                binding.hrScoreValue.text = hrRounded.toString()
                binding.hrvScoreValue.text = hrvScoreRounded.toString()
                binding.respirationScoreValue.text = breathingRateRounded.toString()
                binding.hrvFirstReadingScoreCard.visibility = View.GONE
                binding.hrFirstReadingScoreCard.visibility = View.GONE
                binding.respirationFirstReadingScoreCard.visibility = View.GONE
            } else {
                binding.recoveryScoreTitle.text = "Readiness"
                binding.recoveryValue.text = "N/A"
                val font =
                    Typeface.createFromAsset(activity?.assets, "roboto_regular.ttf")
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
                binding.hrvScoreCard.visibility = View.GONE
                binding.hrScoreCard.visibility = View.GONE
                binding.respirationScoreCard.visibility = View.GONE
                binding.hrFirstReadingScoreValue.text = hrRounded.toString()
                binding.hrvFirstReadingScoreValue.text = hrvScoreRounded.toString()
                binding.respirationFirstReadingScoreValue.text =
                    breathingRateRounded.toString()

                binding.hrvFirstReadingScoreProgressCircular.progress = 100
                binding.hrFirstReadingScoreProgressCircular.progress = 100
                binding.respirationFirstReadingScoreProgressCircular.progress = 100

                // hrv setup
                if (SprenUI.Config.userGender != null && SprenUI.Config.userGender != SprenUI.BiologicalSex.OTHER && SprenUI.Config.userBirthdate != null) {
                    val age = Age.calculate(
                        SprenUI.Config.userBirthdate!!.toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate(),
                        Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    )
                    if (SprenUI.Config.userGender == SprenUI.BiologicalSex.FEMALE) {
                        when (age) {
                            in 18..29 -> {
                                when (hrvScoreRounded) {
                                    in 74..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 71..73 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 65..70 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 59..64 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 51..58 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 30..39 -> {
                                when (hrvScoreRounded) {
                                    in 73..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 68..72 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 62..67 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 55..61 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 49..54 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 40..49 -> {
                                when (hrvScoreRounded) {
                                    in 69..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 64..68 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 58..63 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 51..57 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 44..50 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 50..59 -> {
                                when (hrvScoreRounded) {
                                    in 68..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 62..67 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 56..61 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 51..55 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 46..50 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 60..69 -> {
                                when (hrvScoreRounded) {
                                    in 65..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 60..64 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 54..59 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 48..53 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 41..47 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            else -> {
                                when (hrvScoreRounded) {
                                    in 65..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 60..64 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 52..59 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 43..51 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 38..42 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        when (age) {
                            in 18..29 -> {
                                when (hrvScoreRounded) {
                                    in 76..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 72..75 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 67..71 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 61..66 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 54..60 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 30..39 -> {
                                when (hrvScoreRounded) {
                                    in 73..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 69..72 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 64..68 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 58..63 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 51..57 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 40..49 -> {
                                when (hrvScoreRounded) {
                                    in 71..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 66..70 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 60..65 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 54..59 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 48..53 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 50..59 -> {
                                when (hrvScoreRounded) {
                                    in 68..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 63..67 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 57..62 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 51..56 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 45..50 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            in 60..69 -> {
                                when (hrvScoreRounded) {
                                    in 67..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 61..66 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 55..60 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 48..54 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 42..47 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                            else -> {
                                when (hrvScoreRounded) {
                                    in 69..100 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.athlete
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 63..68 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.very_good
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 55..62 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.above_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Better than average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 47..54 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.athlete
                                        )
                                    }
                                    in 40..46 -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.below_average
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                    else -> {
                                        binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                            requireContext().getColor(
                                                R.color.poor
                                            )
                                        )
                                        binding.hrvFirstReadingScoreChip.text =
                                            "Below average for your age and gender"
                                        binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(
                                            R.color.below_average
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    when (hrvScoreRounded) {
                        in 72..100 -> {
                            binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.athlete
                                )
                            )
                            binding.hrvFirstReadingScoreChip.text =
                                "Better than the population average"
                            binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(R.color.athlete)
                        }
                        in 66..71 -> {
                            binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.very_good
                                )
                            )
                            binding.hrvFirstReadingScoreChip.text =
                                "Better than the population average"
                            binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(R.color.athlete)
                        }
                        in 60..65 -> {
                            binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.above_average
                                )
                            )
                            binding.hrvFirstReadingScoreChip.text =
                                "Better than the population average"
                            binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(R.color.athlete)
                        }
                        in 53..59 -> {
                            binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.average
                                )
                            )
                            binding.hrvFirstReadingScoreChip.text =
                                "Average compared to population"
                            binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(R.color.athlete)
                        }
                        in 46..52 -> {
                            binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            binding.hrvFirstReadingScoreChip.text =
                                "Below the population average"
                            binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(R.color.below_average)
                        }
                        else -> {
                            binding.hrvFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.poor
                                )
                            )
                            binding.hrvFirstReadingScoreChip.text =
                                "Below the population average"
                            binding.hrvFirstReadingScoreChip.setChipBackgroundColorResource(R.color.below_average)
                        }
                    }
                }

                // HR Configuration
                if (SprenUI.Config.userGender != null && SprenUI.Config.userBirthdate != null) {
                    val age = Age.calculate(
                        SprenUI.Config.userBirthdate!!.toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate(),
                        Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    )
                    when (SprenUI.Config.userGender) {
                        SprenUI.BiologicalSex.FEMALE -> {
                            when (age) {
                                in 16..19 -> {
                                    when (hrRounded) {
                                        in 50..61 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 62..68 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 69..76 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 77..84 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 85..93 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                in 20..39 -> {
                                    when (hrRounded) {
                                        in 52..59 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 60..65 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 66..73 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 74..81 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 82..88 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                in 40..59 -> {
                                    when (hrRounded) {
                                        in 51..58 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 59..63 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 64..70 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 71..78 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 79..85 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    when (hrRounded) {
                                        in 52..58 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 59..63 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 64..69 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 70..77 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 78..85 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        SprenUI.BiologicalSex.MALE -> {
                            when (age) {
                                in 16..19 -> {
                                    when (hrRounded) {
                                        in 46..55 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 56..60 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 61..68 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 69..77 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 78..86 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                in 20..39 -> {
                                    when (hrRounded) {
                                        in 47..54 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 55..60 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 61..68 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 69..75 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 76..83 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                in 40..59 -> {
                                    when (hrRounded) {
                                        in 46..54 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 55..60 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 61..67 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 68..76 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 77..84 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    when (hrRounded) {
                                        in 45..53 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 54..59 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 60..66 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 67..74 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 75..83 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            when (age) {
                                in 16..19 -> {
                                    when (hrRounded) {
                                        in 47..57 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 58..63 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 64..72 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 73..81 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 82..89 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                in 20..39 -> {
                                    when (hrRounded) {
                                        in 47..56 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 57..63 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 64..70 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 71..78 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 79..86 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                in 40..59 -> {
                                    when (hrRounded) {
                                        in 46..56 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 57..61 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 62..69 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 70..77 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 78..85 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    when (hrRounded) {
                                        in 46..56 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 57..61 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 62..69 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Better than average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 70..76 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.athlete
                                            )
                                        }
                                        in 77..85 -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.below_average
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                        else -> {
                                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrFirstReadingScoreChip.text =
                                                "Below average for your age and gender"
                                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                                R.color.below_average
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    when (hrRounded) {
                        in 47..56 -> {
                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.athlete
                                )
                            )
                            binding.hrFirstReadingScoreChip.text =
                                "Better than the population average"
                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                R.color.athlete
                            )
                        }
                        in 57..63 -> {
                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.very_good
                                )
                            )
                            binding.hrFirstReadingScoreChip.text =
                                "Better than the population average"
                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                R.color.athlete
                            )
                        }
                        in 64..70 -> {
                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.above_average
                                )
                            )
                            binding.hrFirstReadingScoreChip.text =
                                "Better than the population average"
                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                R.color.athlete
                            )
                        }
                        in 71..78 -> {
                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.average
                                )
                            )
                            binding.hrFirstReadingScoreChip.text =
                                "Average compared to population"
                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                R.color.athlete
                            )
                        }
                        in 79..86 -> {
                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            binding.hrFirstReadingScoreChip.text =
                                "Below the population average"
                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                R.color.below_average
                            )
                        }
                        else -> {
                            binding.hrFirstReadingScoreProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.poor
                                )
                            )
                            binding.hrFirstReadingScoreChip.text =
                                "Below the population average"
                            binding.hrFirstReadingScoreChip.setChipBackgroundColorResource(
                                R.color.below_average
                            )
                        }
                    }
                }

                // respiration setup
                when (breathingRateRounded) {
                    in 0..11 -> {
                        binding.respirationFirstReadingScoreProgressCircular.setIndicatorColor(
                            requireContext().getColor(
                                R.color.below_average
                            )
                        )
                        binding.respirationFirstReadingScoreChip.text =
                            "Abnormally low for healthy adults"
                        binding.respirationFirstReadingScoreChip.setChipBackgroundColorResource(
                            R.color.below_average
                        )
                    }
                    in 12..20 -> {
                        binding.respirationFirstReadingScoreProgressCircular.setIndicatorColor(
                            requireContext().getColor(
                                R.color.athlete
                            )
                        )
                        binding.respirationFirstReadingScoreChip.text =
                            "Normal for healthy adults"
                        binding.respirationFirstReadingScoreChip.setChipBackgroundColorResource(
                            R.color.athlete
                        )
                    }
                    else -> {
                        binding.respirationFirstReadingScoreProgressCircular.setIndicatorColor(
                            requireContext().getColor(
                                R.color.below_average
                            )
                        )
                        binding.respirationFirstReadingScoreChip.text =
                            "Abnormally high for healthy adults"
                        binding.respirationFirstReadingScoreChip.setChipBackgroundColorResource(
                            R.color.below_average
                        )
                    }
                }
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
                    ResultsFingerCamera(
                        SprenUI.LatestRequest.guid,
                        hr,
                        hrvScore,
                        rmssd,
                        breathingRate,
                        if (readiness != 0f) readiness else null,
                        if (ansBalance != 0f) ansBalance else null,
                        signalQuality
                    ), null
                )
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ResultsFragment_to_GreetingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}