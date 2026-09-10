package com.merokisab.app.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merokisab.app.data.entity.Transaction
import com.merokisab.app.util.todayAd
import com.merokisab.app.util.todayBs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDialog(
    ledgerId: Int,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
) {
    var type by remember { mutableStateOf("INCOME") }
    var category by remember { mutableStateOf("Home") }
    var amountText by remember { mutableStateOf("") }
    var remarkText by remember { mutableStateOf("") }
    val today = todayBs()
    val adDate = formatAd(today.first, today.second, today.third)
    val bsDate = formatBs(today.first, today.second, today.third)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Entry") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Type selector
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = {
                            type = "INCOME"
                            category = "Home"
                        },
                        label = { Text("Income") },
                    )
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = {
                            type = "EXPENSE"
                            category = "Education"
                        },
                        label = { Text("Expense") },
                    )
                }

                // Category selector
                val categories = if (type == "INCOME") {
                    listOf("Home", "Borrow", "Own Income", "Write Yourself")
                } else {
                    listOf("Education", "Travel", "Grocery", "Fastfood", "Rent", "Write Yourself")
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Category", style = MaterialTheme.typography.bodySmall)
                    SingleChoiceCategoryPicker(
                        categories = categories,
                        selected = category,
                        onSelected = { category = it },
                    )
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount (NPR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                    ),
                )

                // Remark
                OutlinedTextField(
                    value = remarkText,
                    onValueChange = { remarkText = it },
                    label = { Text("Remark (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Date (read-only, auto-detected)
                OutlinedTextField(
                    value = "$bsDate (AD: $adDate)",
                    onValueChange = {},
                    label = { Text("Date (BS / AD)") },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@TextButton
                    if (amount > 0) {
                        onSave(
                            Transaction(
                                ledgerId = ledgerId,
                                type = type,
                                category = category,
                                amount = amount,
                                dateAD = adDate,
                                dateBS = bsDate,
                                remark = remarkText.trim(),
                            )
                        )
                        onDismiss()
                    }
                },
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
fun SingleChoiceCategoryPicker(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    categories.forEach { category ->
        Row(
            modifier = androidx.compose.foundation.clickable(onClick = { onSelected(category) })
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = category == selected,
                onClick = { onSelected(category) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(category)
        }
    }
}