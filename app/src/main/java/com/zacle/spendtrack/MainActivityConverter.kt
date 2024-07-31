package com.zacle.spendtrack

import android.content.Context
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.model.UserData
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MainActivityConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetUserDataUseCase.Response, UserData>() {
    override fun convertSuccess(data: GetUserDataUseCase.Response): UserData = data.userData

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}