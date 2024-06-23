package com.dhruv.chatapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dhruv.chatapp.presentation.CheckSignIn
import com.dhruv.chatapp.presentation.LCViewModel
import com.dhruv.chatapp.presentation.screen.ChatListScreen
import com.dhruv.chatapp.presentation.screen.ChatScreen
import com.dhruv.chatapp.presentation.screen.LoginScreen
import com.dhruv.chatapp.presentation.screen.ProfileScreen
import com.dhruv.chatapp.presentation.screen.SignupScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationControl(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: LCViewModel = hiltViewModel()

    NavHost(navController =navController, startDestination = Signup ) {
        composable<Signup> {
            SignupScreen(navController = navController, vm = vm)
        }
        composable<Login> {
            LoginScreen(navController = navController, vm = vm)
        }
        composable<Profile> {
            ProfileScreen(navController = navController, vm = vm)
        }
        composable<ChatList> {
            ChatListScreen(navController = navController, vm = vm)
        }
        composable<Chat> {
            val args = it.toRoute<Chat>()
            ChatScreen(navController = navController, vm = vm , chatId = args.chatId)
        }
    }
    CheckSignIn(vm = vm, navController = navController)
}

@Serializable
object Signup
@Serializable
object Login
@Serializable
object ChatList
@Serializable
object Profile
@Serializable
data class Chat(val chatId: String)


