package com.foodkapev.foodsharingrus.presentation.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.LoginActivity
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.FragmentAccountSettingsBinding
import com.foodkapev.foodsharingrus.domain.models.User
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
import com.theartofdev.edmodo.cropper.CropImage

class AccountSettingsFragment : Fragment(R.layout.fragment_account_settings) {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null
    private val binding by viewBinding(FragmentAccountSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        with(binding) {
            logoutBtn.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val signOutIntent = Intent(requireContext(), LoginActivity::class.java)
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(signOutIntent)
                activity?.finish()
            }

            closeProfileBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            deleteAccountBtn.setOnClickListener {

            }

            changeImageBtn.setOnClickListener {
                checker = "clicked"

                CropImage.activity().start(requireContext(), this@AccountSettingsFragment)
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
                    requireContext(), "Загрузите изображение",
                    Toast.LENGTH_SHORT
                ).show()
                fullNameProfileFragment.text.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле имени пусто",
                    Toast.LENGTH_SHORT
                ).show()
                usernameProfileFragment.text.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле никнейма пусто",
                    Toast.LENGTH_SHORT
                ).show()
//            bio_profile_fragment_edit.text.isEmpty() -> Toast.makeText(
//                this, "Bio field is empty",
//                Toast.LENGTH_SHORT).show()
                else -> {
                    val progressDialog = ProgressDialog(requireContext())
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

//                            val intent =
//                                Intent(requireContext(), AccountSettingsActivity::class.java)
//                            startActivity(intent)
//                            findNavController().popBackStack()
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
                    requireContext(), "Поле имени пусто",
                    Toast.LENGTH_SHORT
                ).show()

                usernameProfileFragment.text.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле никнейма пусто",
                    Toast.LENGTH_SHORT
                ).show()

                bioProfileFragmentEdit.text.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле о себе пусто",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
                    val userMap = HashMap<String, Any>()

                    userMap["fullname"] = fullNameProfileFragment.text.toString()
                    userMap["username"] = usernameProfileFragment.text.toString()
                    userMap["bio"] = bioProfileFragmentEdit.text.toString()

                    usersRef.child(firebaseUser.uid).updateChildren(userMap)

                    Toast.makeText(
                        requireContext(),
                        "Информация успешно обновлена!",
                        Toast.LENGTH_SHORT
                    ).show()

//                    val intent = Intent(requireContext(), AccountSettingsActivity::class.java)
//                    startActivity(intent)
//                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun userInfo() {
        val usersRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (context != null)
//                    return

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    with(binding) {
                        Glide.with(requireContext()).load(user?.image)
                            .placeholder(R.drawable.profile)
                            .into(profileImageViewProfileFragment)
                        fullNameProfileFragment.setText(user?.fullname)
                        usernameProfileFragment.setText(user?.username)
                        bioProfileFragmentEdit.setText(user?.bio)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}