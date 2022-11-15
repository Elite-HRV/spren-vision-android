package com.spren.sprenui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.spren.sprenui.databinding.SprenUiViewBinding
import com.spren.sprenui.network.model.ResultsFingerCamera
import com.spren.sprenui.network.model.bodycomp.ResultsBodyComposition
import com.spren.sprenui.util.UserId
import java.util.*


class SprenUI @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        val binding = SprenUiViewBinding.inflate(LayoutInflater.from(context), this, true)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SprenUI)

        val baseUrl = attributes.getString(R.styleable.SprenUI_base_url)
        val apiKey = attributes.getString(R.styleable.SprenUI_api_key)
        val project = attributes.getInt(R.styleable.SprenUI_project, 0)

        if (project != 0) {
            val myNavHostFragment =
                binding.navHostFragment.getFragment<Fragment>() as NavHostFragment
            val inflater = myNavHostFragment.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph_body_comp)
            myNavHostFragment.navController.graph = graph
        }

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

    enum class Graphic {
        // in case using FINGER CAMERA
        GREETING_1, GREETING_2, FINGER_ON_CAMERA, NO_CAMERA,

        // for both
        SERVER_ERROR,

        // if using BODY COMPOSITION
        SETUP_GUIDE, PRIVACY, CAMERA_ACCESS_DENIED, PHOTOS_ACCESS_DENIED, INCORRECT_BODY_POSITION, GREETINGS
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
        var graphics: Map<Graphic, Int>? = null
        var onCancel: (() -> Unit)? = null
        var onFinish: ((ResultsFingerCamera?, ResultsBodyComposition?) -> Unit)? = null
    }
}