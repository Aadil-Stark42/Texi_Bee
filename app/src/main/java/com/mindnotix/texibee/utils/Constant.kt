package com.mindnotix.texibee.utils

import android.app.AlertDialog
import android.content.Context
import android.text.Html
import android.view.View
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

class Constant {
    companion object {
        var CURRENT_USER_TRIP_ARRAY = JSONArray()
        var CURRENT_USER_MAKING_TRIP_OBJ = JSONObject()
        val PREF_NAME = "com.mindnotix.texibee"
        val IS_DRIVER_CREATE = "IS_DRIVER_CREATE"
        val IS_CAR_CREATE = "IS_DRIVER_CREATE"
        val TOOLBAR_TITLE = "TOOLBAR_TITLE"
        val COMEFROMLOGIN = "COMEFROM"
        val DRIVER = "Driver"
        val VEHICLE = "Vehicle"
        val TRIPTYPE = "TripType"
        val ADD_PRICING = "Add Pricing"

        val TRIP = "Trip"
        val USER_LOGIN = "USER_LOGIN"
        val USER_MAIL = "USER_MAIL"
        val USER_NAME = "USER_NAME"
        val vehicle_type = "vehicle_type"
        val ID = "id"
        val vehicle_name = "vehicle_name"
        val vehicle_model = "vehicle_model"
        val vehicle_array = "vehicle_array"
        val trip_type = "trip_type"
        val vehicle_numer = "vehicle_number"
        val vehicle_milage = "vehicle_milage"
        val vehicle_image = "vehicle_image"
        val driver_name = "driver_name"
        val driver_email = "driver_email"
        val car_detail = "car_detail"
        val start_location = "start_location"
        val destination_location = "destination_location"
        val start_km = "start_km"
        val destination_km = "destination_km"
        val traveled_km = "traveled_km"
        val date = "date"
        val start_time = "start_time"
        val end_time = "end_time"
        val travel_minit = "travel_minit"
        val fare = "fare"
        val start_latitude = "start_latitude"
        val start_longitude = "start_longitude"
        val end_latitude = "end_latitude"
        val end_longitude = "end_longitude"
        val payment_method = "payment_method"
        val trip_rate = "trip_rate"
        val PER_MINIT_CHARGE = "PER_MINIT_CHARGE"
        val PER_KM_CHARGE = "PER_KM_CHARGE"
        val PER_BASE_CHARGE = "PER_BASE_CHARGE"
        val TEXI = "TEXI"
        val FAILED = "FAILED"
        val EMPTY = "EMPTY"
        val PRIVATE = "PRIVATE"
        val DRIVER_IMAGE = "driver_image"
        val VEHICLE_IMAGE = "vehicle_image"
        val IMAGE_URL = "image_url"
        val image_profile = "image_profile"
        val EMAIL = "email"


        lateinit var builderObj: AlertDialog

        fun callNativeAlert(
            activity: Context?,
            view: View?,
            title: String?,
            message: String?,
            cancelableFlag: Boolean,
            positiveButtonText: String?,
            negativeButtonText: String,
            neutralButtonText: String,
            nativeDialogClickListener: NativeDialogClickListener
        ) {
            try {
                builderObj.dismiss()
                val builder = AlertDialog.Builder(activity)
                if (view != null) {
                    builder.setView(view)
                } else {
                    builder.setTitle(title).setMessage(Html.fromHtml(message))
                }
                builder.setCancelable(cancelableFlag).setPositiveButton(
                    positiveButtonText
                ) { dialog, whichButton -> nativeDialogClickListener.onPositive(dialog) }
                if (negativeButtonText != "") {
                    builder.setNegativeButton(
                        negativeButtonText
                    ) { dialog, which -> nativeDialogClickListener.onNegative(dialog) }
                }
                if (neutralButtonText != "") {
                    builder.setNeutralButton(
                        neutralButtonText
                    ) { dialog, which -> nativeDialogClickListener.onNeutral(dialog) }
                }
                builderObj = builder.create()
                builderObj.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun IsValidEmail(
            email: String
        ): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

    }


}