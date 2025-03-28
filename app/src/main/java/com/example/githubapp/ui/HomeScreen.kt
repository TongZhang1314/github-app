package com.example.githubapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.githubapp.CenterProgress
import com.example.githubapp.ErrorMessage
import com.example.githubapp.Topic
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.repository.GitHubRepository
import com.example.githubapp.viewmodel.MainViewModel
import com.example.githubapp.viewmodel.UiState

// 首页
@Composable
fun HomeScreen(viewModel: MainViewModel = provideViewModel()) {
    var selectedTab by remember { mutableStateOf(Tab.HotRepos) }

    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Green,
            contentColor = Color.White
        ) {
            Tab(
                selected = selectedTab == Tab.HotRepos,
                onClick = { selectedTab = Tab.HotRepos }
            ) {
                Text("热门仓库", modifier = Modifier.padding(8.dp))
            }
            Tab(
                selected = selectedTab == Tab.TrendingTopics,
                onClick = { selectedTab = Tab.TrendingTopics }
            ) {
                Text("趋势话题", modifier = Modifier.padding(8.dp))
            }
        }

        when (selectedTab) {
            Tab.HotRepos -> HandleHotReposState(viewModel.hotReposState.value)
            Tab.TrendingTopics -> HandleTrendingTopicsState(viewModel.trendingTopicsState.value)
        }
    }
}


// 定义两个页签
enum class Tab {
    HotRepos, TrendingTopics
}


@Composable
fun HandleHotReposState(state: UiState<List<GitHubRepo>>) {
    when (state) {
        is UiState.Loading -> CenterProgress()
        is UiState.Success -> HotReposList(repos = state.data)
        is UiState.Error -> ErrorMessage(message = state.message)
        is UiState.Idle -> {}
        is UiState.Empty -> {}
    }
}

@Composable
fun HandleTrendingTopicsState(state: UiState<List<Topic>>) {
    when (state) {
        is UiState.Loading -> CenterProgress()
        is UiState.Success -> TrendingTopicsList(topics = state.data)
        is UiState.Error -> ErrorMessage(message = state.message)
        is UiState.Idle -> {}
        is UiState.Empty -> {}
    }
}

@Composable
fun HotReposList(repos: List<GitHubRepo>) {
    LazyColumn {
        items(repos) { repo ->
            GitHubRepoItem(repo = repo)
            Divider()
        }
    }
}

@Composable
fun GitHubRepoItem(repo: GitHubRepo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repo.name)
            Spacer(modifier = Modifier.height(4.dp))
            repo.description?.let {
                Text(text = it)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "stars",
                    modifier = Modifier.size(16.dp)
                )
                Text(text = repo.stargazers_count.toString())
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = repo.language ?: "Unknown")
            }
        }
    }
}


@Composable
fun TrendingTopicsList(topics: List<Topic>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(topics) { topic ->
            TopicItem(topic = topic)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TopicItem(topic: Topic) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
//        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = topic.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${topic.discussionCount} 讨论",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "增长 ${topic.growthRate}",
                    fontSize = 14.sp,
                    color = Color.Green
                )
            }
        }
    }
}


@Composable
fun provideViewModel(): MainViewModel {
    val repository = remember { GitHubRepository() }
    return viewModel { MainViewModel(repository) }
}
