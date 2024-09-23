package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber

abstract class UseCase<I: UseCase.Request, O: UseCase.Response>(private val configuration: Configuration) {

    suspend fun execute(request: I) = process(request)
        .map<O, Result<O>> {
            Result.Success(it)
        }
        .flowOn(configuration.dispatcher)
        .catch {
            Timber.e(it)
            emit(Result.Error(UseCaseException.createFromThrowable(it)))
        }

    internal abstract suspend fun process(request: I): Flow<O>

    class Configuration(val dispatcher: CoroutineDispatcher)

    interface Request

    interface Response
}