package com.foodkapev.foodsharingrus

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.databinding.ActivityLoginBinding
import com.foodkapev.foodsharingrus.domain.usecase.LoginUseCase
import com.foodkapev.foodsharingrus.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginUseCase = LoginUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding.signupLinkBtn.setOnClickListener {
            val signUpIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

        binding.loginBtn.setOnClickListener {
//            val email = binding.emailLogin.text.toString()
//            val password = binding.passwordLogin.text.toString()
//            val params = LoginUserParam(email, password)
//            loginUseCase.execute(params)
            loginUser()
        }
    }

    private fun loginUser() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        when {
            email.isEmpty() -> Toast.makeText(this, "Поле эл. почты пусто",
                Toast.LENGTH_SHORT).show()
            password.isEmpty() -> Toast.makeText(this, "Поле пароля пусто",
                Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Вход в аккаунт")
                progressDialog.setMessage("Пожалуйста, подождите ...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()

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
    }


    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}