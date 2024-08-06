package com.zacle.spendtrack

import android.content.Context
import com.zacle.spendtrack.core.domain.GetUserDataAndAuthStateUseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.model.UserData
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.ui.CommonResultConverter
import com.zacle.spendtrack.data.UserStateModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MainActivityConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetUserDataAndAuthStateUseCase.Response, UserStateModel>() {
    override fun convertSuccess(data: GetUserDataAndAuthStateUseCase.Response): UserStateModel =
        UserStateModel(
            userData = data.userData,
            userInfo = data.userInfo
        )

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}