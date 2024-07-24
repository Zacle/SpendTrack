package com.zacle.spendtrack.core.model.usecase

sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Error(val exception: UseCaseException): Result<Nothing>()
}