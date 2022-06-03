package com.mindnotix.texibee.support

import android.app.Activity
import com.mindnotix.texibee.databinding.ToolbarIncludedBinding

class ToolbarSupport(
    title: String,
    toolbar: ToolbarIncludedBinding,
    activity: Activity
) {

    init {
        toolbar.imgback.setOnClickListener {
            activity.onBackPressed()
        }
        toolbar.txtTitle.text = title
    }
}