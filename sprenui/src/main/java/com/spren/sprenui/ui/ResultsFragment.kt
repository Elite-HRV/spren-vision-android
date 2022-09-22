package com.spren.sprenui.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spren.sprenui.R
import com.spren.sprenui.SprenUI
import com.spren.sprenui.databinding.FragmentResultsBinding
import com.spren.sprenui.util.getThemeId
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var font = Typeface.createFromAsset(activity?.assets, "Roboto-Bold.ttf")
        binding.doneText.typeface = font
        binding.doneText.setTextAppearance(requireContext().getThemeId())
        binding.congratsText.typeface = font
        binding.populationComparisonText.typeface = font
        binding.unlockBiomarkersText.typeface = font
        font = Typeface.createFromAsset(activity?.assets, "Roboto-Regular.ttf")
        binding.completedText.typeface = font
        binding.hrvScoreValue.typeface = font
        binding.hrScoreValue.typeface = font
        binding.hrvScoreValueText.typeface = font
        binding.hrScoreValueText.typeface = font
        binding.compareText.typeface = font
        binding.you.typeface = font
        binding.oneText.typeface = font
        binding.hundredText.typeface = font
        binding.twentyFourText.typeface = font
        binding.fiftyEightText.typeface = font
        binding.ninetyThreeText.typeface = font
        binding.brandsTitle.typeface = font
        binding.brandsText.typeface = font
        binding.clickBelowText.typeface = font
        binding.sprenButton.typeface = font
        binding.forInvestigationalUseOnlyText.typeface = font
        binding.sprenButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.spren.com"))
            startActivity(browserIntent)
        }
        var hrValue = 0f
        var hrvValue = 0f
        arguments?.let {
            hrValue = ResultsFragmentArgs.fromBundle(it).hr
            hrvValue = ResultsFragmentArgs.fromBundle(it).hrv
            binding.hrScoreValue.text = hrValue.roundToInt().toString()
            val hrv = hrvValue.roundToInt().toString()
            binding.hrvScoreValue.text = hrv
            binding.you.text = getString(R.string.you, hrv)
        }
        binding.doneText.setOnClickListener {
            SprenUI.Config.onFinish?.let {
                it.invoke(SprenUI.LatestRequest.guid, hrValue, hrvValue)
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_ResultsFragment_to_MeasureHRVHomeFragment)
        }

        // population comparison range setup
        binding.range.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val width: Int = binding.range.width
                val hrvMargin = (width * (hrvValue / 100)).toInt()

                val arrowMargin = hrvMargin - (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4.5f,
                    resources.displayMetrics
                )).toInt()
                var params = binding.arrowImage.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(
                    arrowMargin, (TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    )).toInt(), 0, 0
                )
                binding.arrowImage.layoutParams = params

                val youMargin = hrvMargin - (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    resources.displayMetrics
                )).toInt()
                params = binding.you.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(
                    youMargin, (TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        22f,
                        resources.displayMetrics
                    )).toInt(), 0, 0
                )
                binding.you.layoutParams = params

                val firstMargin = (width * 0.24).toInt()
                val firstMarginNumber = firstMargin - (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    resources.displayMetrics
                )).toInt()
                params = binding.twentyFourText.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(firstMarginNumber, 0, 0, 0)
                binding.twentyFourText.layoutParams = params

                params = binding.rangeUsed.layoutParams as ConstraintLayout.LayoutParams
                val rangeUsedNewWidth = (width * 0.68).toInt()
                params.width = rangeUsedNewWidth
                params.setMargins(firstMargin, 0, 0, 0)
                binding.rangeUsed.layoutParams = params

                val centerMargin = firstMargin + (width * 0.34).toInt()
                params = binding.center.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(centerMargin, 0, 0, 0)
                binding.center.layoutParams = params

                val centerMarginNumber = centerMargin - (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    resources.displayMetrics
                )).toInt()
                params = binding.fiftyEightText.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(centerMarginNumber, 0, 0, 0)
                binding.fiftyEightText.layoutParams = params

                val lastMarginNumber =
                    centerMargin + (width * 0.34).toInt() - (TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        5f,
                        resources.displayMetrics
                    )).toInt()
                params = binding.ninetyThreeText.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(lastMarginNumber, 0, 0, 0)
                binding.ninetyThreeText.layoutParams = params

                binding.range.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}