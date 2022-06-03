package com.mindnotix.texibee.activitys.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.data.model.firebasee.Driver
import com.mindnotix.texibee.databinding.ActivityLoginBinding
import com.mindnotix.texibee.utils.Constant.Companion.DRIVER
import com.mindnotix.texibee.utils.Constant.Companion.IsValidEmail
import com.mindnotix.texibee.utils.Constant.Companion.USER_MAIL
import com.mindnotix.texibee.utils.Constant.Companion.USER_NAME
import com.mindnotix.texibee.utils.MnxPreferenceManager


class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var db: FirebaseFirestore? = null
    private var IsExistDetected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initviews()
        ClickEvents()
    }

    private fun ClickEvents() {
        binding.btnLogin.setOnClickListener {
            if (binding.edtUsername.text.isNotEmpty() && IsValidEmail(binding.edtEmail.text.toString())) {
                CheckUser()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.FieldEmpty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun CheckUser() {
        ShowLoading(getString(R.string.LOADING))
        Firebase.firestore.collection(DRIVER)
            .document(binding.edtEmail.text.toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        IsExistDetected = true
                        val existdriver: Driver? = querySnapshot.toObject(Driver::class.java)
                        Log.e("IsExistDetected", "IsExistDetected")
                        val driver = Driver(
                            driver_name = binding.edtUsername.text.toString(),
                            driver_email = binding.edtEmail.text.toString(),
                            creator_id = existdriver!!.creator_id,
                            image_profile = existdriver.image_profile
                        )
                        AddUser(driver)
                    } else {
                        if (!IsExistDetected) {
                            val driver = Driver(
                                driver_name = binding.edtUsername.text.toString(),
                                driver_email = binding.edtEmail.text.toString(),
                                creator_id = binding.edtEmail.text.toString()
                            )
                            AddUser(driver)
                        }else{
                            HideLoading()
                        }

                    }


                }
            }

    }

    private fun AddUser(driver: Driver) {
        val dbCourses = db!!.collection(DRIVER)

        dbCourses.document(binding.edtEmail.text.toString()).set(driver)
            .addOnSuccessListener {
                HideLoading()
                // after the data addition is successful
                MnxPreferenceManager.setString(USER_MAIL, binding.edtEmail.text.toString())
                MnxPreferenceManager.setString(USER_NAME, binding.edtUsername.text.toString())
                startActivity(
                    Intent(this, AddvehicleActivity::class.java).putExtra(
                        "uname",
                        binding.edtUsername.text.toString()
                    ).putExtra("email", binding.edtEmail.text.toString())
                )
                Toast.makeText(
                    this,
                    "Login Sucessfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                HideLoading()
                Toast.makeText(this, "Fail  \n$e", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun initviews() {
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance()
    }
}