package com.password.shared.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PasswordPromptDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (password: String) -> Unit
) {
    var pwd by remember { mutableStateOf("") }
    var showPwd by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = pwd,
                onValueChange = { pwd = it },
                label = { Text("导出/导入密码") },
                singleLine = true,
                visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPwd = !showPwd }) {
                        Text(if (showPwd) "隐藏" else "显示")
                    }
                }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(pwd) },
                enabled = pwd.isNotBlank()
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
