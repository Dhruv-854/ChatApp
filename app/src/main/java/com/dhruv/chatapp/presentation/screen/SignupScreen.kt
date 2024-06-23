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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dhruv.chatapp.presentation.LCViewModel
import com.dhruv.chatapp.presentation.component.ConfirmButton
import com.dhruv.chatapp.presentation.component.InputTextFiled
import com.dhruv.chatapp.presentation.component.InstructionText
import com.dhruv.chatapp.presentation.navigation.Login

@Composable
fun SignupScreen(modifier: Modifier = Modifier, navController: NavHostController, vm: LCViewModel) {

    var name by remember {
        mutableStateOf("")
    }
    var number by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign Up",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        InputTextFiled(text = name, onValueChange = {name = it}, lable = "Name")
        InputTextFiled(text = number, onValueChange = {number = it}, lable = "Number")
        InputTextFiled(text = email, onValueChange = {email = it}, lable = "Email")
        InputTextFiled(text = password, onValueChange = {password = it}, lable = "Password")
        Spacer(modifier = modifier.height(16.dp))

        ConfirmButton(text = "SignUp") {
            vm.singUp(
                name = name,
                number = number,
                email = email,
                password = password
            ){

                navController.navigate(Login){
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
        }
        Spacer(modifier = modifier.height(16.dp))
        InstructionText(text = "Already User Click here to->Login!!") {
            navController.navigate(Login)
        }
    }

}