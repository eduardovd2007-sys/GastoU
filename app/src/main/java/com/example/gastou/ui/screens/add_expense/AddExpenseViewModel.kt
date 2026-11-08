package com.example.gastou.ui.screens.add_expense

import androidx.lifecycle.ViewModel
import com.example.gastou.Expense
import com.example.gastou.data.GastoURepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Estado que representa el formulario de "Añadir Gasto"
 */
@RequiresApi(Build.VERSION_CODES.O)
data class AddExpenseUiState(
    val amount: String = "",
    val note: String = "",
    val category: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val paymentMethod: String = "Tarjeta prepago U",
    val receiptUri: String? = null
) {
    // Calculamos el texto de fecha formateado directamente en el estado
    val displayDate: String
        get() {
            val today = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es", "ES"))
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("en", "US"))
            val formattedDate = selectedDate.format(dateFormatter)
            val formattedTime = selectedTime.format(timeFormatter)

            val dateStr = when (selectedDate) {
                today -> "Hoy ($formattedDate)"
                today.minusDays(1) -> "Ayer ($formattedDate)"
                else -> formattedDate
            }
            return "$dateStr • $formattedTime"
        }
}

/**
 * ViewModel que maneja toda la lógica de crear un nuevo gasto.
 */
@RequiresApi(Build.VERSION_CODES.O)
class AddExpenseViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun updateAmount(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount) }
    }

    fun updateNote(newNote: String) {
        _uiState.update { it.copy(note = newNote) }
    }

    fun updateCategory(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun updateDateTime(date: LocalDate, time: LocalTime) {
        _uiState.update { it.copy(selectedDate = date, selectedTime = time) }
    }

    fun updatePaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun updateReceiptUri(uri: String) {
        _uiState.update { it.copy(receiptUri = uri) }
    }

    /**
     * Guarda el gasto en el Repositorio Central y reinicia el formulario.
     * Retorna true si se guardó correctamente.
     */
    fun saveExpense(): Boolean {
        val currentState = _uiState.value
        if (currentState.amount.isNotEmpty() && currentState.category != null) {
            val amountValue = currentState.amount.toDoubleOrNull() ?: 0.0

            val newExpense = Expense(
                amount = amountValue,
                categoryId = currentState.category,
                date = currentState.displayDate,
                paymentMethod = currentState.paymentMethod,
                note = currentState.note,
                receiptUri = currentState.receiptUri,
                dateObj = currentState.selectedDate
            )

            GastoURepository.addExpense(newExpense)

            // Reiniciamos el estado para el próximo gasto
            _uiState.value = AddExpenseUiState()
            return true
        }
        return false
    }

    // Método extra para reiniciar manualmente si el usuario cancela
    fun resetForm() {
        _uiState.value = AddExpenseUiState()
    }
}
