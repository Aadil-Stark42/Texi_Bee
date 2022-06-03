package com.mindnotix.texibee.data.model.firebasee

import com.google.firebase.firestore.DocumentId

data class Driver(
    @DocumentId
    val documentId: String = "",
    val driver_name: String = "",
    val driver_email: String = "",
    val creator_id: String = "",
    val image_profile: String = "",
)