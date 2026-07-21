package com.ishaan.roadroot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.ui.theme.*

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, accent: ProjectAccent) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAccent by remember { mutableStateOf(ProjectAccent.GREEN) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        title = {
            Text(
                "New project",
                style = MaterialTheme.typography.headlineMedium,
                color = RROnBackground
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text(
                            "Project name…",
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
                        focusedBorderColor = selectedAccent.color,
                        unfocusedBorderColor = RRBorder,
                        cursorColor = selectedAccent.color
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (name.isNotBlank()) {
                                onConfirm(name, selectedAccent)
                                onDismiss()
                            }
                        }
                    )
                )

                Text(
                    "COLOR",
                    style = MaterialTheme.typography.labelMedium,
                    color = RROnSurfaceMuted
                )
                ColorPickerRow(
                    selected = selectedAccent,
                    onSelect = { selectedAccent = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedAccent)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(
                    "Create",
                    color = if (name.isNotBlank()) selectedAccent.color else RROnSurfaceMuted
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
