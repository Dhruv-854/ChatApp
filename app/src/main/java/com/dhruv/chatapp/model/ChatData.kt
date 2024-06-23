package com.dhruv.chatapp.model

data class ChatData(
    val chatId : String?="",
    val user1 : ChatUser = ChatUser(),
    val user2 : ChatUser = ChatUser(),
)
data class ChatUser(
    val userId : String?="",
    val name : String?="",
    val number: String?="",
)