package com.example.pharmacistassistant

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetContent(columnSelection: MutableState<Map<Int, Boolean>>, sharedPreferences: SharedPreferences) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.select_columns),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        columnSelection.value.keys.forEach { columnName ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        val currentChecked = columnSelection.value[columnName] ?: true
                        columnSelection.value = columnSelection.value.toMutableMap().apply {
                            this[columnName] = !currentChecked
                        }
                        sharedPreferences.edit().putBoolean(columnName.toString(), !currentChecked).apply()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = columnSelection.value[columnName] ?: true,
                    onCheckedChange = { isChecked ->
                        columnSelection.value = columnSelection.value.toMutableMap().apply {
                            this[columnName] = isChecked
                        }
                        sharedPreferences.edit().putBoolean(columnName.toString(), isChecked).apply()
                    }
                )
                Text(
                    text = stringResource(id = columnName),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
