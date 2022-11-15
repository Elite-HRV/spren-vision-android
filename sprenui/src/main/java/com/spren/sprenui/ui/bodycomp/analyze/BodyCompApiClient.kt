package com.spren.sprenui.ui.bodycomp.analyze

import android.content.ContentResolver
import android.content.SharedPreferences
import android.net.Uri
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.spren.sprenui.network.model.bodycomp.BodyCompBody
import com.spren.sprenui.network.model.bodycomp.BodyCompData
import com.spren.sprenui.network.model.bodycomp.Result
import com.spren.sprenui.network.model.bodycomp.TimeInfo
import com.spren.sprenui.network.service.InsightsApi
import com.spren.sprenui.ui.bodycomp.*
import com.spren.sprenui.util.Image
import com.spren.sprenui.util.ReadingStatus
import com.spren.sprenui.util.Units
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


/** Client to access body comp api.  */
object BodyCompApiClient {
    const val ERROR_SERVER_ERROR = "server error"
    const val ERROR_INTERNET_CONNECTION_ERROR = "internet connection error"
    private const val ERROR_HUMAN_NOT_DETECTED_ERROR = "human not detected"
    private const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"

    suspend fun upload(
        insightsApi: InsightsApi,
        uri: Uri,
        contentResolver: ContentResolver,
        sharedPref: SharedPreferences
    ): Task<Result> {
        val bodyCompBody = BodyCompBody(
            bodyCompData = getBodyCompData(uri, contentResolver, sharedPref)
        )

        val guid: String?
        try {
            val result = insightsApi.submitBodyComp(bodyCompBody)
            guid = result.body()?.guid
        } catch (e: Exception) {
            // throw Internet connection exception
            return Tasks.forException(RuntimeException(ERROR_INTERNET_CONNECTION_ERROR))
        }

        guid?.let {
            var result: Result?
            do {
                val response = insightsApi.getBodyCompResults(guid)
                if (!response.isSuccessful) {
                    return Tasks.forException(RuntimeException(ERROR_SERVER_ERROR))
                }

                result = response.body()
                delay(1000)
            } while (result?.bodyFat?.status == ReadingStatus.PENDING.value)

            // We are checking for general error handling
            // In the next version it may be more sophisticated and
            // it could handle partially successful and partially errored responses
            if (result?.bodyFat?.status == ReadingStatus.ERROR.value) {
                return Tasks.forException(RuntimeException(ERROR_HUMAN_NOT_DETECTED_ERROR))
            }
            delay(1000)
            return Tasks.forResult(result)
        } ?: run {
            return Tasks.forException(RuntimeException(ERROR_SERVER_ERROR))
        }
    }

    private fun getBodyCompData(uri: Uri, contentResolver: ContentResolver, sharedPref: SharedPreferences): BodyCompData {
        val base64Image = Image.base64EncodeImage(uri, contentResolver)
        val weightMetric = sharedPref.getString(WeightBodyCompFragment.WEIGHT_METRIC, null)
        val weight = sharedPref.getFloat(WeightBodyCompFragment.WEIGHT, 0F)
        val age = sharedPref.getInt(AgeBodyCompFragment.AGE, 0)
        val activityLevel = sharedPref.getString(ActivityLevelBodyCompFragment.ACTIVITY_LEVEL, null)
        val heightMetric = sharedPref.getString(HeightBodyCompFragment.HEIGHT_METRIC, null)
        val heightFT = sharedPref.getInt(HeightBodyCompFragment.HEIGHT_FT, 0)
        val heightIN = sharedPref.getInt(HeightBodyCompFragment.HEIGHT_IN, 0)
        val heightCM = sharedPref.getFloat(HeightBodyCompFragment.HEIGHT_CM, 0F)
        val gender = sharedPref.getString(GenderBodyCompFragment.GENDER, null)

        val bodyCompData = BodyCompData(
            timeInfo = getTimeInfo(),
            gender = getApiGender(gender),
            age = age,
            height = getApiHeight(heightMetric, heightFT, heightIN, heightCM),
            weight = getApiWeight(weightMetric, weight),
            vigorousDays = activityLevel?.toInt() ?: 0,
            pushUps = 0,
            image = base64Image
        )

        return bodyCompData
    }

    // Convert to kg
    private fun getApiWeight(weightMetric: String?, weight: Float): Double {
        if (weightMetric == WeightBodyCompFragment.WEIGHT_METRIC_KG) {
            return weight.toDouble()
        }

        return Units.lbsToKg(weight)
    }

    // Convert to cm
    private fun getApiHeight(heightMetric: String?, heightFT: Int, heightIN: Int, heightCM: Float): Double {
        if (heightMetric == HeightBodyCompFragment.HEIGHT_METRIC_CM) {
            return heightCM.toDouble()
        }

        return Units.feetToCm(heightFT, heightIN)
    }

    // Returns male/female only
    private fun getApiGender(gender: String?): String {
        val apiGender = when (gender) {
            GenderBodyCompFragment.GENDER_MALE -> GenderBodyCompFragment.GENDER_MALE.lowercase()
            else -> GenderBodyCompFragment.GENDER_FEMALE.lowercase()
        }
        return apiGender
    }

    private fun getTimeInfo(): TimeInfo {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val localDate = ZonedDateTime.now()
        val dateString: String = localDate.format(formatter)
        val timezoneId = localDate.zone.id
        val timezoneAbbreviation = localDate.zone.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
        val country = Locale.getDefault().country

        return TimeInfo(
            dateString, // "2022-07-05T14:29:47.836-03:00",
            timezoneId = timezoneId, // "America/New_York",
            regionCode = country, // "US",
            timezoneAbbreviation = timezoneAbbreviation // "EST"
        )
    }
}
