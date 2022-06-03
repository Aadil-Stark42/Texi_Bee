package com.mindnotix.texibee.data.model.firebasee

import com.google.firebase.firestore.DocumentId

data class TripType(
    @DocumentId
    val documentId: String = "",
    var trip_type_array: String = "[]",
    )