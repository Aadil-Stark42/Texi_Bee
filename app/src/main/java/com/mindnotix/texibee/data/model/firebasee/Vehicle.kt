package com.mindnotix.texibee.data.model.firebasee

import com.google.firebase.firestore.DocumentId

data class Vehicle(
    @DocumentId
    val documentId: String = "",
    var vehicle_array: String = "[]",


    )/* val vehicle_type: String = "",
    val vehicle_name: String = "",
    val vehicle_model: String = "",*/