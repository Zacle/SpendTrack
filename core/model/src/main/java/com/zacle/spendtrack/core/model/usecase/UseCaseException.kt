package com.zacle.spendtrack.core.model.usecase

sealed class UseCaseException(cause: Throwable): Throwable(cause) {

    /**
     * If none of the above exception above occurred, throw an exception to let the user
     * know that something unexpected happened
     */
    class UnknownException(cause: Throwable): UseCaseException(cause)

    /**
     * Throws an error if the user email has not been verified yet
     */
    class EmailNotVerifiedException(cause: Throwable): UseCaseException(cause)

    /**
     * Throws an error if the user has not been authenticated
     */
    class NotAuthenticatedException(cause: Throwable): UseCaseException(cause)

    companion object {

        fun createFromThrowable(throwable: Throwable): UseCaseException {
            return if (throwable is UseCaseException) throwable else UnknownException(throwable)
        }
    }
}