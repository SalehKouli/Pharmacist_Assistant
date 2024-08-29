package com.example.pharmacistassistant

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel,
    drawerState: DrawerState,
    selectedProducts: List<ProductData>,
    columnSelection: MutableState<Map<Int, Boolean>>,
    onReset: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {

        // Button to open/close filters drawer
        Button(
            onClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(stringResource(id = R.string.open_filters))
        }

        // Table
        Box(modifier = Modifier.weight(1f)) {
            ScannedDataTable(
                scannedData = selectedProducts,
                selectedColumns = columnSelection.value
            )
        }

        // Display total commons price
        val totalCommonsPrice = selectedProducts.sumOf { it.commonsPrice.toDoubleOrNull() ?: 0.0 }
        Text(
            text = stringResource(
                id = R.string.total_commons_price,
                String.format(Locale.getDefault(), "%.2f", totalCommonsPrice)
            ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Reset Button
        Button(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(stringResource(id = R.string.reset_table))
        }
    }
}
