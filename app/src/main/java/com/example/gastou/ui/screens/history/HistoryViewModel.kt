package com.example.gastou.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastou.Expense
import com.example.gastou.data.GastoURepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Estado que representa TODO lo que necesitan las pantallas de Historial y Detalle de Gasto
 */
@RequiresApi(Build.VERSION_CODES.O)
data class HistoryUiState(
    val expenses: List<Expense> = emptyList(),
    val selectedMonth: YearMonth = YearMonth.now(),
    val monthlyBudget: Double? = null,
    val selectedExpenseId: String? = null
) {
    // Calculamos el gasto seleccionado dinámicamente
    val selectedExpense: Expense?
        get() = expenses.find { it.id == selectedExpenseId }

    // Calculamos el total gastado en la categoría del gasto seleccionado
    val selectedExpenseCategoryTotal: Double
        get() = selectedExpense?.let { exp ->
            expenses.filter { it.categoryId == exp.categoryId }.sumOf { it.amount }
        } ?: 0.0
}

/**
 * ViewModel para el historial de gastos y sus detalles
 */
@RequiresApi(Build.VERSION_CODES.O)
class HistoryViewModel : ViewModel() {

    // Estado local para saber qué gasto estamos viendo en detalle
    private val _selectedExpenseId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        GastoURepository.expenses,
        GastoURepository.selectedMonth,
        GastoURepository.monthlyBudget,
        _selectedExpenseId
    ) { expensesList, month, budget, expenseId ->
        
        // Filtramos por el mes seleccionado
        val currentMonthExpenses = expensesList.filter { 
            YearMonth.from(it.dateObj) == month 
        }

        HistoryUiState(
            expenses = currentMonthExpenses,
            selectedMonth = month,
            monthlyBudget = budget,
            selectedExpenseId = expenseId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    // Eventos que la UI puede lanzar
    fun selectMonth(month: YearMonth) {
        GastoURepository.updateSelectedMonth(month)
    }

    fun selectExpenseForDetail(expenseId: String?) {
        _selectedExpenseId.value = expenseId
    }

    fun deleteExpense(expenseId: String) {
        GastoURepository.removeExpense(expenseId)
        // Limpiamos la selección si lo acabamos de eliminar
        if (_selectedExpenseId.value == expenseId) {
            _selectedExpenseId.value = null
        }
    }
}
