package com.mindnotix.texibee.activitys.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.activitys.settings.car.CarListActivity
import com.mindnotix.texibee.activitys.settings.driver.DriverDetailsActivity
import com.mindnotix.texibee.activitys.settings.paymentmethod.PaymentMethodActivity
import com.mindnotix.texibee.activitys.settings.price.AddPrincingActivity
import com.mindnotix.texibee.activitys.settings.staticstic.StaticsticActivity
import com.mindnotix.texibee.activitys.settings.typeoftrip.TypeofTripActivity
import com.mindnotix.texibee.callback.AlertDialogClick
import com.mindnotix.texibee.databinding.ActivitySettingsBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.PER_BASE_CHARGE
import com.mindnotix.texibee.utils.Constant.Companion.PER_KM_CHARGE
import com.mindnotix.texibee.utils.Constant.Companion.PER_MINIT_CHARGE
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.views.alertdialog.AlertDialog
import com.mindnotix.texibee.views.alertdialog.ProgressAlert

class SettingsActivity : BaseActivity(), AlertDialogClick {
    lateinit var binding: ActivitySettingsBinding
    private var TitleDialog = ""
    private var Progress_TitleDialog = ""
    private var Dialog_status = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        ToolbarSupport(getString(R.string.settings), binding.toolbar, this)
        ClickEvents()

    }

    private fun ClickEvents() {
        binding.rltvDriverDetails.setOnClickListener {
            startActivity(Intent(this, DriverDetailsActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.rltvCarDetails.setOnClickListener {
            startActivity(Intent(this, CarListActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.rltvTrip.setOnClickListener {
            startActivity(Intent(this, TypeofTripActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.rltvbase.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddPrincingActivity::class.java
                ).putExtra(Constant.COMEFROMLOGIN, false)
            )
            overridePendingTransition(0, R.anim.fade)
        }
        binding.rltvPerKilo.setOnClickListener {
            binding.rltvbase.performClick()
        }
        binding.rltvPerMinute.setOnClickListener {
            binding.rltvbase.performClick()
        }
        binding.rltvPaymentMethod.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.CardStatistics.setOnClickListener {
            startActivity(Intent(this, StaticsticActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.CardBackup.setOnClickListener {
            TitleDialog = getString(R.string.backup_date)
            Dialog_status = "CardBackup"
            Progress_TitleDialog = getString(R.string.Uploading)
            AlertDialog(
                this,
                getString(R.string.backup_date),
                getString(R.string.keep_your_data_safe),
                getString(R.string.cancel),
                getString(R.string.Backup),
                this,
                R.drawable.ic_cloud
            )
        }
        binding.CardRestoreBackup.setOnClickListener {
            TitleDialog = getString(R.string.Restore_backup_data)
            Progress_TitleDialog = getString(R.string.Downloading)
            Dialog_status = "CardRestoreBackup"
            AlertDialog(
                this,
                getString(R.string.Restore_backup_data),
                getString(R.string.Restore_data_from_cloud),
                getString(R.string.cancel),
                getString(R.string.Restore_data),
                this,
                R.drawable.ic_cloud
            )
        }
        binding.CardDeleteTrip.setOnClickListener {
            Dialog_status = "DeleteTrip"
            AlertDialog(
                this,
                getString(R.string.SeleteDriverLog),
                getString(R.string.are_you_sure_delete_trip),
                getString(R.string.cancel),
                getString(R.string.delete),
                this,
                R.drawable.ic_cloud
            )
        }
    }

    override fun OnCancelCLick() {

    }

    override fun OnActionClick() {
        if (Dialog_status != "DeleteTrip") {
            ProgressAlert(this, TitleDialog, Progress_TitleDialog)
        }
    }

    override fun onResume() {
        super.onResume()
        AddiingChargeValue()
    }

    @SuppressLint("SetTextI18n")
    private fun AddiingChargeValue() {
        binding.txtBaseCharge.text =  "CHf "+ MnxPreferenceManager.getString(PER_BASE_CHARGE, "")!!.toDouble()
        binding.txtPerKmCharge.text = "CHf "+ MnxPreferenceManager.getString(PER_KM_CHARGE, "")!!.toDouble()
        binding.txtPerMinute.text =   "CHf "+ MnxPreferenceManager.getString(PER_MINIT_CHARGE, "")!!.toDouble()
    }
}