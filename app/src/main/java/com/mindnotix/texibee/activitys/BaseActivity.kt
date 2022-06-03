package com.mindnotix.texibee.activitys

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.mindnotix.texibee.R
import com.mindnotix.texibee.views.SweetAlert.SweetAlertDialog

open class BaseActivity : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.fade)
    }

    protected fun HideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun ShowLoading(message: String) {
        pDialog.titleText = message
        pDialog.setCancelable(false)
        pDialog.show()
    }

    protected fun HideLoading() {
        pDialog.dismiss()

    }

}