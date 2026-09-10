package com.merokisab.app.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.merokisab.app.util.formatAd
import com.merokisab.app.ui.component.LedgerDialog
import com.merokisab.app.ui.theme.ExpenseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenLedger: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val ledgers by viewModel.ledgers.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedLedger by remember { mutableStateOf<Int?>(null) }
    var showLedgerDialog by remember { mutableStateOf(false) }

    val ledgerToDelete = selectedLedger?.let { id -> ledgers.find { it.id == id } }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLedgerDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New ledger",
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "Ledgers",
                style = MaterialTheme.typography.h2,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(ledgers.size) { index ->
                    val ledger = ledgers[index]
                    LedgerRow(
                        ledger = ledger,
                        index = index + 1,
                        onClick = { onOpenLedger(ledger.id) },
                        onLongClick = {
                            selectedLedger = ledger.id
                            showDeleteDialog = true
                        },
                    )
                }
                if (ledgers.isEmpty()) {
                    item {
                        Text(
                            text = "No ledgers created yet. Tap + to add one.",
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

    // Delete confirmation dialog
    if (showDeleteDialog && ledgerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ledger?") },
            text = { Text("Delete '${ledgerToDelete.name}' and all its entries?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteLedger(ledgerToDelete)
                    showDeleteDialog = false
                    scaffoldState.snackbarHostState.showSnackbar("Deleted")
                }) {
                    Text("Delete", color = ExpenseRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // New ledger dialog
    if (showLedgerDialog) {
        LedgerDialog(
            onLedgerCreated = { name, balance ->
                viewModel.createLedger(name, balance)
                showLedgerDialog = false
            },
            onDismiss = { showLedgerDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerRow(
    ledger: com.merokisab.app.data.entity.Ledger,
    index: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .longClickable(onLongClick = onLongClick)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(24.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp),
            ) {
                Text(
                    text = ledger.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    text = "Created: ${formatAd(ledger.createdDateAD)} • Last edited: ${formatAd(ledger.lastEditedDateAD)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .align(androidx.compose.ui.Alignment.End),
            ) {
                Text(
                    text = formatAd(ledger.lastEditedDateAD),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}