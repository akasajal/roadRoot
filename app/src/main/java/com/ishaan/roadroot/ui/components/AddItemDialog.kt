package com.ishaan.roadroot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ishaan.roadroot.ui.theme.*

@Composable
fun AddItemDialog(
    contextName: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        title = {
            Text(
                text = "Add to $contextName",
                style = MaterialTheme.typography.headlineMedium,
                color = RROnBackground
            )
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        text = "Name this item…",
                        color = RROnSurfaceMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.titleLarge.copy(color = RROnBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RRAccent,
                    unfocusedBorderColor = RRBorder,
                    cursorColor = RRAccent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (title.isNotBlank()) {
                            onConfirm(title)
                            onDismiss()
                        }
                    }
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(
                    "Add",
                    color = if (title.isNotBlank()) RRAccent else RROnSurfaceMuted
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = RROnSurfaceMuted)
            }
        }
    )
}
