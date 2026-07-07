package com.app.cashtrackapp.screens.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.app.cashtrackapp.entity.EntryTypes

@Composable
fun TypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val normalizedSelectedType = EntryTypes.normalize(selectedType)

    Column(modifier = modifier) {
        Text(
            text = "Tipo",
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EntryTypes.available.forEach { type ->
                val isSelected = normalizedSelectedType == type
                val typeTag = if (type == EntryTypes.CREDIT) {
                    "type_credit"
                } else {
                    "type_debit"
                }

                Row(
                    modifier = Modifier
                        .selectable(
                            selected = isSelected,
                            onClick = { onTypeSelected(type) },
                            role = Role.RadioButton
                        )
                        .testTag(typeTag),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onTypeSelected(type) }
                    )
                    Text(text = type)
                }
            }
        }
    }
}
