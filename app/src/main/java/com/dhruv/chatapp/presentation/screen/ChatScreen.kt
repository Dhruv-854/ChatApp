package com.dhruv.chatapp.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dhruv.chatapp.model.Message
import com.dhruv.chatapp.presentation.LCViewModel
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    vm: LCViewModel,
    chatId: String,
) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        vm.onSendReply(
            chatId = chatId,
            message = reply
        )
        reply = ""
    }
    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser = if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1
    val chatMessage = vm.chatMessages

    LaunchedEffect(key1 = Unit) {
        vm.populateChatMessages(chatId)
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                name = chatUser.name ?: "",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MessageBox(
                modifier = Modifier.weight(1f),
                chatMessage = chatMessage.value,
                currentUserId = myUser?.userId ?: ""
            )
            ReplyBox(
                reply = reply,
                onReplyChange = { reply = it },
                onSendReply = onSendReply
            )
        }
    }
}

@Composable
fun ReplyBox(
    modifier: Modifier = Modifier,
    reply: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Divider(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = reply,
                onValueChange = onReplyChange,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onSendReply() }) {
                Text(text = "Send")
            }
        }
    }
}

@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    chatMessage: List<Message>,
    currentUserId: String,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(chatMessage) { msg ->
            val horizontalAlignment = if (msg.sendBy == currentUserId) {
                Alignment.End
            } else {
                Alignment.Start
            }
            val backgroundColor = if (msg.sendBy == currentUserId) {
                Color(0xFF68C400)
            } else {
                Color(0xFFC0C0C0)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = horizontalAlignment
                ) {
                    Text(
                        text = msg.message ?: "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .padding(12.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    name: String,
) {
    TopAppBar(
        title = {
            val firstChar = name.firstOrNull()?.uppercaseChar() ?: ' '
            val colorForChar = getColorForChar(firstChar).color

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        text = name,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}