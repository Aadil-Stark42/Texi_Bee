package com.mindnotix.texibee.activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.auth.LoginActivity
import com.mindnotix.texibee.activitys.dashboard.MainDashBoardActivity
import com.mindnotix.texibee.utils.Constant.Companion.USER_LOGIN
import com.mindnotix.texibee.utils.MnxPreferenceManager

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        HideActionbar()
        Splash()
    }

    private fun Splash() {
        Handler().postDelayed({
            if(MnxPreferenceManager.getBoolean(USER_LOGIN,false)){
                val intent = Intent(this, MainDashBoardActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(0, R.anim.fade)
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(0, R.anim.fade)
            }


        }, 1500) // 3000 is the delayed time in milliseconds.

    }


    public fun HideActionbar() {
        try {
            supportActionBar?.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}