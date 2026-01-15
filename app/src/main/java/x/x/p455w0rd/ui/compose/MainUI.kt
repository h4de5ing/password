package x.x.p455w0rd.ui.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import x.x.p455w0rd.app.App
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.security.ExportCrypto
import x.x.p455w0rd.security.ImportExportService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPasswordItem by remember { mutableStateOf<PasswordItem?>(null) }

    // topBar 菜单
    var showTopMenu by remember { mutableStateOf(false) }

    // 导入/导出密码弹窗
    var showExportPwdDialog by remember { mutableStateOf(false) }
    var showImportPwdDialog by remember { mutableStateOf(false) }

    // 导出：暂存用户输入的口令（先输入口令，再选择路径）
    var pendingExportPassword by remember { mutableStateOf<String?>(null) }
    // 导出：暂存最终选择的 Uri
    var pendingExportUri by remember { mutableStateOf<Uri?>(null) }

    // 导入：暂存选中的 Uri
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val dao = App.dao
    val passwordList by dao.observerPasswordItem().collectAsState(initial = emptyList())

    fun showMsg(msg: String) {
        scope.launch { snackbarHostState.showSnackbar(msg) }
    }

    fun writeTextToUri(uri: Uri, text: String) {
        context.contentResolver.openOutputStream(uri)?.use { os ->
            os.write(text.toByteArray(Charsets.UTF_8))
            os.flush()
        }
    }

    fun readTextFromUri(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)
            ?.use { it.readBytes().toString(Charsets.UTF_8) } ?: ""
    }

    // 选择导入文件
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            showImportPwdDialog = true
        }
    }

    // 选择导出文件位置（导出流程：密码已先输入并暂存到 pendingExportPassword）
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            pendingExportUri = uri

            val pwd = pendingExportPassword
            val exportUri = pendingExportUri

            // 立刻清理状态，避免二次触发
            pendingExportPassword = null
            pendingExportUri = null

            if (pwd.isNullOrBlank() || exportUri == null) {
                showMsg("导出失败：缺少密码或文件")
                return@rememberLauncherForActivityResult
            }

            try {
                val json = ImportExportService.exportJson(dao)
                val encrypted = ExportCrypto.encryptJsonWithPassword(json, pwd.toCharArray())
                writeTextToUri(exportUri, encrypted)
                showMsg("导出成功")
            } catch (e: Exception) {
                showMsg("导出失败：${e.message ?: e::class.java.simpleName}")
            }
        } else {
            // 用户取消选择路径
            pendingExportPassword = null
            pendingExportUri = null
        }
    }

    // LazyList 状态用于检测滚动
    val lazyListState = rememberLazyListState()
    val showFab by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("密码本") },
                actions = {
                    IconButton(onClick = { showTopMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "菜单")
                    }
                    DropdownMenu(
                        expanded = showTopMenu,
                        onDismissRequest = { showTopMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("导出(加密)") },
                            onClick = {
                                showTopMenu = false
                                // 导出：先输入密码再选择路径
                                showExportPwdDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("导入(加密)") },
                            onClick = {
                                showTopMenu = false
                                // 导入：先选文件再输入密码
                                importLauncher.launch(arrayOf("*/*"))
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab, enter = scaleIn(), exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        editingPasswordItem = null
                    }, containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = "添加密码"
                    )
                }
            }
        }
    ) { paddingValues ->
        if (passwordList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "还没有保存的密码",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "点击右下角按钮添加第一个密码",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), state = lazyListState
            ) {
                items(passwordList.size) {
                    val item = passwordList[it]
                    PasswordItemCard(
                        passwordItem = item,
                        onItemClick = {},
                        onEdit = {
                            editingPasswordItem = item
                            showAddDialog = true
                        },
                        onDelete = { dao.delete(item) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPasswordDialog(passwordItem = editingPasswordItem, onDismiss = {
            showAddDialog = false
            editingPasswordItem = null
        }, onConfirm = { type, dataMap, memo ->
            val account = when (type) {
                1 -> dataMap["用户名"] ?: ""
                2 -> dataMap["网站"] ?: ""
                3 -> dataMap["word_1"] ?: ""
                4 -> dataMap["卡号"] ?: ""
                5 -> dataMap["姓名"] ?: ""
                else -> ""
            }

            val newItem = PasswordItem(
                id = editingPasswordItem?.id ?: 0,
                type = type,
                account = account,
                password = dataMap["密码"] ?: "",
                memoInfo = memo,
                dataJson = ""
            ).apply {
                setDataMap(dataMap)
            }

            if (editingPasswordItem != null) {
                dao.update(newItem)
            } else {
                dao.insert(newItem)
            }
        })
    }

    // 导出：先输入密码，再弹出系统保存路径选择
    if (showExportPwdDialog) {
        PasswordPromptDialog(
            title = "加密导出",
            confirmText = "下一步",
            onDismiss = {
                showExportPwdDialog = false
                pendingExportPassword = null
                pendingExportUri = null
            },
            onConfirm = { pwd ->
                showExportPwdDialog = false
                pendingExportPassword = pwd

                // 选择保存位置
                exportLauncher.launch("password_backup.p455w0rd")
            }
        )
    }

    // 导入：先选择文件，再输入密码并执行导入
    if (showImportPwdDialog) {
        PasswordPromptDialog(
            title = "加密导入",
            confirmText = "导入",
            onDismiss = {
                showImportPwdDialog = false
                pendingImportUri = null
            },
            onConfirm = { pwd ->
                val uri = pendingImportUri
                showImportPwdDialog = false
                pendingImportUri = null

                if (uri == null) {
                    showMsg("导入失败：未选择文件")
                    return@PasswordPromptDialog
                }

                try {
                    val encrypted = readTextFromUri(uri)
                    val json = ExportCrypto.decryptJsonWithPassword(encrypted, pwd.toCharArray())
                    ImportExportService.importJson(dao, json)
                    showMsg("导入成功")
                } catch (e: Exception) {
                    showMsg("导入失败：${e.message ?: e::class.java.simpleName}")
                }
            }
        )
    }
}