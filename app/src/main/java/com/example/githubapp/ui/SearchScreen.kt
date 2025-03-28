package com.example.githubapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.githubapp.CenterProgress
import com.example.githubapp.ErrorMessage
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.viewmodel.SearchViewModel
import com.example.githubapp.viewmodel.SearchViewModelFactory
import com.example.githubapp.viewmodel.UiState


// 搜索页面
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory())
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 搜索框
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 搜索结果
        when (val state = viewModel.searchState.value) {
            is UiState.Idle -> ShowHintText("输入编程语言搜索仓库")
            is UiState.Loading -> CenterProgress()
            is UiState.Success -> SearchResultsList(repos = state.data)
            is UiState.Empty -> ShowHintText("没有找到相关仓库")
            is UiState.Error -> ErrorMessage(message = state.message)
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    TextField(
        value = viewModel.searchQuery,
        onValueChange = viewModel::onSearchQueryChanged,
        modifier = modifier,
        placeholder = { Text("输入编程语言，如：Kotlin、Java") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        singleLine = true
    )
}

@Composable
private fun SearchResultsList(repos: List<GitHubRepo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(repos) { repo ->
            SearchResultItem(repo = repo)
            Divider()
        }
    }
}

@Composable
private fun SearchResultItem(repo: GitHubRepo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = repo.name
            )

            Spacer(modifier = Modifier.height(4.dp))

            repo.description?.let {
                Text(
                    text = it
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Star,
                    text = "${repo.stargazers_count}"
                )
                repo.language?.let {
                    InfoChip(
                        icon = Icons.Default.Star,
                        text = it
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text
        )
    }
}

@Composable
private fun ShowHintText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}
