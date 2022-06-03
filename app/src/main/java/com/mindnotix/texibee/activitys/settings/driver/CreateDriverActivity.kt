package com.mindnotix.texibee.activitys.settings.driver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.data.model.firebasee.Driver
import com.mindnotix.texibee.databinding.ActivityCreateDriverBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.IsValidEmail
import com.mindnotix.texibee.utils.Constant.Companion.driver_email
import com.mindnotix.texibee.utils.Constant.Companion.driver_name
import com.mindnotix.texibee.utils.Constant.Companion.image_profile
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.views.permissionx.guolindev.PermissionX
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class CreateDriverActivity : BaseActivity() {
    lateinit var binding: ActivityCreateDriverBinding
    private var db: FirebaseFirestore? = null
    lateinit var photoReference: String

    var Image_File: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_driver)
        initviews()
        ClickEvents()
    }

    private fun ClickEvents() {

        binding.btnAdd.setOnClickListener {

            if (intent.getBooleanExtra(Constant.IS_DRIVER_CREATE, true)) {

                if (binding.edtName.text.isEmpty() && !IsValidEmail(binding.edtemail.text.toString())) {
                    Toast
                        .makeText(
                            this,
                            getString(R.string.FieldEmpty),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else if (Image_File == null) {
                    Toast
                        .makeText(
                            this,
                            getString(R.string.SelecteProfile),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    ShowLoading(getString(R.string.Uploadinngg))
                    photoReference =
                        "${Constant.DRIVER_IMAGE}/${
                            binding.edtemail.text.toString()
                        }${Image_File!!.path!!.substring(Image_File!!.path!!.lastIndexOf("."))}"
                    Log.e("photoReference", photoReference)
                    UploadProfile(true)
                }
            } else {
                if (binding.edtName.text.isEmpty() && !IsValidEmail(binding.edtemail.text.toString())) {
                    Toast
                        .makeText(
                            this,
                            getString(R.string.FieldEmpty),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    ShowLoading(getString(R.string.Uploadinngg))
                    if (Image_File != null) {
                        photoReference =
                            "${Constant.DRIVER_IMAGE}/${
                                binding.edtemail.text
                            }${Image_File!!.path!!.substring(Image_File!!.path!!.lastIndexOf("."))}"
                        Log.e("photoReference", photoReference)
                        UploadProfile(false)
                    } else {
                        UpdateUser(
                            binding.edtemail.text.toString(),
                            binding.edtName.text.toString(),
                            ""
                        )
                    }
                }
            }


        }
        binding.imgprofile.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    }
                }

        }
    }

    private fun UploadProfile(IsCreate: Boolean) {
        val storageRef =
            Firebase.storage.reference.child(photoReference)
        Image_File?.let {
            storageRef
                .putFile(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { downloadurl ->
                            if (IsCreate) {
                                val driver = Driver(
                                    driver_name = binding.edtName.text.toString(),
                                    driver_email = binding.edtemail.text.toString(),
                                    image_profile = downloadurl.toString(),
                                    creator_id = MnxPreferenceManager.getString(
                                        Constant.USER_MAIL,
                                        ""
                                    )
                                        .toString()
                                )
                                AddUser(driver)
                            } else {
                                UpdateUser(
                                    binding.edtemail.text.toString(),
                                    binding.edtName.text.toString(),
                                    downloadurl.toString()
                                )
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

    private fun initviews() {
        db = FirebaseFirestore.getInstance()
        if (intent.getBooleanExtra(Constant.IS_DRIVER_CREATE, true)) {
            ToolbarSupport(getString(R.string.create_driver_details), binding.toolbar, this)
            binding.btnAdd.text = getString(R.string.create_driver)
            binding.edtName.setText("")
        } else {
            binding.btnAdd.text = getString(R.string.edit_driver)
            ToolbarSupport(getString(R.string.edit_driver_details), binding.toolbar, this)
            binding.edtName.setText(intent.getStringExtra(Constant.driver_name))
            binding.edtemail.setText(intent.getStringExtra(Constant.EMAIL))
            Glide.with(this).load(intent.getStringExtra(Constant.IMAGE_URL))
                .into(binding.imgprofile)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                Image_File = result.uri
                binding.imgprofile.setImageURI(Image_File)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }


    private fun AddUser(driver: Driver) {
        val dbCourses = db!!.collection(Constant.DRIVER)
        dbCourses.document(binding.edtemail.text.toString()).set(driver)
            .addOnSuccessListener {
                HideLoading()
                // after the data addition is successful
                Toast.makeText(
                    this,
                    getString(R.string.Drivercreatedsuccessfully),
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

    private fun UpdateUser(email: String, drivername: String, imgurl: String) {
        val Edit_params: HashMap<String, String> = HashMap()
        Edit_params[driver_name] = drivername
        Edit_params[driver_email] = email
        if (imgurl.isNotEmpty()) {
            Edit_params[image_profile] = imgurl
        }
        val dbCourses = db!!.collection(Constant.DRIVER)
        dbCourses.document(binding.edtemail.text.toString()).update(Edit_params as Map<String, Any>)
            .addOnSuccessListener {
                HideLoading()
                // after the data addition is successful
                Toast.makeText(
                    this,
                    getString(R.string.DriverUpdatedSuccessfully),
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
}