package com.zacle.spendtrack.data

import com.zacle.spendtrack.core.model.UserData
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo

data class UserStateModel(
    val userData: UserData,
    val userInfo: AuthenticatedUserInfo?
)
