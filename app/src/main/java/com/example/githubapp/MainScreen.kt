package com.example.githubapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.githubapp.repository.AuthRepository
import com.example.githubapp.ui.HomeScreen
import com.example.githubapp.ui.LoginScreen
import com.example.githubapp.ui.ProfileScreen
import com.example.githubapp.ui.SearchScreen
import com.example.githubapp.viewmodel.UserViewModel
import com.example.githubapp.viewmodel.provideUserViewModel


// 底部导航项定义
sealed class BottomTab(val title: String, val icon: Int) {
    object Home : BottomTab("首页", R.drawable.ic_home)
    object Search : BottomTab("搜索", R.drawable.ic_search)
    object Profile : BottomTab("我的", R.drawable.ic_profile)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val userViewModel = provideUserViewModel()
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainTabsScreen(
                onProfileClick = {
                    navController.navigate("login")
                },
                userViewModel
            )
            userViewModel.reloadUser()
            userViewModel.loadRepositories()
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // 先返回至profile页面
                    navController.popBackStack()
                    // 重新加载用户数据
                    val userViewModel: UserViewModel = UserViewModel(AuthRepository())
                    userViewModel.reloadUser()
                    userViewModel.loadRepositories()
                },
                onLoginCancel = { navController.popBackStack() },
                userViewModel
            )
        }
    }

}

// 修改主页导航
@Composable
fun MainTabsScreen(onProfileClick: () -> Unit, userViewModel: UserViewModel) {

    var selectedTab by remember { mutableStateOf<BottomTab>(BottomTab.Home) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.Gray,
                contentColor = Color.White
            ) {
                BottomNavigationItem(
                    selected = selectedTab == BottomTab.Home,
                    onClick = { selectedTab = BottomTab.Home },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomTab.Home.icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = BottomTab.Home.title,
                            fontSize = 12.sp
                        )
                    }
                )

                BottomNavigationItem(
                    selected = selectedTab == BottomTab.Search,
                    onClick = { selectedTab = BottomTab.Search },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomTab.Search.icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = BottomTab.Search.title,
                            fontSize = 12.sp
                        )
                    }
                )

                BottomNavigationItem(
                    selected = selectedTab == BottomTab.Profile,
                    onClick = { selectedTab = BottomTab.Profile },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomTab.Profile.icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = BottomTab.Profile.title,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                is BottomTab.Home -> HomeScreen()
                is BottomTab.Search -> SearchScreen()
                is BottomTab.Profile -> ProfileScreen(onProfileClick,userViewModel)
            }
        }
    }
}

