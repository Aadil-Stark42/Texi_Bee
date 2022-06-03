package com.mindnotix.texibee.activitys.settings.price

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.activitys.dashboard.MainDashBoardActivity
import com.mindnotix.texibee.data.model.firebasee.AddPrincing
import com.mindnotix.texibee.databinding.ActivityAddPricingBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.COMEFROMLOGIN
import com.mindnotix.texibee.utils.Constant.Companion.PER_BASE_CHARGE
import com.mindnotix.texibee.utils.Constant.Companion.PER_KM_CHARGE
import com.mindnotix.texibee.utils.Constant.Companion.PER_MINIT_CHARGE
import com.mindnotix.texibee.utils.MnxPreferenceManager

class AddPrincingActivity : BaseActivity() {
    lateinit var binding: ActivityAddPricingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_pricing)
        ToolbarSupport(getString(R.string.AddPricing), binding.toolbar, this)
        CLickEnets()
        AddingPricingData()
    }

    private fun AddingPricingData() {
        Firebase.firestore.collection(Constant.ADD_PRICING)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        val addPrincing = querySnapshot.toObject(AddPrincing::class.java)!!
                        binding.edtBaseCharge.setText(addPrincing.base_charge)
                        binding.edtKmCharge.setText(addPrincing.charge_per_km)
                        binding.edtminutCharge.setText(addPrincing.charge_per_min)
                        MnxPreferenceManager.setString(
                            PER_MINIT_CHARGE,
                            binding.edtminutCharge.text.toString()
                        )
                        MnxPreferenceManager.setString(
                            PER_KM_CHARGE,
                            binding.edtKmCharge.text.toString()
                        )
                        MnxPreferenceManager.setString(
                            PER_BASE_CHARGE,
                            binding.edtBaseCharge.text.toString()
                        )
                    }

                }
            }
    }

    private fun CLickEnets() {
        binding.btnSave.setOnClickListener {
            if (binding.edtBaseCharge.text.isNotEmpty() && binding.edtKmCharge.text.isNotEmpty()
                && binding.edtminutCharge.text.isNotEmpty()
            ) {
                MnxPreferenceManager.setString(
                    PER_MINIT_CHARGE,
                    binding.edtminutCharge.text.toString()
                )
                MnxPreferenceManager.setString(
                    PER_KM_CHARGE,
                    binding.edtKmCharge.text.toString()
                )
                MnxPreferenceManager.setString(
                    PER_BASE_CHARGE,
                    binding.edtBaseCharge.text.toString()
                )
                val addPrincing = AddPrincing(
                    base_charge = binding.edtBaseCharge.text.toString(),
                    charge_per_km = binding.edtKmCharge.text.toString(),
                    charge_per_min = binding.edtminutCharge.text.toString(),
                )
                Firebase.firestore.collection(Constant.ADD_PRICING)
                    .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
                    .set(addPrincing)
                    .addOnSuccessListener {
                        if (intent.getBooleanExtra(COMEFROMLOGIN, false)) {
                            MnxPreferenceManager.setBoolean(Constant.USER_LOGIN, true)
                            Toast.makeText(
                                this,
                                getString(R.string.Pricdeaddedsuccess),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(this, MainDashBoardActivity::class.java)
                            )
                        }else{
                            Toast.makeText(
                                this,
                                getString(R.string.PriceChangeSuccessfully),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }
                    .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                        // displaying a toast message when data addition is failed.
                        Toast.makeText(this, "Fail to add addPrincing \n$e", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.FieldEmpty),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}