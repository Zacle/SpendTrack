package com.zacle.spendtrack.feature.login

import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo

data class LoginModel(
    val userIfo: AuthenticatedUserInfo? = null,
)
