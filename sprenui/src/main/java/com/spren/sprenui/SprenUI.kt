package com.spren.sprenui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.spren.sprenui.databinding.SprenUiViewBinding
import com.spren.sprenui.util.UserId
import java.util.Date

class SprenUI @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        SprenUiViewBinding.inflate(LayoutInflater.from(context), this, true)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SprenUI)

        val baseUrl = attributes.getString(R.styleable.SprenUI_base_url)
        val apiKey = attributes.getString(R.styleable.SprenUI_api_key)

        baseUrl?.let { Config.baseURL = it }
        apiKey?.let { Config.apiKey = it }

        Config.userId = UserId.get(context.contentResolver)

        attributes.recycle()
    }

    internal object LatestRequest {
        var guid: String = ""
    }

    enum class BiologicalSex {
        MALE, FEMALE, OTHER
    }

    object Config {
        // API config
        var baseURL: String = ""
        var apiKey: String = ""

        // user config
        var userId: String = ""
        var userGender: BiologicalSex? = null
        var userBirthdate: Date? = null

        // UI config
        var onCancel: (() -> Unit)? = null
        var onFinish: ((String, Float, Float, Float, Float, Float?, Float?, Float) -> Unit)? = null
    }
}