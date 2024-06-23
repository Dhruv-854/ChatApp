package com.dhruv.chatapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dhruv.chatapp.presentation.LCViewModel
import com.dhruv.chatapp.presentation.component.ConfirmButton
import com.dhruv.chatapp.presentation.component.InputTextFiled
import com.dhruv.chatapp.presentation.component.InstructionText
import com.dhruv.chatapp.presentation.navigation.ChatList
import com.dhruv.chatapp.presentation.navigation.Signup

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavHostController, vm: LCViewModel) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

//    BackHandler(onBack = {
//        (context as? MainActivity)?.finish()
//    })


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        InputTextFiled(text = email, onValueChange = {email = it}, lable = "Email")
        InputTextFiled(text = password, onValueChange = {password = it}, lable = "Password")
        Spacer(modifier = modifier.height(16.dp))
        ConfirmButton(text = "Login") {

            vm.login(
                email,
                password
            ){
                navController.navigate(ChatList)

            }

        }
        Spacer(modifier = modifier.height(16.dp))
        InstructionText(text = "Don't have an account?->Click here!!") {
            navController.navigate(Signup)
        }

    }
}