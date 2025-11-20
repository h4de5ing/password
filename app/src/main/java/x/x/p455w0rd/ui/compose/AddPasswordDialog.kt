package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.db.PasswordType

@Composable
fun AddPasswordDialog(
    passwordItem: PasswordItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (type: Int, title: String, dataMap: Map<String, String>, memo: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(passwordItem?.getPasswordType() ?: PasswordType.PASSWORD) }
    var showTypeDropdown by remember { mutableStateOf(false) }
    
    // 为每个字段都创建独立的状态
    var formDataState by remember {
        mutableStateOf(
            if (passwordItem != null) {
                val map = passwordItem.getDataMap().toMutableMap()
                map["标题"] = passwordItem.title
                map["备注"] = passwordItem.memoInfo
                map
            } else {
                mapOf()
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (passwordItem == null) "添加密码" else "编辑密码") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 类型选择器
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showTypeDropdown = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("类型: ${selectedType.displayName}")
                    }
                    DropdownMenu(
                        expanded = showTypeDropdown,
                        onDismissRequest = { showTypeDropdown = false }
                    ) {
                        for (type in PasswordType.entries) {
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    showTypeDropdown = false
                                    formDataState = mapOf() // 清空表单数据
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 动态表单字段
                when (selectedType) {
                    PasswordType.PASSWORD -> {
                        PasswordFormFields(formDataState) { formDataState = it }
                    }

                    PasswordType.GOOGLE_AUTH -> {
                        GoogleAuthFormFields(formDataState) { formDataState = it }
                    }

                    PasswordType.MNEMONIC -> {
                        MnemonicFormFields(formDataState) { formDataState = it }
                    }

                    PasswordType.BANK_CARD -> {
                        BankCardFormFields(formDataState) { formDataState = it }
                    }

                    PasswordType.ID_CARD -> {
                        IdCardFormFields(formDataState) { formDataState = it }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val title = formDataState["标题"]?.takeIf { it.isNotBlank() }
                    val memo = formDataState["备注"] ?: ""
                    val dataMap = formDataState.filterKeys { it != "备注" }
                    if (title != null && dataMap.isNotEmpty()) {
                        onConfirm(
                            selectedType.id,
                            title,
                            dataMap,
                            memo
                        )
                        onDismiss()
                    }
                },
                enabled = formDataState["标题"]?.isNotBlank() == true &&
                        formDataState.size > 1
            ) {
                Text(if (passwordItem == null) "添加" else "保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun PasswordFormFields(
    formData: Map<String, String>,
    onFormDataChange: (Map<String, String>) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    FormTextField(
        label = "标题*",
        value = formData["标题"] ?: "",
        onValueChange = { onFormDataChange(formData + ("标题" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "用户名*",
        value = formData["用户名"] ?: "",
        onValueChange = { onFormDataChange(formData + ("用户名" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    PasswordFormTextField(
        label = "密码*",
        value = formData["密码"] ?: "",
        onValueChange = { onFormDataChange(formData + ("密码" to it)) },
        showPassword = showPassword,
        onShowPasswordChange = { showPassword = it }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "网站",
        value = formData["网站"] ?: "",
        onValueChange = { onFormDataChange(formData + ("网站" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "备注",
        value = formData["备注"] ?: "",
        onValueChange = { onFormDataChange(formData + ("备注" to it)) },
        minLines = 2
    )
}

@Composable
private fun GoogleAuthFormFields(
    formData: Map<String, String>,
    onFormDataChange: (Map<String, String>) -> Unit
) {
    FormTextField(
        label = "标题*",
        value = formData["标题"] ?: "",
        onValueChange = { onFormDataChange(formData + ("标题" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "网站*",
        value = formData["网站"] ?: "",
        onValueChange = { onFormDataChange(formData + ("网站" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "恢复代码",
        value = formData["恢复代码"] ?: "",
        onValueChange = { onFormDataChange(formData + ("恢复代码" to it)) },
        minLines = 2
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "备注",
        value = formData["备注"] ?: "",
        onValueChange = { onFormDataChange(formData + ("备注" to it)) },
        minLines = 2
    )
}

@Composable
private fun MnemonicFormFields(
    formData: Map<String, String>,
    onFormDataChange: (Map<String, String>) -> Unit
) {
    var mnemonicCount by remember { mutableStateOf(12) }

    FormTextField(
        label = "标题*",
        value = formData["标题"] ?: "",
        onValueChange = { onFormDataChange(formData + ("标题" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    // 选择12或24个助记词
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { mnemonicCount = 12 },
            modifier = Modifier.weight(1f),
            colors = if (mnemonicCount == 12) androidx.compose.material3.ButtonDefaults.buttonColors()
            else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
        ) {
            Text("12个单词")
        }
        Button(
            onClick = { mnemonicCount = 24 },
            modifier = Modifier.weight(1f),
            colors = if (mnemonicCount == 24) androidx.compose.material3.ButtonDefaults.buttonColors()
            else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
        ) {
            Text("24个单词")
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // 显示12或24个输入框
    for (i in 1..mnemonicCount) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$i",
                modifier = Modifier.width(24.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = formData["word_$i"] ?: "",
                onValueChange = { onFormDataChange(formData + ("word_$i" to it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "备注",
        value = formData["备注"] ?: "",
        onValueChange = { onFormDataChange(formData + ("备注" to it)) },
        minLines = 2
    )
}

@Composable
private fun BankCardFormFields(
    formData: Map<String, String>,
    onFormDataChange: (Map<String, String>) -> Unit
) {
    var cardType by remember { mutableStateOf(formData["卡类型"] ?: "信用卡") }
    var showCardNumber by remember { mutableStateOf(false) }
    var showCvv by remember { mutableStateOf(false) }

    FormTextField(
        label = "标题*",
        value = formData["标题"] ?: "",
        onValueChange = { onFormDataChange(formData + ("标题" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    // 卡类型选择
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                cardType = "信用卡"
                onFormDataChange(formData + ("卡类型" to "信用卡"))
            },
            modifier = Modifier.weight(1f),
            colors = if (cardType == "信用卡") ButtonDefaults.buttonColors()
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("信用卡")
        }
        Button(
            onClick = {
                cardType = "借记卡"
                onFormDataChange(formData + ("卡类型" to "借记卡"))
            },
            modifier = Modifier.weight(1f),
            colors = if (cardType == "借记卡") ButtonDefaults.buttonColors()
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("借记卡")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "银行名称*",
        value = formData["银行名称"] ?: "",
        onValueChange = { onFormDataChange(formData + ("银行名称" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    PasswordFormTextField(
        label = "卡号*",
        value = formData["卡号"] ?: "",
        onValueChange = { onFormDataChange(formData + ("卡号" to it)) },
        showPassword = showCardNumber,
        onShowPasswordChange = { showCardNumber = it }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "持卡人*",
        value = formData["持卡人"] ?: "",
        onValueChange = { onFormDataChange(formData + ("持卡人" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    // CVV 只在信用卡时显示
    if (cardType == "信用卡") {
        PasswordFormTextField(
            label = "CVV*",
            value = formData["CVV"] ?: "",
            onValueChange = { onFormDataChange(formData + ("CVV" to it)) },
            showPassword = showCvv,
            onShowPasswordChange = { showCvv = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    FormTextField(
        label = "有效期*",
        value = formData["有效期"] ?: "",
        onValueChange = { onFormDataChange(formData + ("有效期" to it)) },
        placeholder = "MM/YY"
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "开户行",
        value = formData["开户行"] ?: "",
        onValueChange = { onFormDataChange(formData + ("开户行" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "备注",
        value = formData["备注"] ?: "",
        onValueChange = { onFormDataChange(formData + ("备注" to it)) },
        minLines = 2
    )
}

@Composable
private fun IdCardFormFields(
    formData: Map<String, String>,
    onFormDataChange: (Map<String, String>) -> Unit
) {
    FormTextField(
        label = "标题",
        value = formData["标题"] ?: "",
        onValueChange = { onFormDataChange(formData + ("标题" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "姓名*",
        value = formData["姓名"] ?: "",
        onValueChange = { onFormDataChange(formData + ("姓名" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "身份证号码*",
        value = formData["身份证号码"] ?: "",
        onValueChange = { onFormDataChange(formData + ("身份证号码" to it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "地址",
        value = formData["地址"] ?: "",
        onValueChange = { onFormDataChange(formData + ("地址" to it)) },
        minLines = 2
    )
    Spacer(modifier = Modifier.height(8.dp))

    FormTextField(
        label = "备注",
        value = formData["备注"] ?: "",
        onValueChange = { onFormDataChange(formData + ("备注" to it)) },
        minLines = 2
    )
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        singleLine = minLines == 1,
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder) }
        } else null
    )
}

@Composable
private fun PasswordFormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (showPassword) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onShowPasswordChange(!showPassword) }) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        minLines = minLines,
        singleLine = minLines == 1
    )
}