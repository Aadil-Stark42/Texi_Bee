package com.mindnotix.texibee.views.alertdialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.mindnotix.texibee.R
import com.mindnotix.texibee.databinding.ProgressDialogBinding


data class ProgressAlert(
    private var context: Context,
    private var title: String,
    private var progress_title: String,
) {
    init {
        val binding: ProgressDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.progress_dialog,
            null,
            false
        )
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        val activity: Activity = context as Activity
        val width = (activity.resources.displayMetrics.widthPixels * 0.90)

        dialog.window?.setLayout(
            width.toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnComplete.isClickable = false
        binding.btnComplete.setOnClickListener {
            dialog.dismiss()
        }
        binding.imgClose.setOnClickListener {
            dialog.dismiss()
        }
        binding.txtTitle.text = title
            var ProgressCount = 1
        val handler = Handler()
        val r: Runnable = object : Runnable {
            @SuppressLint("UseCompatLoadingForColorStateLists")
            override fun run() {
                binding.progressBar.progress = ProgressCount
                if (ProgressCount == 100) {
                    binding.txtProgress.text = "Complete"

                    binding.btnComplete.isClickable = true
                    binding.btnComplete.setTextColor(context.resources.getColor(R.color.black))
                    binding.btnComplete.backgroundTintList =
                        context.resources.getColorStateList(R.color.view_main_color)
                    handler.removeCallbacks(this)
                } else {
                    ProgressCount += 1
                    binding.txtProgress.text = "$progress_title $ProgressCount %"
                    handler.postDelayed(this, 100)
                }

            }
        }

        handler.postDelayed(r, 100)
        dialog.show()

    }
}