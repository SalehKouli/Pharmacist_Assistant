// UtilityFunctions.kt
package com.example.pharmacistassistant

fun getInitialColumnSelection(): MutableMap<Int, Boolean> {
    return mutableMapOf(
        R.string.barcode to true,
        R.string.trade_name to true,
        R.string.form to true,
        R.string.dosage to true,
        R.string.size to true,
        R.string.factory to true,
        R.string.commons_price to true,
        R.string.quantity_available to true,
        R.string.wholesale_price to true,
        R.string.purchase_price to true
    )
}
