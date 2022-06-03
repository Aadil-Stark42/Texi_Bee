package com.mindnotix.texibee.views.alertdialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.mindnotix.texibee.R
import com.mindnotix.texibee.callback.AlertDialogClick
import com.mindnotix.texibee.databinding.AlertDialogBinding


data class AlertDialog(
    private var context: Context,
    private var title: String,
    private var desc: String,
    private var btn_nagative: String,
    private var btn_positive: String,
    private var alertDialogClick: AlertDialogClick,
    private var icCloud: Int
) {
    init {
        val binding: AlertDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.alert_dialog,
            null,
            false
        )
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val activity: Activity = context as Activity
        val width = (activity.resources.displayMetrics.widthPixels * 0.90).toInt()

        dialog.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.btnCancel.text = btn_nagative
        binding.btnDelete.text = btn_positive
        binding.txtTitle.text = title
        binding.txtDesc.text = desc
        binding.imgStatus.setImageResource(icCloud)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            alertDialogClick.OnCancelCLick()
        }
        binding.imgClose.setOnClickListener {
            dialog.dismiss()
            alertDialogClick.OnCancelCLick()
        }
        binding.btnDelete.setOnClickListener {
            dialog.dismiss()
            alertDialogClick.OnActionClick()
        }

        dialog.show()

    }
}