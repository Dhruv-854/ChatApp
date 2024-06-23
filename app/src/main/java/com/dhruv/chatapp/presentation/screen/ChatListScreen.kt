package com.dhruv.chatapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dhruv.chatapp.presentation.LCViewModel
import com.dhruv.chatapp.presentation.component.ConfirmButton
import com.dhruv.chatapp.presentation.component.TopAppBarComponent
import com.dhruv.chatapp.presentation.navigation.Chat
import com.dhruv.chatapp.presentation.navigation.Profile

@Composable
fun ChatListScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    vm: LCViewModel,
) {
    val inProcessChat = vm.inProcessChat
    val chats = vm.chats.value
    val userData = vm.userData.value
    val showDialog = remember {
        mutableStateOf(false)
    }


    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "ChatApp",
                icon = Icons.Default.Person
            ) {
                navController.navigate(Profile)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog.value = true
            }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {

            if (chats.isEmpty()) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No Chats Available")
                }
            } else {

                LazyColumn {
                    items(chats){ chat ->
                        val chatUser = if (chat.user1.userId == userData?.userId) {
                            chat.user2
                        } else {
                            chat.user1
                        }
                        CommonRow(name = chatUser.name.toString()) {
                            chat.chatId?.let { chatId ->
                                navController.navigate(
                                    Chat(chatId = chatId)
                                )
                            }
                        }
                    }
                }
            }



            AddUserDialog(
                showDialog = showDialog.value,
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    vm.onAddChat(it)
                    showDialog.value = false

                }
            )
        }
    }
}

@Composable
fun AddUserDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    confirmButton: (String) -> Unit,
) {

    val AddChatNumber = remember {
        mutableStateOf("")
    }

    if (showDialog) {

        AlertDialog(
            onDismissRequest = {
                onDismissRequest.invoke()
                AddChatNumber.value = ""
            },
            confirmButton = {
                ConfirmButton(text = "Add") {
                    confirmButton(AddChatNumber.value)
                }
            },
            title = {
                Text(text = "New Chat")
            },
            text = {
                OutlinedTextField(
                    value = AddChatNumber.value,
                    onValueChange = {
                        AddChatNumber.value = it
                    },
                    label = { Text(text = "Number") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        )
    }
}

@Composable
fun CommonRow(
    modifier: Modifier = Modifier,
    name: String,
    onClick: () -> Unit,
) {
    val userName = name
    val firstChar = userName.firstOrNull()?.uppercaseChar() ?: ' '
    val colorForChar = getColorForChar(firstChar).color

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(75.dp)
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colorForChar),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$firstChar", color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Text(
                text = name ?: "----",
                modifier = modifier.padding(start = 4.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}