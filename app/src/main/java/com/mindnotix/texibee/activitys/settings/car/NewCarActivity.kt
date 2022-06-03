package com.mindnotix.texibee.activitys.settings.car

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.data.model.firebasee.Vehicle
import com.mindnotix.texibee.databinding.ActivityNewCarBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.USER_MAIL
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class NewCarActivity : BaseActivity() {
    var Image_File: Uri? = null
    lateinit var binding: ActivityNewCarBinding
    lateinit var photoReference: String
    private lateinit var vehicle: Vehicle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_car)
        initviews()
        ClickEvents()
    }

    private fun ClickEvents() {
        binding.btnAdd.setOnClickListener {

            if (intent.getBooleanExtra(Constant.IS_CAR_CREATE, true)) {
                if (binding.edtVehicleModel.text.toString().isEmpty() &&
                    binding.edtVehicleName.text.toString().isEmpty() &&
                    binding.edtMilagePerLiter.text.toString().isEmpty() &&
                    binding.edtVehicleNumber.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        this,
                        getString(R.string.FieldEmpty),
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (Image_File == null) {
                    Toast.makeText(
                        this,
                        getString(R.string.select_vehicle_image),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                  ShowLoading(getString(R.string.CarAdding))
                    photoReference =
                        "${Constant.VEHICLE_IMAGE}/${
                            UUID.randomUUID()
                        }${Image_File!!.path!!.substring(Image_File!!.path!!.lastIndexOf("."))}"

                    UploadVehicleImage(true)
                }
            } else {
                ShowLoading(getString(R.string.UpdateCar))
                if (binding.edtVehicleModel.text.toString().isEmpty() &&
                    binding.edtVehicleName.text.toString().isEmpty() &&
                    binding.edtMilagePerLiter.text.toString().isEmpty() &&
                    binding.edtVehicleNumber.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        this,
                        getString(R.string.FieldEmpty),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    if (Image_File != null) {
                        UploadVehicleImage(false)
                    } else {
                        UpdateCar("")
                    }


                }
            }

        }
        binding.imgCar.setOnClickListener {

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
        }
    }

    private fun initviews() {
        if (intent.getBooleanExtra(Constant.IS_CAR_CREATE, true)) {
            ToolbarSupport(getString(R.string.add_new_car), binding.toolbar, this)
            binding.btnAdd.text = getString(R.string.AddCar)
        } else {
            binding.btnAdd.text = getString(R.string.Edit_Car)
            ToolbarSupport(getString(R.string.edit_car_details), binding.toolbar, this)
            binding.edtVehicleName.setText(intent.getStringExtra(Constant.vehicle_name))
            binding.edtVehicleModel.setText(intent.getStringExtra(Constant.vehicle_model))
            binding.edtVehicleNumber.setText(intent.getStringExtra(Constant.vehicle_numer))
            Glide.with(this).load(intent.getStringExtra(Constant.VEHICLE_IMAGE)).into(binding.imgCar)
        }
        vehicle = Vehicle()
        Firebase.firestore.collection(Constant.VEHICLE)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        vehicle = querySnapshot.toObject(Vehicle::class.java)!!
                    } else {
                        vehicle.vehicle_array = "[]"
                    }
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                Image_File = result.uri
                binding.imgCar.setImageURI(Image_File)
                binding.imgCar.scaleType = ImageView.ScaleType.CENTER_CROP
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error
            }
        }
    }

    private fun UploadVehicleImage(IsCreate: Boolean) {
        val storageRef =
            Firebase.storage.reference.child(photoReference)
        Image_File?.let {
            storageRef
                .putFile(it)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { downloadurl ->
                            if (IsCreate) {
                                CreateCar(downloadurl.toString())
                            } else {
                                UpdateCar(downloadurl.toString())
                            }

                        }
                    } else {
                        HideLoading()
                        Toast
                            .makeText(
                                this,
                                task.exception?.message,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
        }
    }

    private fun UpdateCar(downloadurl: String) {

        val jsonArray = JSONArray(vehicle.vehicle_array)
        var EditableObject = JSONObject()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (intent.getStringExtra(Constant.ID) == jsonObject.getString(Constant.ID)) {
                EditableObject = jsonObject
                break
            }
        }


        EditableObject.put(Constant.vehicle_model, binding.edtVehicleModel.text.toString())
        EditableObject.put(Constant.vehicle_numer, binding.edtVehicleNumber.text.toString())
        EditableObject.put(Constant.vehicle_name, binding.edtVehicleName.text.toString())

        if (downloadurl.isNotEmpty()) {
            EditableObject.put(Constant.vehicle_image, downloadurl)
        }
        val Edit_params: HashMap<String, String> = HashMap()
        Edit_params[Constant.vehicle_array] = jsonArray.toString()

        val dbCourses = FirebaseFirestore.getInstance().collection(Constant.VEHICLE)
        dbCourses.document(MnxPreferenceManager.getString(USER_MAIL, "").toString())
            .update(Edit_params as Map<String, Any>)
            .addOnSuccessListener {

                HideLoading()
                // after the data addition is successful
                Toast.makeText(
                    this,
                    getString(R.string.CarUpdated),
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            }
            .addOnFailureListener { e ->
                HideLoading()// this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(this, "Fail  \n$e", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun CreateCar(downloadurl: String) {

        val dbCourses =
            FirebaseFirestore.getInstance().collection(Constant.VEHICLE)
        val jsonArray = JSONArray(vehicle.vehicle_array)
        val addvehicleobj = JSONObject()
        addvehicleobj.put(Constant.ID, UUID.randomUUID().toString())
        addvehicleobj.put(
            Constant.vehicle_model,
            binding.edtVehicleModel.text.toString()
        )
        addvehicleobj.put(
            Constant.vehicle_name,
            binding.edtVehicleName.text.toString()
        )
        addvehicleobj.put(Constant.trip_type, Constant.TEXI)
        addvehicleobj.put(
            Constant.vehicle_numer,
            binding.edtVehicleNumber.text.toString()
        )
        addvehicleobj.put(
            Constant.vehicle_milage,
            binding.edtMilagePerLiter.text.toString()
        )
        addvehicleobj.put(Constant.vehicle_image, downloadurl)
        jsonArray.put(addvehicleobj)
        val vehicle = Vehicle(
            vehicle_array = jsonArray.toString()
        )
        dbCourses.document(
            MnxPreferenceManager.getString(
                Constant.USER_MAIL,
                ""
            ).toString()
        ).set(vehicle)
            .addOnSuccessListener {
                // after the data addition is successful
                HideLoading()
                Toast.makeText(
                    this,
                    getString(R.string.VehicleAdded),
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            }
            .addOnFailureListener { e ->
                HideLoading()
                Toast.makeText(
                    this,
                    "Fail to add vehicle \n$e",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

    }

}