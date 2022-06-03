package com.mindnotix.texibee.data.model.firebasee

import com.google.firebase.firestore.DocumentId

data class AddPrincing(
    @DocumentId
    val documentId: String = "",
    var base_charge: String = "",
    var charge_per_km: String = "",
    var charge_per_min: String = "",
    )