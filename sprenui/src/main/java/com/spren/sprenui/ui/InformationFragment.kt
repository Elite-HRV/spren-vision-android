package com.spren.sprenui.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentInformationBinding
import com.spren.sprenui.util.Age
import com.spren.sprenui.util.InformationScreenType
import java.time.ZoneId
import java.util.*

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val type = InformationFragmentArgs.fromBundle(it).type
            val value = InformationFragmentArgs.fromBundle(it).value

            when (type) {
                InformationScreenType.RECOVERY -> {
                    binding.barText.text = getString(R.string.recovery_bar_title)
                    binding.titleText.text = getString(R.string.recovery_bar_title)
                    binding.firstText.text = getString(R.string.recovery_first_text)
                    binding.image.setImageResource(R.drawable.ic_metric)
                }
                InformationScreenType.HRV -> {
                    binding.barText.text = getString(R.string.hrv_score_bar_title)
                    binding.titleText.text = getString(R.string.hrv_score_title)
                    binding.firstText.text = getString(R.string.hrv_score_first_text)

                    binding.hrvRateCard.visibility = View.VISIBLE
                    binding.hrvRateProgressCircular.progress = 100
                    binding.hrvRateScoreValue.text = value.toString()
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.body)
                    constraintSet.connect(
                        R.id.title_text,
                        ConstraintSet.TOP,
                        R.id.hrv_rate_card,
                        ConstraintSet.BOTTOM,
                        24
                    )
                    constraintSet.applyTo(binding.body)

                    if (SprenUI.Config.userGender != null && SprenUI.Config.userGender != SprenUI.BiologicalSex.OTHER && SprenUI.Config.userBirthdate != null) {
                        val age = Age.calculate(
                            SprenUI.Config.userBirthdate!!.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                            Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                        if (SprenUI.Config.userGender == SprenUI.BiologicalSex.FEMALE) {
                            when (age) {
                                in 18..29 -> {
                                    binding.hrvRateScoreAthleteValue.text = "74-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "71-73"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "65-70"
                                    binding.hrvRateScoreAverageValue.text = "59-64"
                                    binding.hrvRateScoreBelowAverageValue.text = "51-58"
                                    binding.hrvRateScorePoorValue.text = "1-50"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for women 18-29"
                                    when (value) {
                                        in 74..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for women your age."
                                        }
                                        in 71..73 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for women your age."
                                        }
                                        in 65..70 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for women your age."
                                        }
                                        in 59..64 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for women your age."
                                        }
                                        in 51..58 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for women your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for women your age."
                                        }
                                    }
                                }
                                in 30..39 -> {
                                    binding.hrvRateScoreAthleteValue.text = "73-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "68-72"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "62-67"
                                    binding.hrvRateScoreAverageValue.text = "55-61"
                                    binding.hrvRateScoreBelowAverageValue.text = "49-54"
                                    binding.hrvRateScorePoorValue.text = "1-48"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for women 30-39"
                                    when (value) {
                                        in 73..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for women your age."
                                        }
                                        in 68..72 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for women your age."
                                        }
                                        in 62..67 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for women your age."
                                        }
                                        in 55..61 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for women your age."
                                        }
                                        in 49..54 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for women your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for women your age."
                                        }
                                    }
                                }
                                in 40..49 -> {
                                    binding.hrvRateScoreAthleteValue.text = "69-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "64-68"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "58-63"
                                    binding.hrvRateScoreAverageValue.text = "51-57"
                                    binding.hrvRateScoreBelowAverageValue.text = "44-40"
                                    binding.hrvRateScorePoorValue.text = "1-43"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for women 40-49"
                                    when (value) {
                                        in 69..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for women your age."
                                        }
                                        in 64..68 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for women your age."
                                        }
                                        in 58..63 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for women your age."
                                        }
                                        in 51..57 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for women your age."
                                        }
                                        in 44..50 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for women your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for women your age."
                                        }
                                    }
                                }
                                in 50..59 -> {
                                    binding.hrvRateScoreAthleteValue.text = "68-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "62-67"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "56-61"
                                    binding.hrvRateScoreAverageValue.text = "51-55"
                                    binding.hrvRateScoreBelowAverageValue.text = "46-50"
                                    binding.hrvRateScorePoorValue.text = "1-45"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for women 50-59"
                                    when (value) {
                                        in 68..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for women your age."
                                        }
                                        in 62..67 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for women your age."
                                        }
                                        in 56..61 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for women your age."
                                        }
                                        in 51..55 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for women your age."
                                        }
                                        in 46..50 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for women your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for women your age."
                                        }
                                    }
                                }
                                in 60..69 -> {
                                    binding.hrvRateScoreAthleteValue.text = "65-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "60-64"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "54-59"
                                    binding.hrvRateScoreAverageValue.text = "48-53"
                                    binding.hrvRateScoreBelowAverageValue.text = "41-47"
                                    binding.hrvRateScorePoorValue.text = "1-40"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for women 60-69"
                                    when (value) {
                                        in 65..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for women your age."
                                        }
                                        in 60..64 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for women your age."
                                        }
                                        in 54..59 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for women your age."
                                        }
                                        in 48..53 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for women your age."
                                        }
                                        in 41..47 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for women your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for women your age."
                                        }
                                    }
                                }
                                else -> {
                                    binding.hrvRateScoreAthleteValue.text = "65-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "60-64"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "52-59"
                                    binding.hrvRateScoreAverageValue.text = "43-51"
                                    binding.hrvRateScoreBelowAverageValue.text = "38-42"
                                    binding.hrvRateScorePoorValue.text = "1-37"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for women 70+"
                                    when (value) {
                                        in 65..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for women your age."
                                        }
                                        in 60..64 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for women your age."
                                        }
                                        in 52..59 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for women your age."
                                        }
                                        in 43..51 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for women your age."
                                        }
                                        in 38..42 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for women your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for women your age."
                                        }
                                    }
                                }
                            }
                        } else {
                            when (age) {
                                in 18..29 -> {
                                    binding.hrvRateScoreAthleteValue.text = "76-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "72-75"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "67-71"
                                    binding.hrvRateScoreAverageValue.text = "61-66"
                                    binding.hrvRateScoreBelowAverageValue.text = "54-60"
                                    binding.hrvRateScorePoorValue.text = "1-53"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for men 18-29"
                                    when (value) {
                                        in 76..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for men your age."
                                        }
                                        in 72..75 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for men your age."
                                        }
                                        in 67..71 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for men your age."
                                        }
                                        in 61..66 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for men your age."
                                        }
                                        in 54..60 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for men your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for men your age."
                                        }
                                    }
                                }
                                in 30..39 -> {
                                    binding.hrvRateScoreAthleteValue.text = "73-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "69-72"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "64-68"
                                    binding.hrvRateScoreAverageValue.text = "58-63"
                                    binding.hrvRateScoreBelowAverageValue.text = "51-57"
                                    binding.hrvRateScorePoorValue.text = "1-50"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for men 30-39"
                                    when (value) {
                                        in 73..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for men your age."
                                        }
                                        in 69..72 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for men your age."
                                        }
                                        in 64..68 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for men your age."
                                        }
                                        in 58..63 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for men your age."
                                        }
                                        in 51..57 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for men your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for men your age."
                                        }
                                    }
                                }
                                in 40..49 -> {
                                    binding.hrvRateScoreAthleteValue.text = "71-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "66-70"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "60-65"
                                    binding.hrvRateScoreAverageValue.text = "54-59"
                                    binding.hrvRateScoreBelowAverageValue.text = "48-53"
                                    binding.hrvRateScorePoorValue.text = "1-47"
                                    binding.hrvRateScoreRanges.text =
                                        "Heart rate ranges for men 40-49"
                                    when (value) {
                                        in 71..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for men your age."
                                        }
                                        in 66..70 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for men your age."
                                        }
                                        in 60..65 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for men your age."
                                        }
                                        in 54..59 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for men your age."
                                        }
                                        in 48..53 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for men your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for men your age."
                                        }
                                    }
                                }
                                in 50..59 -> {
                                    binding.hrvRateScoreAthleteValue.text = "68-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "62-67"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "56-61"
                                    binding.hrvRateScoreAverageValue.text = "51-55"
                                    binding.hrvRateScoreBelowAverageValue.text = "46-50"
                                    binding.hrvRateScorePoorValue.text = "1-45"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for men 50-59"
                                    when (value) {
                                        in 68..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for men your age."
                                        }
                                        in 62..67 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for men your age."
                                        }
                                        in 56..61 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for men your age."
                                        }
                                        in 51..55 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for men your age."
                                        }
                                        in 46..50 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for men your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for men your age."
                                        }
                                    }
                                }
                                in 60..69 -> {
                                    binding.hrvRateScoreAthleteValue.text = "65-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "60-64"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "54-59"
                                    binding.hrvRateScoreAverageValue.text = "48-53"
                                    binding.hrvRateScoreBelowAverageValue.text = "41-47"
                                    binding.hrvRateScorePoorValue.text = "1-40"
                                    binding.hrvRateScoreRanges.text =
                                        "HRV levels for men 60-69"
                                    when (value) {
                                        in 65..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for men your age."
                                        }
                                        in 60..64 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for men your age."
                                        }
                                        in 54..59 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for men your age."
                                        }
                                        in 48..53 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for men your age."
                                        }
                                        in 41..47 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for men your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for men your age."
                                        }
                                    }
                                }
                                else -> {
                                    binding.hrvRateScoreAthleteValue.text = "65-100"
                                    binding.hrvRateScoreVeryGoodValue.text = "60-64"
                                    binding.hrvRateScoreBetterThanAverageValue.text = "52-59"
                                    binding.hrvRateScoreAverageValue.text = "43-51"
                                    binding.hrvRateScoreBelowAverageValue.text = "38-42"
                                    binding.hrvRateScorePoorValue.text = "1-37"
                                    binding.hrvRateScoreRanges.text =
                                        "Heart rate ranges for men 70+"
                                    when (value) {
                                        in 65..100 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.athlete
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Athlete for men your age."
                                        }
                                        in 60..64 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.very_good
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Very Good for men your age."
                                        }
                                        in 52..59 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.above_average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Above Average for men your age."
                                        }
                                        in 43..51 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Average for men your age."
                                        }
                                        in 38..42 -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.average
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Below Average for men your age."
                                        }
                                        else -> {
                                            binding.hrvRateProgressCircular.setIndicatorColor(
                                                requireContext().getColor(
                                                    R.color.poor
                                                )
                                            )
                                            binding.hrvRateScoreText.text =
                                                "Your HRV Score of $value is Poor for men your age."
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        binding.hrvRateScoreAthleteValue.text = "72-100"
                        binding.hrvRateScoreVeryGoodValue.text = "66-71"
                        binding.hrvRateScoreBetterThanAverageValue.text = "60-65"
                        binding.hrvRateScoreAverageValue.text = "53-59"
                        binding.hrvRateScoreBelowAverageValue.text = "46-52"
                        binding.hrvRateScorePoorValue.text = "1-45"
                        binding.hrvRateScoreRanges.text =
                            "HRV levels for population average"
                        when (value) {
                            in 72..100 -> {
                                binding.hrvRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.athlete
                                    )
                                )
                                binding.hrvRateScoreText.text =
                                    "Your HRV Score of $value is Athlete compared to the population."
                            }
                            in 66..71 -> {
                                binding.hrvRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.very_good
                                    )
                                )
                                binding.hrvRateScoreText.text =
                                    "Your HRV Score of $value is Very Good compared to the population."
                            }
                            in 60..65 -> {
                                binding.hrvRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.above_average
                                    )
                                )
                                binding.hrvRateScoreText.text =
                                    "Your HRV Score of $value is Above Average compared to the population."
                            }
                            in 53..59 -> {
                                binding.hrvRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.average
                                    )
                                )
                                binding.hrvRateScoreText.text =
                                    "Your HRV Score of $value is Average compared to the population."
                            }
                            in 46..52 -> {
                                binding.hrvRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.average
                                    )
                                )
                                binding.hrvRateScoreText.text =
                                    "Your HRV Score of $value is Below Average compared to the population."
                            }
                            else -> {
                                binding.hrvRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.poor
                                    )
                                )
                                binding.hrvRateScoreText.text =
                                    "Your HRV Score of $value is Poor compared to the population."
                            }
                        }
                    }

                    binding.improveHrvCard.visibility = View.VISIBLE
                    binding.improveHrvShowText.text = "Show more"
                    binding.improveHrvShowTextImage.setImageResource(R.drawable.ic_arrow_bottom)
                    val collapseHrv = {
                        if (binding.improveHrvShowText.text == "Show more") {
                            binding.improveHrvShowText.text = "Show less"
                            binding.improveHrvShowTextImage.setImageResource(R.drawable.ic_arrow_top)
                            binding.collapsingImproveHrv.visibility = View.VISIBLE
                        } else {
                            binding.improveHrvShowText.text = "Show more"
                            binding.improveHrvShowTextImage.setImageResource(R.drawable.ic_arrow_bottom)
                            binding.collapsingImproveHrv.visibility = View.GONE
                        }
                    }
                    binding.improveHrvShowText.setOnClickListener { collapseHrv.invoke() }
                    binding.improveHrvShowTextImage.setOnClickListener { collapseHrv.invoke() }
                }
                InformationScreenType.HR -> {
                    binding.barText.text = getString(R.string.heart_rate_bar_title)
                    binding.titleText.text = getString(R.string.heart_rate_title)
                    binding.firstText.text = getString(R.string.heart_rate_first_text)

                    binding.hrRateCard.visibility = View.VISIBLE
                    binding.hrRateProgressCircular.progress = 100
                    binding.hrRateScoreValue.text = value.toString()
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.body)
                    constraintSet.connect(
                        R.id.title_text,
                        ConstraintSet.TOP,
                        R.id.hr_rate_card,
                        ConstraintSet.BOTTOM,
                        24
                    )
                    constraintSet.applyTo(binding.body)

                    if (SprenUI.Config.userGender != null && SprenUI.Config.userBirthdate != null) {
                        val age = Age.calculate(
                            SprenUI.Config.userBirthdate!!.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                            Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                        when (SprenUI.Config.userGender) {
                            SprenUI.BiologicalSex.FEMALE -> {
                                when (age) {
                                    in 16..19 -> {
                                        binding.hrRateScoreAthleteValue.text = "50-61"
                                        binding.hrRateScoreVeryGoodValue.text = "62-68"
                                        binding.hrRateScoreBetterThanAverageValue.text = "69-76"
                                        binding.hrRateScoreAverageValue.text = "77-84"
                                        binding.hrRateScoreBelowAverageValue.text = "85-93"
                                        binding.hrRateScorePoorValue.text = "94-102"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for women 16-19"
                                        when (value) {
                                            in 50..61 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for women your age."
                                            }
                                            in 62..68 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for women your age."
                                            }
                                            in 69..76 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for women your age."
                                            }
                                            in 77..84 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for women your age."
                                            }
                                            in 85..93 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for women your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for women your age."
                                            }
                                        }
                                    }
                                    in 20..39 -> {
                                        binding.hrRateScoreAthleteValue.text = "52-59"
                                        binding.hrRateScoreVeryGoodValue.text = "60-65"
                                        binding.hrRateScoreBetterThanAverageValue.text = "66-73"
                                        binding.hrRateScoreAverageValue.text = "74-81"
                                        binding.hrRateScoreBelowAverageValue.text = "82-88"
                                        binding.hrRateScorePoorValue.text = "89-98"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for women 20-39"
                                        when (value) {
                                            in 52..59 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for women your age."
                                            }
                                            in 60..65 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for women your age."
                                            }
                                            in 66..73 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for women your age."
                                            }
                                            in 74..81 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for women your age."
                                            }
                                            in 82..88 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for women your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for women your age."
                                            }
                                        }
                                    }
                                    in 40..59 -> {
                                        binding.hrRateScoreAthleteValue.text = "51-58"
                                        binding.hrRateScoreVeryGoodValue.text = "59-63"
                                        binding.hrRateScoreBetterThanAverageValue.text = "64-70"
                                        binding.hrRateScoreAverageValue.text = "71-78"
                                        binding.hrRateScoreBelowAverageValue.text = "79-85"
                                        binding.hrRateScorePoorValue.text = "86-96"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for women 40-59"
                                        when (value) {
                                            in 51..58 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for women your age."
                                            }
                                            in 59..63 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for women your age."
                                            }
                                            in 64..70 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for women your age."
                                            }
                                            in 71..78 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for women your age."
                                            }
                                            in 79..85 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for women your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for women your age."
                                            }
                                        }
                                    }
                                    else -> {
                                        binding.hrRateScoreAthleteValue.text = "52-58"
                                        binding.hrRateScoreVeryGoodValue.text = "59-63"
                                        binding.hrRateScoreBetterThanAverageValue.text = "64-69"
                                        binding.hrRateScoreAverageValue.text = "70-77"
                                        binding.hrRateScoreBelowAverageValue.text = "78-85"
                                        binding.hrRateScorePoorValue.text = "86-95"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for women 60+"
                                        when (value) {
                                            in 52..58 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for women your age."
                                            }
                                            in 59..63 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for women your age."
                                            }
                                            in 64..69 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for women your age."
                                            }
                                            in 70..77 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for women your age."
                                            }
                                            in 78..85 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for women your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for women your age."
                                            }
                                        }
                                    }
                                }
                            }
                            SprenUI.BiologicalSex.MALE -> {
                                when (age) {
                                    in 16..19 -> {
                                        binding.hrRateScoreAthleteValue.text = "46-55"
                                        binding.hrRateScoreVeryGoodValue.text = "56-60"
                                        binding.hrRateScoreBetterThanAverageValue.text = "61-68"
                                        binding.hrRateScoreAverageValue.text = "69-77"
                                        binding.hrRateScoreBelowAverageValue.text = "78-86"
                                        binding.hrRateScorePoorValue.text = "87-94"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for men 16-19"
                                        when (value) {
                                            in 46..55 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for men your age."
                                            }
                                            in 56..60 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for men your age."
                                            }
                                            in 61..68 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for men your age."
                                            }
                                            in 69..77 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for men your age."
                                            }
                                            in 78..86 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for men your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for men your age."
                                            }
                                        }
                                    }
                                    in 20..39 -> {
                                        binding.hrRateScoreAthleteValue.text = "47-54"
                                        binding.hrRateScoreVeryGoodValue.text = "55-60"
                                        binding.hrRateScoreBetterThanAverageValue.text = "61-68"
                                        binding.hrRateScoreAverageValue.text = "69-75"
                                        binding.hrRateScoreBelowAverageValue.text = "76-83"
                                        binding.hrRateScorePoorValue.text = "84-94"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for men 20-39"
                                        when (value) {
                                            in 47..54 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for men your age."
                                            }
                                            in 55..60 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for men your age."
                                            }
                                            in 61..68 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for men your age."
                                            }
                                            in 69..75 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for men your age."
                                            }
                                            in 76..83 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for men your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for men your age."
                                            }
                                        }
                                    }
                                    in 40..59 -> {
                                        binding.hrRateScoreAthleteValue.text = "46-54"
                                        binding.hrRateScoreVeryGoodValue.text = "55-60"
                                        binding.hrRateScoreBetterThanAverageValue.text = "61-67"
                                        binding.hrRateScoreAverageValue.text = "68-76"
                                        binding.hrRateScoreBelowAverageValue.text = "77-84"
                                        binding.hrRateScorePoorValue.text = "85-94"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for men 40-59"
                                        when (value) {
                                            in 46..54 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for men your age."
                                            }
                                            in 55..60 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for men your age."
                                            }
                                            in 61..67 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for men your age."
                                            }
                                            in 68..76 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for men your age."
                                            }
                                            in 77..84 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for men your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for men your age."
                                            }
                                        }
                                    }
                                    else -> {
                                        binding.hrRateScoreAthleteValue.text = "45-53"
                                        binding.hrRateScoreVeryGoodValue.text = "54-59"
                                        binding.hrRateScoreBetterThanAverageValue.text = "60-66"
                                        binding.hrRateScoreAverageValue.text = "67-74"
                                        binding.hrRateScoreBelowAverageValue.text = "75-83"
                                        binding.hrRateScorePoorValue.text = "84-97"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for men 60+"
                                        when (value) {
                                            in 45..53 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for men your age."
                                            }
                                            in 54..59 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for men your age."
                                            }
                                            in 60..66 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for men your age."
                                            }
                                            in 67..74 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for men your age."
                                            }
                                            in 74..83 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for men your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for men your age."
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {
                                when (age) {
                                    in 16..19 -> {
                                        binding.hrRateScoreAthleteValue.text = "47-57"
                                        binding.hrRateScoreVeryGoodValue.text = "58-63"
                                        binding.hrRateScoreBetterThanAverageValue.text = "64-72"
                                        binding.hrRateScoreAverageValue.text = "73-81"
                                        binding.hrRateScoreBelowAverageValue.text = "82-89"
                                        binding.hrRateScorePoorValue.text = "90-100"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for non-gender specific 16-19"
                                        when (value) {
                                            in 47..57 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for non-gender specific your age."
                                            }
                                            in 58..63 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for non-gender specific your age."
                                            }
                                            in 64..72 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for non-gender specific your age."
                                            }
                                            in 73..81 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for non-gender specific your age."
                                            }
                                            in 82..89 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for non-gender specific your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for non-gender specific your age."
                                            }
                                        }
                                    }
                                    in 20..39 -> {
                                        binding.hrRateScoreAthleteValue.text = "47-56"
                                        binding.hrRateScoreVeryGoodValue.text = "57-63"
                                        binding.hrRateScoreBetterThanAverageValue.text = "64-70"
                                        binding.hrRateScoreAverageValue.text = "71-78"
                                        binding.hrRateScoreBelowAverageValue.text = "79-86"
                                        binding.hrRateScorePoorValue.text = "87-97"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for non-gender specific 20-39"
                                        when (value) {
                                            in 47..56 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for non-gender specific your age."
                                            }
                                            in 57..63 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for non-gender specific your age."
                                            }
                                            in 64..70 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for non-gender specific your age."
                                            }
                                            in 71..78 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for non-gender specific your age."
                                            }
                                            in 79..86 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for non-gender specific your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for non-gender specific your age."
                                            }
                                        }
                                    }
                                    in 40..59 -> {
                                        binding.hrRateScoreAthleteValue.text = "46-56"
                                        binding.hrRateScoreVeryGoodValue.text = "57-61"
                                        binding.hrRateScoreBetterThanAverageValue.text = "62-69"
                                        binding.hrRateScoreAverageValue.text = "70-77"
                                        binding.hrRateScoreBelowAverageValue.text = "78-85"
                                        binding.hrRateScorePoorValue.text = "86-95"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for non-gender specific 40-59"
                                        when (value) {
                                            in 46..56 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for non-gender specific your age."
                                            }
                                            in 57..61 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for non-gender specific your age."
                                            }
                                            in 62..69 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for non-gender specific your age."
                                            }
                                            in 70..77 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for non-gender specific your age."
                                            }
                                            in 78..85 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for non-gender specific your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for non-gender specific your age."
                                            }
                                        }
                                    }
                                    else -> {
                                        binding.hrRateScoreAthleteValue.text = "46-56"
                                        binding.hrRateScoreVeryGoodValue.text = "57-61"
                                        binding.hrRateScoreBetterThanAverageValue.text = "62-69"
                                        binding.hrRateScoreAverageValue.text = "70-76"
                                        binding.hrRateScoreBelowAverageValue.text = "77-85"
                                        binding.hrRateScorePoorValue.text = "86-97"
                                        binding.hrRateScoreRanges.text =
                                            "Heart rate ranges for non-gender specific 60+"
                                        when (value) {
                                            in 46..56 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.athlete
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Athlete for non-gender specific your age."
                                            }
                                            in 57..61 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.very_good
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Very Good for non-gender specific your age."
                                            }
                                            in 62..69 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.above_average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Better than Average for non-gender specific your age."
                                            }
                                            in 70..76 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Average for non-gender specific your age."
                                            }
                                            in 77..85 -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.average
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Below Average for non-gender specific your age."
                                            }
                                            else -> {
                                                binding.hrRateProgressCircular.setIndicatorColor(
                                                    requireContext().getColor(
                                                        R.color.poor
                                                    )
                                                )
                                                binding.hrRateScoreText.text =
                                                    "Your resting heart rate of $value bpm is Poor for non-gender specific your age."
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        binding.hrRateScoreAthleteValue.text = "47-56"
                        binding.hrRateScoreVeryGoodValue.text = "57-63"
                        binding.hrRateScoreBetterThanAverageValue.text = "64-70"
                        binding.hrRateScoreAverageValue.text = "71-78"
                        binding.hrRateScoreBelowAverageValue.text = "79-86"
                        binding.hrRateScorePoorValue.text = "87-97"
                        binding.hrRateScoreRanges.text =
                            "Heart rate ranges for population average"
                        when (value) {
                            in 47..56 -> {
                                binding.hrRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.athlete
                                    )
                                )
                                binding.hrRateScoreText.text =
                                    "Your resting heart rate of $value bpm is Athlete compared to the population."
                            }
                            in 57..63 -> {
                                binding.hrRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.very_good
                                    )
                                )
                                binding.hrRateScoreText.text =
                                    "Your resting heart rate of $value bpm is Very Good compared to the population."
                            }
                            in 64..70 -> {
                                binding.hrRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.above_average
                                    )
                                )
                                binding.hrRateScoreText.text =
                                    "Your resting heart rate of $value bpm is Better than Average compared to the population."
                            }
                            in 71..78 -> {
                                binding.hrRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.average
                                    )
                                )
                                binding.hrRateScoreText.text =
                                    "Your resting heart rate of $value bpm is Average compared to the population."
                            }
                            in 79..86 -> {
                                binding.hrRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.average
                                    )
                                )
                                binding.hrRateScoreText.text =
                                    "Your resting heart rate of $value bpm is Below Average compared to the population."
                            }
                            else -> {
                                binding.hrRateProgressCircular.setIndicatorColor(
                                    requireContext().getColor(
                                        R.color.poor
                                    )
                                )
                                binding.hrRateScoreText.text =
                                    "Your resting heart rate of $value bpm is Poor compared to the population."
                            }
                        }
                    }

                    binding.improveHrCard.visibility = View.VISIBLE
                    binding.improveHrShowText.text = "Show more"
                    binding.improveHrShowTextImage.setImageResource(R.drawable.ic_arrow_bottom)
                    val collapseHr = {
                        if (binding.improveHrShowText.text == "Show more") {
                            binding.improveHrShowText.text = "Show less"
                            binding.improveHrShowTextImage.setImageResource(R.drawable.ic_arrow_top)
                            binding.collapsingImproveHr.visibility = View.VISIBLE
                        } else {
                            binding.improveHrShowText.text = "Show more"
                            binding.improveHrShowTextImage.setImageResource(R.drawable.ic_arrow_bottom)
                            binding.collapsingImproveHr.visibility = View.GONE
                        }
                    }
                    binding.improveHrShowText.setOnClickListener { collapseHr.invoke() }
                    binding.improveHrShowTextImage.setOnClickListener { collapseHr.invoke() }
                }
                InformationScreenType.RESPIRATION_RATE -> {
                    binding.barText.text = getString(R.string.respiration_rate_bar_title)
                    binding.titleText.text = getString(R.string.respiration_rate_title)
                    binding.firstText.text = getString(R.string.respiration_rate_first_text)

                    binding.respirationRateCard.visibility = View.VISIBLE
                    binding.respirationRateProgressCircular.progress = 100
                    binding.respirationRateScoreValue.text = value.toString()
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.body)
                    constraintSet.connect(
                        R.id.title_text,
                        ConstraintSet.TOP,
                        R.id.respiration_rate_card,
                        ConstraintSet.BOTTOM,
                        24
                    )
                    constraintSet.applyTo(binding.body)
                    val progress: Int
                    when (value) {
                        in 0..11 -> {
                            binding.respirationRateProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            binding.respirationRateScoreText.text =
                                "Your respiratory rate of $value rpm is abnormally low for healthy adults."
                            binding.respirationRateProgressLinear.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            progress = (value.toFloat() * 100 / 12 / 100 * 35).toInt()
                            binding.respirationRateScoreIndicator.setColorFilter(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            binding.improveRespirationCard.visibility = View.VISIBLE
                            binding.improveRespirationCardTitle.text =
                                getString(R.string.improve_respiration_card_title_2)
                            binding.improveRespirationCardText.text =
                                getString(R.string.improve_respiration_card_text_2)
                        }
                        in 12..20 -> {
                            binding.respirationRateProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.athlete
                                )
                            )
                            binding.respirationRateScoreText.text =
                                "Your respiratory rate of $value rpm is normal for healthy adults."
                            binding.respirationRateProgressLinear.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.athlete
                                )
                            )
                            progress = (value.toFloat() * 100 / 20 / 100 * 66).toInt()
                            binding.respirationRateScoreIndicator.setColorFilter(
                                requireContext().getColor(
                                    R.color.athlete
                                )
                            )
                        }
                        else -> {
                            binding.respirationRateProgressCircular.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            binding.respirationRateScoreText.text =
                                "Your respiratory rate of $value rpm is abnormally high for healthy adults."
                            binding.respirationRateProgressLinear.setIndicatorColor(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            val progressValue = if (value > 30) 30 else value
                            progress = (progressValue.toFloat() * 100 / 30).toInt()
                            binding.respirationRateScoreIndicator.setColorFilter(
                                requireContext().getColor(
                                    R.color.below_average
                                )
                            )
                            binding.improveRespirationCard.visibility = View.VISIBLE
                            binding.improveRespirationCardTitle.text =
                                getString(R.string.improve_respiration_card_title_1)
                            binding.improveRespirationCardText.text =
                                getString(R.string.improve_respiration_card_text_1)
                        }
                    }
                    binding.respirationRateProgressLinear.progress = progress
                    binding.respirationRateProgressLinear.viewTreeObserver.addOnGlobalLayoutListener(
                        object :
                            ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                val width: Int = binding.respirationRateProgressLinear.width
                                val margin = (width * (progress.toFloat() / 100)).toInt()
                                val half = if (value > 29) 11f else if (value >= 12) 5f else 0f
                                val indicatorMargin = margin - (TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    half,
                                    resources.displayMetrics
                                )).toInt()
                                val params =
                                    binding.respirationRateScoreIndicator.layoutParams as ConstraintLayout.LayoutParams
                                params.setMargins(
                                    indicatorMargin, (TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        12f,
                                        resources.displayMetrics
                                    )).toInt(), 0, 0
                                )
                                binding.respirationRateScoreIndicator.layoutParams = params
                                binding.respirationRateScoreIndicator.viewTreeObserver.removeOnGlobalLayoutListener(
                                    this
                                )
                            }
                        })
                }
                else -> {
                    binding.barText.text = getString(R.string.ans_balance_bar_title)
                    binding.titleText.text = getString(R.string.ans_balance_bar_title)
                    binding.firstText.text = getString(R.string.ans_balance_first_text)
                    binding.secondText.text = getString(R.string.ans_balance_second_text)
                    binding.image.setImageResource(R.drawable.ic_ans_balance)
                }
            }
        }
        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
