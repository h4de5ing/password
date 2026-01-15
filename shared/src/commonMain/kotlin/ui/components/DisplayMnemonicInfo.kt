package com.password.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DisplayMnemonicInfo(dataMap: Map<String, String>) {
    var showWords by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    // 计数有多少个单词
    val wordCount = (1..24).count { dataMap["word_$it"]?.isNotEmpty() == true }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "助记词: ${wordCount}个单词${if (wordCount == 12) " (12字)" else if (wordCount == 24) " (24字)" else ""}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 只有在展开状态下才允许显示/隐藏单词
            if (isExpanded) {
                IconButton(
                    onClick = { showWords = !showWords }, modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (showWords) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showWords) "隐藏单词" else "显示单词",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // 折叠时强制隐藏（保持安全默认）
                if (showWords) showWords = false
            }
            // 展开/折叠矩阵图标
            IconButton(
                onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "折叠矩阵" else "展开矩阵",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // 只在展开状态下显示矩阵
    if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))

        // 矩阵显示单词：12个单词显示为3X4，24个单词显示为3X8
        val columnsCount = 3
        val rowsCount = (wordCount + columnsCount - 1) / columnsCount

        for (row in 0 until rowsCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columnsCount) {
                    val index = row * columnsCount + col + 1
                    val word = if (index <= wordCount) dataMap["word_$index"] ?: "" else ""

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp), contentAlignment = Alignment.Center
                    ) {
                        if (word.isNotEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = index.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // 显示单词或星号
                                Text(
                                    text = if (showWords) word else "●",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
