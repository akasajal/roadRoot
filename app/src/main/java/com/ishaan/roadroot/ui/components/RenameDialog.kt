package com.ishaan.roadroot.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.ishaan.roadroot.ui.theme.*

@Composable
fun RenameDialog(
    currentName: String,
    label: String = "Rename",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember {
        mutableStateOf(TextFieldValue(currentName, selection = TextRange(0, currentName.length)))
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        title = {
            Text(label, style = MaterialTheme.typography.headlineMedium, color = RROnBackground)
        },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
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
                        if (value.text.isNotBlank()) {
                            onConfirm(value.text)
                            onDismiss()
                        }
                    }
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (value.text.isNotBlank()) {
                        onConfirm(value.text)
                        onDismiss()
                    }
                },
                enabled = value.text.isNotBlank()
            ) {
                Text("Save", color = if (value.text.isNotBlank()) RRAccent else RROnSurfaceMuted)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = RROnSurfaceMuted)
            }
        }
    )
}
