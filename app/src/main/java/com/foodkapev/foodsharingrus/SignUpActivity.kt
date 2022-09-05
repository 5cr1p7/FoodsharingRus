package com.foodkapev.foodsharingrus

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.databinding.ActivitySignupBinding
import com.foodkapev.foodsharingrus.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivitySignupBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        binding.signinLinkBtn.setOnClickListener {
            val signInIntent = Intent(this, LoginActivity::class.java)
            startActivity(signInIntent)
        }

        binding.signupBtn.setOnClickListener {
            createAccount()
        }

        binding.privacyBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/5cr1p7/FoodsharingRus/master/PrivacyPolicy"))
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
                    this@SignUpActivity, "Поле имени пусто",
                    Toast.LENGTH_SHORT
                ).show()
                userName.isEmpty() -> Toast.makeText(
                    this@SignUpActivity, "Поле никнейма пусто",
                    Toast.LENGTH_SHORT
                ).show()
                email.isEmpty() -> Toast.makeText(
                    this@SignUpActivity, "Поле эл. почты пусто",
                    Toast.LENGTH_SHORT
                ).show()
                password.isEmpty() -> Toast.makeText(
                    this@SignUpActivity, "Поле пароля пусто",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    val progressDialog = ProgressDialog(this@SignUpActivity)
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
                                Toast.makeText(this@SignUpActivity, "Ошибка: $message", Toast.LENGTH_SHORT).show()
                                mAuth.signOut()
                                progressDialog.dismiss()
                            }
                        }
                }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, progressDialog: ProgressDialog) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["email"] = email
        userMap["bio"] = ""
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/gramclone.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=a0bf2ee5-217f-4128-bc6b-7b9285f690f4"
        usersRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Создание прошло успешно!", Toast.LENGTH_SHORT).show()
                    
//                    FirebaseDatabase.getInstance().reference
//                        .child("Follow").child(currentUserId)
//                        .child("Following").child(currentUserId)
//                        .setValue(true)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Ошибка: $message", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }

            }
    }
}