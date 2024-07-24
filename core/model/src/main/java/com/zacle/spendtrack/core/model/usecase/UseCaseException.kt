package com.zacle.spendtrack.core.model.usecase

sealed class UseCaseException(cause: Throwable): Throwable(cause) {

    /**
     * If none of the above exception above occurred, throw an exception to let the user
     * know that something unexpected happened
     */
    class UnknownException(cause: Throwable): UseCaseException(cause)

    companion object {

        fun createFromThrowable(throwable: Throwable): UseCaseException {
            return if (throwable is UseCaseException) throwable else UnknownException(throwable)
        }
    }
}