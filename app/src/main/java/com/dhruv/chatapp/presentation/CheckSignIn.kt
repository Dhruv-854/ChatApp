package com.dhruv.chatapp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.dhruv.chatapp.presentation.navigation.ChatList

@Composable
fun CheckSignIn(
    modifier: Modifier = Modifier,
    vm : LCViewModel,
    navController: NavHostController
) {
    val alreadySignIn = remember {
        mutableStateOf(false)
    }
    val signIn = vm.signIn.value
    LaunchedEffect(signIn) {
        if (signIn && !alreadySignIn.value) {
            alreadySignIn.value = true
            navController.navigate(ChatList) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}