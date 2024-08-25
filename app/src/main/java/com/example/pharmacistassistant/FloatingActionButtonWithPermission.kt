// FloatingActionButtonWithPermission.kt
package com.example.pharmacistassistant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun FloatingActionButtonWithPermission(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Camera, contentDescription = stringResource(id = R.string.scan_button_text))
    }
}
