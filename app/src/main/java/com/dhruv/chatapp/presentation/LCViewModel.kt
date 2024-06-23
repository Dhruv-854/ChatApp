package com.dhruv.chatapp.presentation

import android.icu.util.Calendar
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.dhruv.chatapp.CHATS
import com.dhruv.chatapp.Event
import com.dhruv.chatapp.MESSAGE
import com.dhruv.chatapp.USER_NODE
import com.dhruv.chatapp.model.ChatData
import com.dhruv.chatapp.model.ChatUser
import com.dhruv.chatapp.model.Message
import com.dhruv.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
) : ViewModel() {


    var inProcess = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)


    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(emptyList())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProcessMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    private val _isLogingIn = mutableStateOf(false)


    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signOut() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateChatMessages()
        currentChatMessageListener = null
        eventMutableState.value = Event("Signed Out")
    }


    fun singUp(
        name: String,
        number: String,
        email: String,
        password: String,
        onComplicate: () -> Unit,
    ) {
        inProcess.value = true
        if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        }
        inProcess.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password)

                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            signIn.value = true
                            onComplicate()
                            createOrUpdateProfile(name, number)
                        } else {
                            handleException(it.exception, "SignUp failed")
                        }
                        inProcess.value = false
                    }
            } else {
                handleException(customMessage = "Number already exists")
            }
        }
    }

    fun login(
        email: String, password: String,
        onComplicate: () -> Unit,
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        }

        inProcess.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signIn.value = true
                    auth.currentUser?.uid?.let { userId ->
                        getUserData(userId)
                    }
                    onComplicate()
                } else {
                    handleException(task.exception, "Login failed")
                }
                inProcess.value = false
            }.addOnFailureListener { exception ->
                handleException(exception, "Login failed")
                inProcess.value = false
            }
    }


    suspend fun updateProfile(
        name: String? = null,
        number: String? = null,
        imageurl: String? = null,
    ) {
        var uid = auth.currentUser?.uid
        uid?.let {
            val docRef = db.collection(USER_NODE).document(uid)
            val existingData = docRef.get().await() // Wait for data
            val userData = UserData(
                userId = uid,
                name = name ?: existingData.get("name") as? String,
                number = number ?: existingData.get("number") as? String,
                imageurl = imageurl ?: existingData.get("imageurl") as? String
            )
            docRef.set(userData) // Set or update data
            getUserData(uid) // Get updated data
            inProcess.value = false // Update UI to show success
        }
    }


    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageurl: String? = null,
    ) {
        inProcess.value = true
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageurl = imageurl ?: userData.value?.imageurl
        )
        uid?.let {
            inProcess.value = true
            db.collection(USER_NODE).document(uid).get()
                .addOnSuccessListener {
                    if (it.exists()) {

                    } else {
                        db.collection(USER_NODE).document(uid).set(userData)
                        inProcess.value = false
                        getUserData(uid)

                    }
                }
                .addOnFailureListener {
                    handleException(it, "Failed to create profile")
                }
        }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Failed to get user data")
            }
            if (value != null) {
                var user = value.toObject<UserData?>()
                userData.value = user
                inProcess.value = false
                populateChats()
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String? = null) {
        Log.e("ChatApp", "ChatAppException", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) {
            errorMsg
        } else {
            customMessage
        }
        eventMutableState.value = Event(message)
        inProcess.value = false
    }

    fun onAddChat(number: String) {
        // Validate the number input
        if (number.isEmpty() || !number.isDigitsOnly()) {
            handleException(customMessage = "Please enter a valid number")
            return
        }

        // Ensure user data is loaded
        val currentUser = userData.value
        if (currentUser == null) {
            handleException(customMessage = "Current user data not available")
            return
        }

        // Check if chat already exists
        db.collection(CHATS).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.number", number),
                    Filter.equalTo("user2.number", currentUser.number)
                ),
                Filter.and(
                    Filter.equalTo("user1.number", currentUser.number),
                    Filter.equalTo("user2.number", number)
                )
            )
        ).get().addOnSuccessListener { chatQuerySnapshot ->
            if (chatQuerySnapshot.isEmpty) {
                // Check if the user with the given number exists
                db.collection(USER_NODE).whereEqualTo("number", number).get()
                    .addOnSuccessListener { userQuerySnapshot ->
                        if (userQuerySnapshot.isEmpty) {
                            handleException(customMessage = "User not found")
                        } else {
                            // Create a new chat
                            val chatPartner = userQuerySnapshot.toObjects(UserData::class.java)[0]
                            val id = db.collection(CHATS).document().id
                            val chat = ChatData(
                                chatId = id,
                                user1 = ChatUser(
                                    userId = currentUser.userId,
                                    number = currentUser.number,
                                    name = currentUser.name,
                                ),
                                user2 = ChatUser(
                                    userId = chatPartner.userId,
                                    number = chatPartner.number,
                                    name = chatPartner.name,
                                )
                            )
                            db.collection(CHATS).document(id).set(chat)
                        }
                    }
                    .addOnFailureListener { e ->
                        handleException(e, "Failed to get user data")
                    }
            } else {
                handleException(customMessage = "Chat already exists")
            }
        }.addOnFailureListener { e ->
            handleException(e, "Failed to check existing chats")
        }
    }

    fun populateChats() {
        inProcessChat.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Failed to get chats")
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChat.value = false
            }
        }
    }

    fun populateChatMessages(chatId: String) {
        inProcessMessage.value = true
        currentChatMessageListener = db.collection(CHATS)
            .document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Failed to get chat messages")
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy {
                        it.timestamp
                    }
                    inProcessMessage.value = false
                }
            }
    }

    fun depopulateChatMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener = null

    }
    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(
            userData.value?.userId, message, time
        )
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)

    }
}