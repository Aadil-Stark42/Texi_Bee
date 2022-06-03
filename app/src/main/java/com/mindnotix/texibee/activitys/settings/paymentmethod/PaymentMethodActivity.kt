package com.mindnotix.texibee.activitys.settings.paymentmethod

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.databinding.ActivityPaymentMethodBinding
import com.mindnotix.texibee.databinding.PaymentMethodListBinding
import com.mindnotix.texibee.support.ToolbarSupport
import java.util.ArrayList

class PaymentMethodActivity : BaseActivity() {
    lateinit var binding: ActivityPaymentMethodBinding
    private val list: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_method)
        ToolbarSupport(getString(R.string.payment_method), binding.toolbar, this)
        ClickEvents()
        AddData()

    }


    private fun ClickEvents() {
        binding.llnewPayment.setOnClickListener {
            binding.llnewPayment.visibility = View.GONE
            binding.llCreatePayment.visibility = View.VISIBLE
            binding.nestedscroll.post { binding.nestedscroll.fullScroll(View.FOCUS_DOWN) }
        }
        binding.btnSave.setOnClickListener {
            HideKeyboard(this)
            binding.llnewPayment.visibility = View.VISIBLE
            binding.llCreatePayment.visibility = View.GONE
            if (binding.edtTripName.text.isNotEmpty()) {
                list.add(binding.edtTripName.text.toString())
                RvDriverListAdapter(list)
                binding.edtTripName.setText("")

            }
            binding.nestedscroll.post { binding.nestedscroll.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun AddData() {
        list.add("Taxi Trip")
        list.add("Failed Trip")
        list.add("Empty Trip")
        RvDriverListAdapter(list)
    }


    @SuppressLint("SetTextI18n")
    private fun RvDriverListAdapter(data: ArrayList<String>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.payment_method_list)
        binding.RvPaymentMethod.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvPaymentMethod.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: PaymentMethodListBinding = viewDataBinding as PaymentMethodListBinding

            binding.txtPaymentName.text = data[position]

            binding.icMenu.setOnClickListener {
                openOptionMenu(binding.icMenu, position)
            }
        }
    }

    fun openOptionMenu(v: View, position: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater
            .inflate(R.menu.driver_list_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.edit_menu) {

            }
            true
        }
        popup.show()
    }
}