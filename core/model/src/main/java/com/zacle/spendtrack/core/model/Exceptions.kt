package com.zacle.spendtrack.core.model

object Exceptions {
    class FirebaseUserNotFoundException : Exception()
    class FirebaseUserNotCreatedException : Exception()
    class FirebaseBudgetNotCreatedException: Exception()
    class FirebaseBudgetNotUpdatedException: Exception()
    class ExpenseNotFoundException: Exception()
    class IncomeNotFoundException: Exception()
    class BudgetNotFoundException : Exception()
    class CategoryBudgetNotExistsException : Exception()
}