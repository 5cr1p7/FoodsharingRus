package com.foodkapev.foodsharingrus

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.data.User
import com.foodkapev.foodsharingrus.databinding.ActivityAccountSettingsBinding
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

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null
    private val binding by viewBinding(ActivityAccountSettingsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        with(binding) {
            logoutBtn.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val signOutIntent = Intent(this@AccountSettingsActivity, LoginActivity::class.java)
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(signOutIntent)
                finish()
            }

            closeProfileBtn.setOnClickListener {
                onBackPressed()
                finish()
            }

            deleteAccountBtn.setOnClickListener {

            }

            changeImageBtn.setOnClickListener {
                checker = "clicked"

                CropImage.activity().setAspectRatio(1, 1).start(this@AccountSettingsActivity)
            }

            saveInfoProfileBtn.setOnClickListener {
                if (checker == "clicked") {
                    uploadImageAndUpdateInfo()
                } else {
                    updateUserInfoOnly()
                }
            }
        }

        userInfo()
    }

    private fun uploadImageAndUpdateInfo() {
        with(binding) {
            when {
                imageUri == null -> Toast.makeText(
                    this@AccountSettingsActivity, "Загрузите изображение",
                    Toast.LENGTH_SHORT
                ).show()
                fullNameProfileFragment.text.isEmpty() -> Toast.makeText(
                    this@AccountSettingsActivity, "Поле имени пусто",
                    Toast.LENGTH_SHORT
                ).show()
                usernameProfileFragment.text.isEmpty() -> Toast.makeText(
                    this@AccountSettingsActivity, "Поле никнейма пусто",
                    Toast.LENGTH_SHORT
                ).show()
//            bio_profile_fragment_edit.text.isEmpty() -> Toast.makeText(
//                this, "Bio field is empty",
//                Toast.LENGTH_SHORT).show()
                else -> {
                    val progressDialog = ProgressDialog(this@AccountSettingsActivity)
                    progressDialog.setTitle("Сохранение изменений")
                    progressDialog.setMessage("Пожалуйста, подождите ...")
                    progressDialog.show()

                    val fileRef = storageProfilePicRef!!.child(firebaseUser.uid + ".jpg")

                    val uploadTask: StorageTask<*>
                    uploadTask = fileRef.putFile(imageUri!!)

                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
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

                            userMap["fullname"] = fullNameProfileFragment.text.toString()
                            userMap["username"] = usernameProfileFragment.text.toString()
                            userMap["bio"] = bioProfileFragmentEdit.text.toString()
                            userMap["image"] = myUrl

                            ref.child(firebaseUser.uid).updateChildren(userMap)

                            val intent = Intent(this@AccountSettingsActivity, AccountSettingsActivity::class.java)
                            startActivity(intent)
                            finish()
                            progressDialog.dismiss()
                        } else
                            progressDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            binding.profileImageViewProfileFragment.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {
        with(binding) {
            when {
                fullNameProfileFragment.text.isEmpty() -> Toast.makeText(
                    this@AccountSettingsActivity, "Поле имени пусто",
                    Toast.LENGTH_SHORT
                ).show()
                usernameProfileFragment.text.isEmpty() -> Toast.makeText(
                    this@AccountSettingsActivity, "Поле никнейма пусто",
                    Toast.LENGTH_SHORT
                ).show()
                bioProfileFragmentEdit.text.isEmpty() -> Toast.makeText(
                    this@AccountSettingsActivity, "Поле о себе пусто",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
                    val userMap = HashMap<String, Any>()

                    userMap["fullname"] = fullNameProfileFragment.text.toString()
                    userMap["username"] = usernameProfileFragment.text.toString()
                    userMap["bio"] = bioProfileFragmentEdit.text.toString()

                    usersRef.child(firebaseUser.uid).updateChildren(userMap)

                    Toast.makeText(this@AccountSettingsActivity, "Информация успешно обновлена!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@AccountSettingsActivity, AccountSettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
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

                    with(binding) {
                        Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profileImageViewProfileFragment)
                        usernameProfileFragment.setText(user.username)
                        fullNameProfileFragment.setText(user.fullname)
                        bioProfileFragmentEdit.setText(user.bio)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}