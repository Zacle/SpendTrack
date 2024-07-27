package com.zacle.spendtrack.core.ui

import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.usecase.UseCaseException

abstract class CommonResultConverter<T: Any, R: Any> {

    fun convert(result: Result<T>): UiState<R> {
        return when (result) {
            is Result.Success -> UiState.Success(convertSuccess(result.data))
            is Result.Error -> UiState.Error(convertError(result.exception))
        }
    }

    abstract fun convertSuccess(data: T): R

    abstract fun convertError(useCaseException: UseCaseException): String
}