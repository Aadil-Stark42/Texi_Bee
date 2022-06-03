package com.mindnotix.texibee.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mindnotix.texibee.R
import com.mindnotix.texibee.callback.VehicleAddedClick
import com.mindnotix.texibee.data.model.firebasee.TripType
import com.mindnotix.texibee.data.model.firebasee.Vehicle
import com.mindnotix.texibee.databinding.FragmentAddVehicleBinding
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.ID
import com.mindnotix.texibee.utils.Constant.Companion.TEXI
import com.mindnotix.texibee.utils.Constant.Companion.trip_type
import com.mindnotix.texibee.views.SweetAlert.SweetAlertDialog
import com.mindnotix.texibee.views.permissionx.guolindev.PermissionX
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class AddVehicleFragment(var usermail: String, var vehicleAddedClick: VehicleAddedClick) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddVehicleBinding
    private var db: FirebaseFirestore? = null
    private lateinit var vehicle: Vehicle
    private lateinit var tripType: TripType
    var Image_File: Uri? = null
    lateinit var photoReference: String
    lateinit var pDialog: SweetAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_vehicle, container, false)
        initviews()
        clickEvents()
        return binding.root
    }

    private fun clickEvents() {
        binding.btnAdd.setOnClickListener {
            if (binding.edtVehicleModel.text.toString().isEmpty() &&
                binding.edtVehicleName.text.toString().isEmpty() &&
                binding.edtMilagePerLiter.text.toString().isEmpty() &&
                binding.edtVehicleNumber.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    activity,
                    getString(R.string.FieldEmpty),
                    Toast.LENGTH_SHORT
                ).show()

            } else if (Image_File == null) {
                Toast.makeText(
                    activity,
                    getString(R.string.select_vehicle_image),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                pDialog.titleText = getString(R.string.CarAdding)
                pDialog.setCancelable(false)
                pDialog.show()
                photoReference =
                    "${Constant.VEHICLE_IMAGE}/${
                        UUID.randomUUID()
                    }${Image_File!!.path!!.substring(Image_File!!.path!!.lastIndexOf("."))}"
                Log.e("photoReference", photoReference)
                UploadVehicleImage()
            }
        }

        binding.imgVehicle.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(requireActivity());
                    }
                }
        }
    }

    private fun initviews() {
        pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        db = FirebaseFirestore.getInstance()
        vehicle = Vehicle()
        Firebase.firestore.collection(Constant.VEHICLE).document(usermail)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        vehicle = querySnapshot.toObject(Vehicle::class.java)!!

                    } else {
                        vehicle.vehicle_array = "[]"
                    }
                }
            }
        tripType = TripType()
        Firebase.firestore.collection(Constant.TRIPTYPE).document(usermail)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        tripType = querySnapshot.toObject(TripType::class.java)!!

                    } else {
                        tripType.trip_type_array = "[]"
                    }
                }
            }

    }


    private fun UploadVehicleImage() {
        val storageRef =
            Firebase.storage.reference.child(photoReference)
        Image_File?.let {
            storageRef
                .putFile(it)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { downloadurl ->
                            val dbTRips = db!!.collection(Constant.TRIPTYPE)
                            val jsonArray = JSONArray(tripType.trip_type_array)
                            val addTripobj = JSONObject()
                            addTripobj.put(ID, UUID.randomUUID().toString())
                            addTripobj.put(
                                trip_type,
                                TEXI
                            )
                            jsonArray.put(addTripobj)
                            val triptypemodel = TripType(
                                trip_type_array = jsonArray.toString()
                            )
                            dbTRips.document(usermail).set(triptypemodel)
                                .addOnSuccessListener {
                                    // after the data addition is successful
                                    AddVehicle(downloadurl)
                                }
                                .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                                    // displaying a toast message when data addition is failed.
                                    pDialog.dismiss()
                                    Toast.makeText(
                                        activity,
                                        "Fail to add dbTRips \n$e",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                        }
                    } else {
                        pDialog.dismiss()
                        Toast
                            .makeText(
                                requireActivity(),
                                task.exception?.message,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
        }
    }

    private fun AddVehicle(downloadurl: Uri) {

        val dbCourses = db!!.collection(Constant.VEHICLE)
        val jsonArray = JSONArray(vehicle.vehicle_array)
        val addvehicleobj = JSONObject()
        addvehicleobj.put(ID, UUID.randomUUID().toString())
        addvehicleobj.put(
            Constant.vehicle_model,
            binding.edtVehicleModel.text.toString()
        )
        addvehicleobj.put(Constant.vehicle_name, binding.edtVehicleName.text.toString())
        addvehicleobj.put(trip_type, TEXI)
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
        dbCourses.document(usermail).set(vehicle)
            .addOnSuccessListener {
                // after the data addition is successful
                pDialog.dismiss()
                vehicleAddedClick.VehicleAddedClick()
                Toast.makeText(
                    activity,
                    getString(R.string.VehicleAdded),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
            .addOnFailureListener { e ->
                pDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Fail to add vehicle \n$e",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

    }

    fun vehicleFragment(uri: Uri?) {
        Image_File = uri
        binding.imgVehicle.setImageURI(Image_File)

    }

}