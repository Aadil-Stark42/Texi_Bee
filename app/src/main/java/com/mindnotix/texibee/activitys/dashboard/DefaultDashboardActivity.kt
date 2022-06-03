package com.mindnotix.texibee.activitys.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mindnotix.texibee.R
import com.mindnotix.texibee.databinding.ActivityDefaultDashboardBinding
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.activitys.settings.SettingsActivity


class DefaultDashboardActivity : BaseActivity() {
    lateinit var binding: ActivityDefaultDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_dashboard)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_default_dashboard)
        ClickEvents()
    }

    private fun ClickEvents() {
        binding.btnGoBack.setOnClickListener {
            startActivity(Intent(this, MainDashBoardActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
    }
}