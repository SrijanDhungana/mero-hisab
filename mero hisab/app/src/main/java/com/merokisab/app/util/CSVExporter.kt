package com.merokisab.app.util

import android.content.Context
import com.merokisab.app.data.entity.Transaction
import java.io.File
import java.io.FileWriter

object CSVExporter {
    fun export(context: Context, ledgerName: String, transactions: List<Transaction>): File? {
        val dir = context.getExternalFilesDir(null) ?: return null
        val safeName = ledgerName.replace(Regex("[^a-zA-Z0-9]"), "_")
        val file = File(dir, "${safeName}_transactions.csv")

        try {
            FileWriter(file).use { writer ->
                writer.append("S.N.,Name,Type,Category,Amount,Date BS,Date AD,Remark\n")
                transactions.forEachIndexed { index, t ->
                    writer.append("${index + 1},")
                    writer.append("\"${t.remark.replace("\"", "\"\"")}\",")
                    writer.append(t.type)
                    writer.append(",\"${t.category.replace("\"", "\"\"")}\",")
                    writer.append("%.2f,".format(t.amount))
                    writer.append("\"${t.dateBS}\",")
                    writer.append("\"${t.dateAD}\"")
                    writer.append("\n")
                }
            }
            return file
        } catch (e: Exception) {
            return null
        }
    }
}