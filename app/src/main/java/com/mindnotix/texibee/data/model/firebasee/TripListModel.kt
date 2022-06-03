package com.mindnotix.texibee.data.model.firebasee
import com.google.gson.annotations.SerializedName
data class TripListModel(
    @SerializedName("driver_name") var driverName: String? = "",
    @SerializedName("car_detail") var carDetail: String? = "",
    @SerializedName("trip_type") var tripType: String? = "",
    @SerializedName("start_location") var startLocation: String? = "",
    @SerializedName("start_latitude") var startLatitude: String? = "",
    @SerializedName("start_longitude") var startLongitude: String? = "",
    @SerializedName("destination_location") var destinationLocation: String? = "",
    @SerializedName("start_km") var startKm: String? = "",
    @SerializedName("end_latitude") var endLatitude: String? = "",
    @SerializedName("end_longitude") var endLongitude: String? = "",
    @SerializedName("destination_km") var destinationKm: String? = "",
    @SerializedName("traveled_km") var traveledKm: String? = "",
    @SerializedName("trip_rate") var tripRate: String? = "",
    @SerializedName("date") var date: String? = "",
    @SerializedName("start_time") var startTime: String? = "",
    @SerializedName("end_time") var endTime: String? = "",
    @SerializedName("payment_method") var paymentMethod: String? = "",
    @SerializedName("travel_minit") var travelMinit: String? = ""

)