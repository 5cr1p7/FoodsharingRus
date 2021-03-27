package com.foodkapev.foodsharingrus

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.foodkapev.foodsharingrus.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_account_settings.full_name_profile_fragment

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val signOutIntent = Intent(this, LoginActivity::class.java)
            signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(signOutIntent)
            finish()
        }

        close_profile_btn.setOnClickListener {
            onBackPressed()
            finish()
        }

        delete_account_btn.setOnClickListener {

        }

        change_image_btn.setOnClickListener {
            checker = "clicked"

            CropImage.activity().setAspectRatio(1, 1).start(this)
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            }
            else {
                updateUserInfoOnly()
            }
        }

        userInfo()


    }

    private fun uploadImageAndUpdateInfo() {
        when {
            imageUri == null -> Toast.makeText(
                this, "Загрузите изображение",
                Toast.LENGTH_SHORT).show()
            full_name_profile_fragment.text.isEmpty() -> Toast.makeText(
                this, "Поле имени пусто",
                Toast.LENGTH_SHORT).show()
            username_profile_fragment.text.isEmpty() -> Toast.makeText(
                this, "Поле никнейма пусто",
                Toast.LENGTH_SHORT).show()
//            bio_profile_fragment_edit.text.isEmpty() -> Toast.makeText(
//                this, "Bio field is empty",
//                Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Сохранение изменений")
                progressDialog.setMessage("Пожалуйста, подождите ...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser.uid + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()

                        userMap["fullname"] = full_name_profile_fragment.text.toString()
                        userMap["username"] = username_profile_fragment.text.toString()
                        userMap["bio"] = bio_profile_fragment_edit.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        val intent = Intent(this, AccountSettingsActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else
                        progressDialog.dismiss()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_fragment.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {
        when {
            full_name_profile_fragment.text.isEmpty() -> Toast.makeText(
                this, "Поле имени пусто",
                Toast.LENGTH_SHORT
            ).show()
            username_profile_fragment.text.isEmpty() -> Toast.makeText(
                this, "Поле никнейма пусто",
                Toast.LENGTH_SHORT
            ).show()
            bio_profile_fragment_edit.text.isEmpty() -> Toast.makeText(
                this, "Поле о себе пусто",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>()

                userMap["fullname"] = full_name_profile_fragment.text.toString()
                userMap["username"] = username_profile_fragment.text.toString()
                userMap["bio"] = bio_profile_fragment_edit.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Информация успешно обновлена!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AccountSettingsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (context != null)
//                    return

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_view_profile_fragment)
                    username_profile_fragment.setText(user.getUsername())
                    full_name_profile_fragment.setText(user.getFullname())
                    bio_profile_fragment_edit.setText(user.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}