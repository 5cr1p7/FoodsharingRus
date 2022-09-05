package com.foodkapev.foodsharingrus.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment(R.layout.activity_signup) {

    private val binding by viewBinding(ActivitySignupBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signinLinkBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.signupBtn.setOnClickListener {
            createAccount()
        }

        binding.privacyBtn.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://raw.githubusercontent.com/5cr1p7/FoodsharingRus/master/PrivacyPolicy")
            )
            startActivity(browserIntent)
        }
    }

    private fun createAccount() {
        with(binding) {
            val fullName = fullnameSignup.text.toString()
            val userName = usernameSignup.text.toString()
            val email = emailSignup.text.toString()
            val password = passwordSignup.text.toString()


            when {
                fullName.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле имени пусто",
                    Toast.LENGTH_SHORT
                ).show()
                userName.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле никнейма пусто",
                    Toast.LENGTH_SHORT
                ).show()
                email.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле эл. почты пусто",
                    Toast.LENGTH_SHORT
                ).show()
                password.isEmpty() -> Toast.makeText(
                    requireContext(), "Поле пароля пусто",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    val progressDialog = ProgressDialog(requireContext())
                    progressDialog.setTitle("Создание аккаунта")
                    progressDialog.setMessage("Пожалуйста, подождите ...")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                saveUserInfo(fullName, userName, email, progressDialog)
                            } else {
                                val message = task.exception!!.toString()
                                Toast.makeText(
                                    requireContext(),
                                    "Ошибка: $message",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mAuth.signOut()
                                progressDialog.dismiss()
                            }
                        }
                }
            }
        }
    }

    private fun saveUserInfo(
        fullName: String,
        userName: String,
        email: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["email"] = email
        userMap["bio"] = ""
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/gramclone.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=a0bf2ee5-217f-4128-bc6b-7b9285f690f4"
        usersRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Создание прошло успешно!", Toast.LENGTH_SHORT)
                        .show()

//                    FirebaseDatabase.getInstance().reference
//                        .child("Follow").child(currentUserId)
//                        .child("Following").child(currentUserId)
//                        .setValue(true)

                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                } else {
                    val message = task.exception!!.toString()
                    Toast.makeText(requireContext(), "Ошибка: $message", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }

            }
    }
}