package com.app.cashtrackapp.screens.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun DateField(
    dateMillis: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val dateText = remember(dateMillis, locale) {
        SimpleDateFormat("dd/MM/yyyy", locale).format(Date(dateMillis))
    }

    OutlinedTextField(
        value = dateText,
        onValueChange = {},
        label = { Text("Data Lançamento") },
        readOnly = true,
        modifier = modifier.clickable {
            val cal = Calendar.getInstance()
            cal.timeInMillis = dateMillis
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val newCal = Calendar.getInstance()
                    newCal.set(year, month, dayOfMonth)
                    onDateSelected(newCal.timeInMillis)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    )
}
