package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
    val dao = App.dao
    val passwordList by dao.observerPasswordItem().collectAsState(initial = emptyList())
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("密码本") })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "添加密码"
            )
        }
    }) { paddingValues ->
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
                    .padding(paddingValues)
            ) {
                items(passwordList.size) {
                    PasswordItemCard(
                        passwordItem = passwordList[it],
                        onItemClick = {},
                        onDelete = {},
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPasswordDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, username, password, website, notes ->
                dao.insert(PasswordItem(0, 0, title, username, password, "$notes"))
            })
    }
}