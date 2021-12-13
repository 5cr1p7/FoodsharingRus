package com.foodkapev.foodsharingrus

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.databinding.ActivityAddPostBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageAddPostPicRef: StorageReference? = null
    private val binding by viewBinding(ActivityAddPostBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storageAddPostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        with(binding) {
            saveNewPostBtn.setOnClickListener { uploadImage() }

            closeAddPostBtn.setOnClickListener { onBackPressed() }

            imagePost.setOnClickListener {
                CropImage.activity().start(this@AddPostActivity)
            }

//        val json = assets.open("cities.json").bufferedReader().readText()
//        val cities = Json.decodeFromString<City>(json)
//        val citiesArray: Array<String> = arrayOf(cities.city)
//        Log.d("asdas", citiesArray[0])
            val cities = resources.getStringArray(R.array.cities)
            val adapter = ArrayAdapter(
                this@AddPostActivity, android.R.layout.simple_dropdown_item_1line, cities)
            cityPickerList.setAdapter(adapter)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            val dimensionsInPx: Float = 250f
            val dimensionInDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dimensionsInPx, resources.displayMetrics).toInt()

            imageUri = result.uri
            with(binding) {
                imagePost.setImageURI(imageUri)
                imagePost.layoutParams.width = dimensionInDp
                imagePost.layoutParams.height = dimensionInDp
                imagePost.requestLayout()
                addText.visibility = View.GONE
            }
        }
    }

    private fun uploadImage() {
        when {
            imageUri == null -> Toast.makeText(
                this, "Поле изображения пусто",
                Toast.LENGTH_SHORT).show()
            binding.descriptionPost.text.isEmpty() -> Toast.makeText(
                this, "Поле описания пусто",
                Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Добавление предложения")
                progressDialog.setMessage("Пожалуйста, подождите...")
                progressDialog.show()

                val fileRef = storageAddPostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                val dateFormat = SimpleDateFormat("dd.MM, HH:mm", Locale.getDefault())
                val date = dateFormat.format(Date())

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key

                        val postMap = HashMap<String, Any>()

                        with(binding) {
                            postMap["postId"] = postId!!
                            postMap["title"] = titlePost.text.toString()
                            postMap["description"] = descriptionPost.text.toString()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postImage"] = myUrl
                            postMap["time"] = date.toString()
                            postMap["location"] = cityPickerList.text.toString()
                            if (edibleGoodsType.isChecked)
                                postMap["type"] = edibleGoodsType.text.toString()
                            else if (inedibleGoodsType.isChecked)
                                postMap["type"] = inedibleGoodsType.text.toString()

                            ref.child(postId).updateChildren(postMap)
                        }

                        Toast.makeText(this, "Предложение добавлено успешно!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else
                        progressDialog.dismiss()
                } )
            }
        }
    }
}