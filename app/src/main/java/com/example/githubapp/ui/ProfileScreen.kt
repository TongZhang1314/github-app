package com.example.githubapp.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.githubapp.viewmodel.UserViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.githubapp.CenterProgress
import com.example.githubapp.ErrorMessage
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.viewmodel.IssueViewModel
import com.example.githubapp.viewmodel.UiState
import com.example.githubapp.viewmodel.provideIssueViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// 个人页面
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: UserViewModel
) {
    val user = viewModel.userState
    var showLogoutDialog by remember { mutableStateOf(false) } // 控制对话框显示

    // 注销确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("确认注销") },
            text = { Text("确定要退出当前账号吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    }
                ) {
                    Text("确定", color = Color.Blue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("取消", color = Color.Red)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable {
                    if (user != null) {
                        // 已登录：显示注销对话框
                        showLogoutDialog = true
                    } else {
                        // 未登录：跳转登录页
                        onNavigateToLogin()
                    }
                }
        ) {
            if (user != null) {
                AsyncImage(
                    model = user.avatar_url,
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "默认头像",
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (user != null) {
            Text(
                text = user.login,
                style = MaterialTheme.typography.bodySmall
            )
            user.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("公开仓库数: ${user.repos}")
            Text("点击头像进行注销")

            // 仓库列表区域
            Text(
                text = "我的仓库",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when (val state = viewModel.repoState.value) {
                is UiState.Idle -> Unit
                is UiState.Loading -> CenterProgress()
                is UiState.Success -> RepoList(repos = state.data)
                is UiState.Empty -> Text("暂无公开仓库")
                is UiState.Error -> ErrorMessage(state.message)
            }

        } else {
            Text(
                text = "点击头像登录",
                color = Color.Gray
            )
        }

    }
}
// 仓库列表组件
@Composable
private fun RepoList(repos: List<GitHubRepo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
    ) {
        items(repos) { repo ->
            RepoListItem(repo = repo)
            Divider()
        }
    }
}

// 单个仓库项组件
@Composable
private fun RepoListItem(
    repo: GitHubRepo,
    issueViewModel: IssueViewModel = provideIssueViewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
//                issueViewModel.showCreateDialog(repo.owner.login, repo.name)
                issueViewModel.showCreateDialog(repo.name, repo.name)
            }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = repo.language ?: "Unknown",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            repo.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoBadge(
                    icon = Icons.Default.Star,
                    text = "${repo.stargazers_count} stars"
                )
                Text(
                    text = "更新于 ${formatDate(repo.updatedAt)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }

    val context = LocalContext.current

    // 观察成功状态
    LaunchedEffect(issueViewModel.showSuccessToast) {
        if (issueViewModel.showSuccessToast) {
            Toast.makeText(
                context,
                "Issue 提交成功!",
                Toast.LENGTH_SHORT
            ).show()
            issueViewModel.resetToastState()
        }
    }

    if (issueViewModel.showDialog) {
        CreateIssueDialog(
            viewModel = issueViewModel,
            onDismiss = {
                issueViewModel.showDialog = false
                issueViewModel.issueState.value = null
            }
        )
    }

    // 处理提交结果
    when (val state = issueViewModel.issueState.value) {
        is UiState.Error -> {
            Snackbar {
                Text(state.message)
            }
            issueViewModel.issueState.value = null
        }
        else -> Unit
    }
}

// 日期格式化工具
private fun formatDate(isoDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date)
    } catch (e: Exception) {
        "未知时间"
    }
}

// 信息徽章组件
@Composable
private fun InfoBadge(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CreateIssueDialog(
    viewModel: IssueViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建 Issue") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容描述") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            when (val state = viewModel.issueState.value) {
                is UiState.Loading -> CircularProgressIndicator()
                else -> Button(
                    onClick = { viewModel.createIssue(title, content) },
                    enabled = title.isNotBlank()
                ) {
                    Text("提交")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}