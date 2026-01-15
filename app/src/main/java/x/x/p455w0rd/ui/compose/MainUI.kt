package x.x.p455w0rd.ui.compose

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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import x.x.p455w0rd.app.App
import x.x.p455w0rd.db.PasswordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI() {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPasswordItem by remember { mutableStateOf<PasswordItem?>(null) }
    val dao = App.dao
    val passwordList by dao.observerPasswordItem().collectAsState(initial = emptyList())
    
    // LazyList 状态用于检测滚动
    val lazyListState = rememberLazyListState()
    // 判断是否应该显示 FAB（只有在列表不为空且向下滚动时才隐藏）
    val showFab by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("密码本") })
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        editingPasswordItem = null
                        showAddDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加密码"
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
                    .padding(paddingValues),
                state = lazyListState
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
        AddPasswordDialog(
            passwordItem = editingPasswordItem,
            onDismiss = {
                showAddDialog = false
                editingPasswordItem = null
            },
            onConfirm = { type, dataMap, memo ->
                // 根据类型提取account字段
                val account = when (type) {
                    1 -> dataMap["用户名"] ?: ""  // PASSWORD
                    2 -> dataMap["网站"] ?: ""     // GOOGLE_AUTH
                    3 -> dataMap["word_1"] ?: ""   // MNEMONIC (第一个助记词)
                    4 -> dataMap["卡号"] ?: ""     // BANK_CARD
                    5 -> dataMap["姓名"] ?: ""     // ID_CARD
                    else -> ""
                }

                val newItem = PasswordItem(
                    id = editingPasswordItem?.id ?: 0,
                    type = type,
                    account = account,
                    password = dataMap["密码"] ?: "",
                    memoInfo = memo,
                    dataJson = "" // 将在setDataMap时设置
                ).apply {
                    setDataMap(dataMap)
                }

                if (editingPasswordItem != null) {
                    dao.update(newItem)
                } else {
                    dao.insert(newItem)
                }
            }
        )
    }
}