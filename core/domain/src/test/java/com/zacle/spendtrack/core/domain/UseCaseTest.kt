package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UseCaseTest {
    private val configuration = UseCase.Configuration(UnconfinedTestDispatcher())
    private val request = mock<UseCase.Request>()
    private val response = mock<UseCase.Response>()

    private lateinit var useCase: UseCase<UseCase.Request, UseCase.Response>

    @Before
    fun setup() {
        useCase = object : UseCase<UseCase.Request, UseCase.Response>(configuration) {
            override suspend fun process(request: Request): Flow<Response> {
                assertEquals(this@UseCaseTest.request, request)
                return flowOf(response)
            }
        }
    }

    @Test
    fun `test execute return success`() = runTest {
        val result = useCase.execute(request).first()
        assertEquals(Result.Success(response), result)
    }

    @Test
    fun `test execute return unknown error`() = runTest {
        useCase = object: UseCase<UseCase.Request, UseCase.Response>(configuration) {
            override suspend fun process(request: Request): Flow<Response> {
                return flow {
                    throw RuntimeException()
                }
            }
        }
        val result = useCase.execute(request).first()
        assertTrue((result as Result.Error).exception is UseCaseException.UnknownException)
    }
}