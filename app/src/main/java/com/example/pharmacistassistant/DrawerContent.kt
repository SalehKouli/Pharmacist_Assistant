// DrawerContent.kt
package com.example.pharmacistassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(columnSelection: MutableState<Map<Int, Boolean>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.select_columns))
        columnSelection.value.keys.forEach { columnName ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = columnSelection.value[columnName] ?: true,
                    onCheckedChange = { isChecked ->
                        columnSelection.value = columnSelection.value.toMutableMap().apply {
                            this[columnName] = isChecked
                        }
                    }
                )
                Text(text = stringResource(id = columnName))
            }
        }
    }
}
