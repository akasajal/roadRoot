package com.ishaan.roadroot.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.ishaan.roadroot.ui.theme.*

@Composable
fun DeleteConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        title = {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = RROnBackground)
        },
        text = {
            Text(message, style = MaterialTheme.typography.bodyMedium, color = RROnSurfaceMuted)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text("Delete", color = RRError)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = RROnSurfaceMuted)
            }
        }
    )
}
