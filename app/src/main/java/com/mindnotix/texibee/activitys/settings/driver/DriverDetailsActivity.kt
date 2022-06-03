package com.mindnotix.texibee.activitys.settings.driver

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.callback.AlertDialogClick
import com.mindnotix.texibee.data.model.firebasee.Driver
import com.mindnotix.texibee.databinding.ActivityDriverDetailsBinding
import com.mindnotix.texibee.databinding.DriversListItemBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.IS_DRIVER_CREATE
import com.mindnotix.texibee.utils.Constant.Companion.USER_MAIL
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.views.alertdialog.AlertDialog


class DriverDetailsActivity : BaseActivity(), AlertDialogClick {
    lateinit var binding: ActivityDriverDetailsBinding
    private var db: FirebaseFirestore? = null
    private var Selected_Email_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_details)
        ToolbarSupport(getString(R.string.driver_details), binding.toolbar, this)
        initviews()
        AddData()
        ClickEvents()
    }

    private fun initviews() {
        db = FirebaseFirestore.getInstance()
    }

    private fun ClickEvents() {
        binding.llNewDriver.setOnClickListener {
            startActivity(
                Intent(this, CreateDriverActivity::class.java).putExtra(
                    IS_DRIVER_CREATE,
                    true
                )
            )
            overridePendingTransition(0, R.anim.fade)
        }
    }

    private fun AddData() {
        Firebase.firestore.collection(Constant.DRIVER)
            .addSnapshotListener { snapshot, _ ->
                val DriverList: List<Driver> = snapshot!!.toObjects(Driver::class.java)
                val FilterDriverList: ArrayList<Driver> = ArrayList()
                Log.e("DriverList", DriverList.size.toString())
                DriverList.forEach {
                    if (it.documentId != MnxPreferenceManager.getString(USER_MAIL, "").toString()) {
                        if (it.creator_id == MnxPreferenceManager.getString(USER_MAIL, "")
                                .toString()
                        ) {
                            FilterDriverList.add(it)
                        }
                    }
                }
                Log.e("FilterDriverList", FilterDriverList.size.toString())
                RvDriverListAdapter(FilterDriverList)
            }


    }


    @SuppressLint("SetTextI18n")
    private fun RvDriverListAdapter(data: ArrayList<Driver>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.drivers_list_item)
        binding.RvDriverList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvDriverList.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: DriversListItemBinding = viewDataBinding as DriversListItemBinding
            Glide.with(this).load(data[position].image_profile)
                .placeholder(R.drawable.edit_profile_holder).into(binding.imgprofile)
            binding.txtname.text = data[position].driver_name
            binding.icMenu.setOnClickListener {
                Selected_Email_id = data[position].driver_email
                openOptionMenu(binding.icMenu, position, data)
            }
        }
    }

    fun openOptionMenu(v: View, position: Int, data: ArrayList<Driver>) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater
            .inflate(R.menu.driver_list_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.edit_menu) {
                startActivity(
                    Intent(this, CreateDriverActivity::class.java).putExtra(
                        IS_DRIVER_CREATE,
                        false
                    ).putExtra(Constant.IMAGE_URL, data[position].image_profile)
                        .putExtra(Constant.driver_name, data[position].driver_name)
                        .putExtra(Constant.EMAIL, data[position].driver_email)
                )
                overridePendingTransition(0, R.anim.fade)

            }
            if (item.itemId == R.id.delete_menu) {

                AlertDialog(
                    this,
                    getString(R.string.delete_driver),
                    getString(R.string.are_you_sure_delete_driver),
                    getString(R.string.nogoback),
                    getString(R.string.delete_driver),
                    this,
                    R.drawable.ic_warning
                )
            }
            true
        }
        popup.show()
    }

    override fun OnCancelCLick() {

    }

    override fun OnActionClick() {
        ShowLoading(getString(R.string.DriverDeleting))
        val dbCourses = db!!.collection(Constant.DRIVER)
        dbCourses.document(Selected_Email_id).delete()
            .addOnSuccessListener {
                HideLoading()
                // after the data addition is successful
                HideLoading()
                Toast.makeText(
                    this,
                    getString(R.string.DRiverDeleted),
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener { e ->
                HideLoading()// this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(this, "Fail  \n$e", Toast.LENGTH_SHORT)
                    .show()
            }

    }
}