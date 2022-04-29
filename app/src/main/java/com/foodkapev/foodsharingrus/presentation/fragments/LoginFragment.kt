package com.foodkapev.foodsharingrus.presentation.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.ActivityLoginBinding
import com.foodkapev.foodsharingrus.domain.usecase.LoginUseCase
import com.google.firebase.auth.FirebaseAuth

class LoginFragment: Fragment(R.layout.activity_login) {

    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginUseCase = LoginUseCase()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupLinkBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
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
            email.isEmpty() -> Toast.makeText(requireContext(), "Поле эл. почты пусто",
                Toast.LENGTH_SHORT).show()
            password.isEmpty() -> Toast.makeText(requireContext(), "Поле пароля пусто",
                Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Вход в аккаунт")
                progressDialog.setMessage("Пожалуйста, подождите ...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()

                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                    else {
                        val message = task.exception!!.toString()
                        Toast.makeText(requireContext(), "Ошибка: $message", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }

            }

        }
    }
}