package com.example.gastou.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastou.Expense
import com.example.gastou.data.GastoURepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

import com.example.gastou.SavingGoal

/**
 * Estado que representa TODO lo que necesita pintar el HomeScreen
 */
data class HomeUiState(
    val monthlyBudget: Double? = null,
    val selectedMonth: YearMonth = YearMonth.now(),
    val expenses: List<Expense> = emptyList(),
    val savingGoals: List<SavingGoal> = emptyList(),
    val totalSpent: Double = 0.0
)

/**
 * ViewModel para el HomeScreen (Capa de Presentación)
 * Convierte los datos del Repositorio en un estado listo para la vista.
 */
class HomeViewModel : ViewModel() {

    // Combinamos los datos del repositorio con nuestro estado local
    val uiState: StateFlow<HomeUiState> = combine(
        GastoURepository.monthlyBudget,
        GastoURepository.selectedMonth,
        GastoURepository.expenses,
        GastoURepository.savingGoals
    ) { budget, month, expensesList, goalsList ->

        // Filtramos gastos por el mes seleccionado
        val currentMonthExpenses = expensesList.filter { 
            YearMonth.from(it.dateObj) == month 
        }
        
        // Filtramos metas (Por ahora incluimos todas las que no están completadas O las que se completaron en este mes)
        // Como no guardamos la fecha exacta de "cuando se completó", pasaremos todas para que el PDF decida
        val activeGoals = goalsList
        
        // Aquí va la lógica de negocio (ej: sumar gastos del mes)
        val total = currentMonthExpenses.sumOf { it.amount }
        
        HomeUiState(
            monthlyBudget = budget,
            selectedMonth = month,
            expenses = currentMonthExpenses,
            savingGoals = activeGoals,
            totalSpent = total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    // Eventos que la UI puede lanzar
    fun selectMonth(month: YearMonth) {
        GastoURepository.updateSelectedMonth(month)
    }
}
