package com.dhruv.chatapp.model

data class Message(
    var sendBy : String?="",
    var message : String?="",
    val timestamp: String?=""
)
