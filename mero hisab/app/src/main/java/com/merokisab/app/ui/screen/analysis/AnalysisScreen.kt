package com.merokisab.app.ui.screen.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.merokisab.app.ui.component.PieChart
import com.merokisab.app.ui.theme.IncomeGreen
import com.merokisab.app.ui.theme.ExpenseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    ledgerId: Int,
    navController: NavHostController,
    viewModel: AnalysisViewModel = viewModel(),
) {
    val state by viewModel.observe(ledgerId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rs. %.2f".format(state.balance),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (state.balance >= 0) IncomeGreen else ExpenseRed,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "All Time",
                style = MaterialTheme.typography.titleMedium,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    PieChart(
                        data = state.incomeByCategory,
                        title = "Income",
                        modifier = Modifier.weight(1f),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    PieChart(
                        data = state.expenseByCategory,
                        title = "Expense",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}