package com.merokisab.app.ui.screen.ledger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.merokisab.app.data.entity.Transaction
import com.merokisab.app.ui.component.EntryDialog
import com.merokisab.app.ui.component.RenameDialog
import com.merokisab.app.ui.theme.ExpenseRed
import com.merokisab.app.ui.theme.IncomeGreen
import com.merokisab.app.util.CSVExporter
import com.merokisab.app.util.formatAd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    ledgerId: Int,
    navController: NavHostController,
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.observeLedger(ledgerId).collectAsState()
    val showEntryDialog = remember { mutableStateOf(false) }
    val showRenameDialog = remember { mutableStateOf(false) }
    val editingTransaction = remember { mutableStateOf<Transaction?>(null) }
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.ledger?.name ?: "Ledger",
                        modifier = Modifier.clickable { showRenameDialog.value = true },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val ctx = LocalContext.current
                        val file = CSVExporter.export(
                            context = ctx,
                            ledgerName = state.ledger?.name ?: "ledger",
                            transactions = state.transactions,
                        )
                        if (file != null) {
                            scaffoldState.snackbarHostState.showSnackbar("Exported to ${file.name}")
                        }
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEntryDialog.value = true },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.merokisab.app.R.drawable.ic_add),
                    contentDescription = "Add entry",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            state.ledger?.let { ledger ->
                BalanceCard(
                    balance = state.balance,
                    startingBalance = ledger.startingBalance,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(state.transactions.size) { index ->
                    val tx = state.transactions[index]
                    TransactionRow(
                        tx = tx,
                        index = index + 1,
                        onClick = { editingTransaction.value = tx },
                    )
                }
                if (state.transactions.isEmpty()) {
                    item {
                        Text(
                            text = "No transactions yet. Tap + to add one.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                        )
                    }
                }
            }
        }
    }

    if (showEntryDialog.value) {
        EntryDialog(
            ledgerId = ledgerId,
            onDismiss = { showEntryDialog.value = false },
            onSave = { tx -> viewModel.saveTransaction(tx) },
        )
    }

    if (showRenameDialog.value && state.ledger != null) {
        RenameDialog(
            currentName = state.ledger!!.name,
            onDismiss = { showRenameDialog.value = false },
            onRename = { newName -> viewModel.renameLedger(state.ledger!!, newName) },
        )
    }

    editingTransaction.value?.let { tx ->
        TransactionActionsDialog(
            transaction = tx,
            onDismiss = { editingTransaction.value = null },
            onEdit = { editingTransaction.value = null; showEntryDialog.value = true },
            onDelete = {
                viewModel.deleteTransaction(tx)
                editingTransaction.value = null
            },
        )
    }
}

@Composable
fun BalanceCard(
    balance: Double,
    startingBalance: Double,
    onRenameClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                text = "Rs. %.2f".format(balance),
                style = MaterialTheme.typography.headlineMedium,
                color = if (balance >= 0) IncomeGreen else ExpenseRed,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Starting: Rs. %.2f".format(startingBalance),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun TransactionRow(
    tx: Transaction,
    index: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$index.",
                modifier = Modifier.width(24.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = tx.category,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${formatAd(tx.dateAD)} • ${tx.dateBS}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (tx.remark.isNotBlank()) {
                    Text(
                        text = tx.remark,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = if (tx.type == "INCOME") "Rs. %.2f".format(tx.amount) else "Rs. %.2f".format(tx.amount),
                style = MaterialTheme.typography.titleMedium,
                color = if (tx.type == "INCOME") IncomeGreen else ExpenseRed,
                modifier = Modifier.width(96.dp),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
fun TransactionActionsDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transaction") },
        text = {
            Column {
                Text("Type: ${transaction.type}")
                Text("Amount: Rs. %.2f".format(transaction.amount))
                Text("Date: ${transaction.dateBS}")
                Text("Category: ${transaction.category}")
            }
        },
        confirmButton = {
            TextButton(onClick = onEdit) { Text("Edit", color = IncomeGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDelete) { Text("Delete", color = ExpenseRed) }
        },
    )
}