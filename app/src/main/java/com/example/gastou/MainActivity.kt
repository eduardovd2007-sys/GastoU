package com.example.gastou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.gastou.ui.screens.LoginScreen
import com.example.gastou.ui.screens.AddExpenseScreen
import com.example.gastou.ui.screens.HomeScreen
import com.example.gastou.ui.screens.LoginScreen
import com.example.gastou.ui.screens.ModifyBudgetScreen
import com.example.gastou.ui.screens.RegisterScreen
import com.example.gastou.ui.screens.SelectCategoryScreen
import com.example.gastou.ui.screens.SelectDateScreen
import com.example.gastou.ui.theme.GastoUTheme
import java.time.YearMonth

enum class Screen {
    Login,
    Register,
    Home,
    ModifyBudget,
    AddExpense,
    SelectCategory,
    SelectDate,
    SelectPaymentMethod,
    History,
    ExpenseDetail,
    Goals,
    AddGoal,
    GoalDetail
}

data class SavingGoal(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val category: String,
    val targetDate: String?,
    val isCompleted: Boolean = false
)

data class Expense(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Double,
    val categoryId: String,
    val date: String,
    val paymentMethod: String,
    val note: String,
    val receiptUri: String? = null,
    val dateObj: java.time.LocalDate = java.time.LocalDate.now()
)

class MainActivity : ComponentActivity() {
    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GastoUTheme {
                var currentScreen by remember { mutableStateOf(Screen.Login) }
                
                // MVVM: Leemos los estados globales desde el Repositorio
                val monthlyBudget by com.example.gastou.data.GastoURepository.monthlyBudget.collectAsState()
                val selectedMonth by com.example.gastou.data.GastoURepository.selectedMonth.collectAsState()
                // MVVM: ViewModel para el Home Screen
                val homeViewModel = remember { com.example.gastou.ui.screens.home.HomeViewModel() }
                val homeUiState by homeViewModel.uiState.collectAsState()
                
                // MVVM: ViewModel para Add Expense
                val addExpenseViewModel = remember { com.example.gastou.ui.screens.add_expense.AddExpenseViewModel() }
                val addExpenseUiState by addExpenseViewModel.uiState.collectAsState()

                // MVVM: ViewModel para Historial y Detalle
                val historyViewModel = remember { com.example.gastou.ui.screens.history.HistoryViewModel() }
                val historyUiState by historyViewModel.uiState.collectAsState()

                // MVVM: ViewModel para Metas de Ahorro
                val goalsViewModel = remember { com.example.gastou.ui.screens.goals.GoalsViewModel() }
                val goalsUiState by goalsViewModel.uiState.collectAsState()
                
                // Form state for adding an expense (MOVIDO AL VIEWMODEL)
                // (Eliminado de MainActivity)

                when (currentScreen) {
                    Screen.Login -> {
                        LoginScreen(
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            onNavigateToHome = { currentScreen = Screen.Home }
                        )
                    }
                    Screen.Register -> {
                        RegisterScreen(
                            onNavigateBack = { currentScreen = Screen.Login },
                            onNavigateToLogin = { currentScreen = Screen.Login }
                        )
                    }
                    Screen.Home -> {
                        HomeScreen(
                            uiState = homeUiState,
                            onMonthSelected = homeViewModel::selectMonth,
                            onNavigateToModifyBudget = { currentScreen = Screen.ModifyBudget },
                            onNavigateToAddExpense = { currentScreen = Screen.AddExpense },
                            onNavigateToHistory = { currentScreen = Screen.History },
                            onNavigateToGoals = { currentScreen = Screen.Goals }
                        )
                    }
                    Screen.ModifyBudget -> {
                        ModifyBudgetScreen(
                            currentBudget = monthlyBudget,
                            selectedMonth = selectedMonth,
                            onMonthSelected = { com.example.gastou.data.GastoURepository.updateSelectedMonth(it) },
                            onSaveBudget = { newBudget ->
                                com.example.gastou.data.GastoURepository.updateBudget(newBudget)
                                currentScreen = Screen.Home
                            },
                            onNavigateBack = { currentScreen = Screen.Home }
                        )
                    }
                    Screen.AddExpense -> {
                        AddExpenseScreen(
                            uiState = addExpenseUiState,
                            onAmountChange = addExpenseViewModel::updateAmount,
                            onNavigateToCategory = { currentScreen = Screen.SelectCategory },
                            onNavigateToDate = { currentScreen = Screen.SelectDate },
                            onNavigateToPaymentMethod = { currentScreen = Screen.SelectPaymentMethod },
                            onNoteChange = addExpenseViewModel::updateNote,
                            onReceiptPhotoTaken = addExpenseViewModel::updateReceiptUri,
                            onSave = {
                                val success = addExpenseViewModel.saveExpense()
                                if (success) {
                                    currentScreen = Screen.Home
                                }
                            },
                            onNavigateHome = {
                                addExpenseViewModel.resetForm()
                                currentScreen = Screen.Home
                            },
                            onNavigateToHistory = {
                                addExpenseViewModel.resetForm()
                                currentScreen = Screen.History
                            },
                            onNavigateToGoals = {
                                addExpenseViewModel.resetForm()
                                currentScreen = Screen.Goals
                            }
                        )
                    }
                    Screen.SelectCategory -> {
                        SelectCategoryScreen(
                            currentCategory = addExpenseUiState.category,
                            onCategorySelected = { 
                                addExpenseViewModel.updateCategory(it) 
                                currentScreen = Screen.AddExpense
                            },
                            onNavigateBack = { currentScreen = Screen.AddExpense }
                        )
                    }
                    Screen.SelectDate -> {
                        SelectDateScreen(
                            currentDate = addExpenseUiState.selectedDate,
                            currentTime = addExpenseUiState.selectedTime,
                            onDateTimeSelected = { date, time -> 
                                addExpenseViewModel.updateDateTime(date, time)
                            },
                            onConfirm = { currentScreen = Screen.AddExpense },
                            onNavigateBack = { currentScreen = Screen.AddExpense }
                        )
                    }
                    Screen.SelectPaymentMethod -> {
                        com.example.gastou.ui.screens.SelectPaymentMethodScreen(
                            currentMethod = addExpenseUiState.paymentMethod,
                            onMethodSelected = {
                                addExpenseViewModel.updatePaymentMethod(it)
                                currentScreen = Screen.AddExpense
                            },
                            onNavigateBack = { currentScreen = Screen.AddExpense }
                        )
                    }
                    Screen.History -> {
                        com.example.gastou.ui.screens.HistoryScreen(
                            uiState = historyUiState,
                            onMonthSelected = historyViewModel::selectMonth,
                            onNavigateToHome = { currentScreen = Screen.Home },
                            onNavigateToAddExpense = { currentScreen = Screen.AddExpense },
                            onNavigateToGoals = { currentScreen = Screen.Goals },
                            onNavigateToExpenseDetail = { id: String -> 
                                historyViewModel.selectExpenseForDetail(id)
                                currentScreen = Screen.ExpenseDetail
                            }
                        )
                    }
                    Screen.ExpenseDetail -> {
                        val expense = historyUiState.selectedExpense
                        if (expense != null) {
                            com.example.gastou.ui.screens.ExpenseDetailScreen(
                                expense = expense,
                                uiState = historyUiState,
                                onNavigateBack = { currentScreen = Screen.History },
                                onDelete = {
                                    historyViewModel.deleteExpense(expense.id)
                                    currentScreen = Screen.History
                                }
                            )
                        } else {
                            currentScreen = Screen.History
                        }
                    }
                    Screen.Goals -> {
                        com.example.gastou.ui.screens.GoalsScreen(
                            uiState = goalsUiState,
                            onNavigateHome = { currentScreen = Screen.Home },
                            onNavigateToAddExpense = { currentScreen = Screen.AddExpense },
                            onNavigateToHistory = { currentScreen = Screen.History },
                            onNavigateToAddGoal = { currentScreen = Screen.AddGoal },
                            onNavigateToGoalDetail = { id -> 
                                goalsViewModel.selectGoal(id)
                                currentScreen = Screen.GoalDetail
                            }
                        )
                    }
                    Screen.AddGoal -> {
                        com.example.gastou.ui.screens.AddGoalScreen(
                            onSaveGoal = { newGoal ->
                                goalsViewModel.saveNewGoal(newGoal)
                                currentScreen = Screen.Goals
                            },
                            onNavigateBack = { currentScreen = Screen.Goals }
                        )
                    }
                    Screen.GoalDetail -> {
                        val goal = goalsUiState.selectedGoal
                        if (goal != null) {
                            com.example.gastou.ui.screens.GoalDetailScreen(
                                uiState = goalsUiState,
                                onNavigateBack = { currentScreen = Screen.Goals },
                                onAddFunds = { amountToAdd, paymentMethod ->
                                    goalsViewModel.addFundsToGoal(amountToAdd, paymentMethod)
                                },
                                onMarkCompleted = {
                                    goalsViewModel.markGoalCompleted()
                                },
                                onDelete = {
                                    goalsViewModel.deleteGoal()
                                    currentScreen = Screen.Goals
                                }
                            )
                        } else {
                            currentScreen = Screen.Goals
                        }
                    }
                }
            }
        }
    }
}
