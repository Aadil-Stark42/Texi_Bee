package com.mindnotix.texibee.data.model.firebasee

import com.google.firebase.firestore.DocumentId

data class Trip(
    @DocumentId
    val documentId: String = "",
    var trip_array: String = "[]",
    )