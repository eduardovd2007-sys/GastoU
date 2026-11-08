package com.example.gastou.data

import com.example.gastou.Expense
import com.example.gastou.SavingGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repositorio central (Capa de Datos).
 * Por ahora funciona en memoria. En el futuro, aquí conectaríamos Room (Base de datos local)
 * o Firebase sin tener que tocar la UI.
 */
object GastoURepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _monthlyBudget = MutableStateFlow<Double?>(null)
    val monthlyBudget: StateFlow<Double?> = _monthlyBudget.asStateFlow()

    private val _selectedMonth = MutableStateFlow(java.time.YearMonth.now())
    val selectedMonth: StateFlow<java.time.YearMonth> = _selectedMonth.asStateFlow()

    private val _savingGoals = MutableStateFlow<List<SavingGoal>>(emptyList())
    val savingGoals: StateFlow<List<SavingGoal>> = _savingGoals.asStateFlow()

    fun addExpense(expense: Expense) {
        _expenses.update { it + expense }
    }

    fun removeExpense(expenseId: String) {
        _expenses.update { list -> list.filter { it.id != expenseId } }
    }

    fun updateBudget(budget: Double?) {
        _monthlyBudget.value = budget
    }

    fun updateSelectedMonth(month: java.time.YearMonth) {
        _selectedMonth.value = month
    }

    fun addGoal(goal: SavingGoal) {
        _savingGoals.update { it + goal }
    }

    fun updateGoal(updatedGoal: SavingGoal) {
        _savingGoals.update { list -> list.map { if (it.id == updatedGoal.id) updatedGoal else it } }
    }

    fun removeGoal(goalId: String) {
        _savingGoals.update { list -> list.filter { it.id != goalId } }
    }
}
