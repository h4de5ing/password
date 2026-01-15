package com.password.shared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.password.shared.db.PasswordItem
import com.password.shared.ui.components.PasswordItemCard
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    passwordListFlow: Flow<List<PasswordItem>>,
    snackbarHost: @Composable () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    onAddClick: () -> Unit = {},
    onItemClick: (PasswordItem) -> Unit = {},
    onEdit: (PasswordItem) -> Unit = {},
    onDelete: (PasswordItem) -> Unit = {}
) {
    val passwordList by passwordListFlow.collectAsState(initial = emptyList())

    // LazyList 状态用于检测滚动
    val lazyListState = rememberLazyListState()
    val showFab by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    Scaffold(snackbarHost = snackbarHost, topBar = {
        CenterAlignedTopAppBar(
            title = { Text("密码本") },
            actions = topBarActions,
        )
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = showFab, enter = scaleIn(), exit = scaleOut()
        ) {
            FloatingActionButton(
                onClick = onAddClick, containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "添加密码"
                )
            }
        }
    }) { paddingValues ->
        if (passwordList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center
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
                modifier = Modifier.fillMaxSize().padding(paddingValues), state = lazyListState
            ) {
                items(passwordList.size) { index ->
                    val item = passwordList[index]
                    PasswordItemCard(
                        passwordItem = item, onItemClick = onItemClick, onEdit = onEdit, onDelete = onDelete
                    )
                }
            }
        }
    }
}
