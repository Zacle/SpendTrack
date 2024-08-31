package com.zacle.spendtrack.core.domain.category

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.CategoryRepository
import com.zacle.spendtrack.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategoriesUseCase(
    configuration: Configuration,
    private val categoryRepository: CategoryRepository
): UseCase<GetCategoriesUseCase.Request, GetCategoriesUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        categoryRepository.categories()
            .map { categories -> Response(categories) }

    data object Request: UseCase.Request

    data class Response(val categories: List<Category>): UseCase.Response
}