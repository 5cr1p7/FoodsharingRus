package com.foodkapev.foodsharingrus.domain.usecase

import com.foodkapev.foodsharingrus.domain.models.LoginUserParam

class LoginUseCase {

    fun execute(param: LoginUserParam): Boolean {
        if (param.email.isEmpty() || param.password.isEmpty()) {
            return false
        } else {
            return true
        }

    }
}