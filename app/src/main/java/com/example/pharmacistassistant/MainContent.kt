package com.example.pharmacistassistant

import android.util.Log
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
    val selectedProductsState by remember { mutableStateOf(selectedProducts) }
    val scope = rememberCoroutineScope()

    val totalCommonsPrice by remember(selectedProductsState) {
        derivedStateOf {
            selectedProductsState.sumOf { it.commonsPrice.toDoubleOrNull() ?: 0.0 }
        }
    }

    LaunchedEffect(selectedProductsState) {
        Log.d("MainContent", "Selected products changed. Count: ${selectedProductsState.size}")
    }

    Column(modifier = modifier.fillMaxSize()) {
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

        Box(modifier = Modifier.weight(1f)) {
            ScannedDataTable(
                scannedData = selectedProductsState,
                selectedColumns = columnSelection.value
            )
        }

        Text(
            text = stringResource(
                id = R.string.total_commons_price,
                String.format(Locale.getDefault(), "%.2f", totalCommonsPrice)
            ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

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