package com.spren.spren

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spren.sprenui.*
import com.spren.sprenui.SprenUI
import com.spren.sprenui.util.HardwareAlert
import com.spren.sprenui.util.HardwareCheck
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var hardwareAlert: HardwareAlert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // optionally set custom theme
        // theme inherits from "Theme.MaterialComponents.DayNight.NoActionBar"
        // see themes.xml example below
        setTheme(R.style.Theme_SprenUI)

        setContentView(R.layout.activity_main)

        // optionally check if hardware is compatible
        hardwareAlert = HardwareAlert(this)
        if (HardwareCheck.isHighPerformingDevice(this)) {

            // set user ID
            SprenUI.Config.userId = "Foo"

            // optionally set user biological sex
            SprenUI.Config.userGender = SprenUI.BiologicalSex.MALE

            // optionally set user birthdate
            SprenUI.Config.userBirthdate = Date(0)

            // after dismissing results screen
            SprenUI.Config.onFinish = { results, composition ->
                println("results $results")
                println("composition $composition")
            }

            // user presses X
            SprenUI.Config.onCancel = {
                println("canceled")
            }
        } else {
            hardwareAlert.show()
        }

        // optionally override default intro screen graphics
        // provide drawable ids for image sets in project
        // *all are required for each project if overriding
//        SprenUI.Config.graphics = mapOf(
//            SprenUI.Graphic.GREETING_1 to <image set drawable id>, // greeting screen 1
//            SprenUI.Graphic.GREETING_2 to <image set drawable id>, // greeting screen 2
//            SprenUI.Graphic.FINGER_ON_CAMERA to <image set drawable id>, // finger on camera instruction screen
//            SprenUI.Graphic.NO_CAMERA to <image set drawable id>, // camera access authorization denied screen
//            SprenUI.Graphic.SERVER_ERROR to <image set drawable id>  // server or calculation error
//        )
    }
}