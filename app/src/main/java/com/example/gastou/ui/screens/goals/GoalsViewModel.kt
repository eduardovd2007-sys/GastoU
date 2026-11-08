package com.example.gastou.ui.screens.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastou.Expense
import com.example.gastou.SavingGoal
import com.example.gastou.data.GastoURepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Estado que representa las pantallas de Metas de Ahorro
 */
data class GoalsUiState(
    val savingGoals: List<SavingGoal> = emptyList(),
    val selectedGoalId: String? = null
) {
    // Calculamos la meta seleccionada dinámicamente
    val selectedGoal: SavingGoal?
        get() = savingGoals.find { it.id == selectedGoalId }
}

@RequiresApi(Build.VERSION_CODES.O)
class GoalsViewModel : ViewModel() {

    private val _selectedGoalId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<GoalsUiState> = combine(
        GastoURepository.savingGoals,
        _selectedGoalId
    ) { goalsList, goalId ->
        GoalsUiState(
            savingGoals = goalsList,
            selectedGoalId = goalId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalsUiState()
    )

    fun selectGoal(goalId: String?) {
        _selectedGoalId.value = goalId
    }

    fun saveNewGoal(goal: SavingGoal) {
        GastoURepository.addGoal(goal)
    }

    fun addFundsToGoal(amountToAdd: Double, paymentMethod: String) {
        val currentGoal = uiState.value.selectedGoal ?: return

        // 1. Actualizamos la meta sumando el dinero
        val updatedGoal = currentGoal.copy(currentAmount = currentGoal.currentAmount + amountToAdd)
        GastoURepository.updateGoal(updatedGoal)

        // 2. Registramos ese dinero como un gasto en el historial
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es", "ES"))
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("en", "US"))
        val nowTime = LocalTime.now()
        val nowDate = LocalDate.now()
        val dateStr = "Hoy (${nowDate.format(dateFormatter)}) • ${nowTime.format(timeFormatter)}"

        val expenseEntry = Expense(
            amount = amountToAdd,
            categoryId = "Ahorro e Inversión",
            date = dateStr,
            paymentMethod = paymentMethod,
            note = "Abono a meta: ${currentGoal.name}",
            dateObj = nowDate
        )
        GastoURepository.addExpense(expenseEntry)
    }

    fun markGoalCompleted() {
        val currentGoal = uiState.value.selectedGoal ?: return
        val updatedGoal = currentGoal.copy(isCompleted = true)
        GastoURepository.updateGoal(updatedGoal)
    }

    fun deleteGoal() {
        val goalId = _selectedGoalId.value ?: return
        GastoURepository.removeGoal(goalId)
        _selectedGoalId.value = null
    }
}
