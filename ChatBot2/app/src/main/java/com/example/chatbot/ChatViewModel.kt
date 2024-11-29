package com.example.chatbot

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    val generativeMode: GenerativeModel = GenerativeModel(
        //modelName = "gemini-pro"
        modelName = "gemini-1.5-flash-001",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String) {
        // Log.i("In ChatViewModel",question)
        viewModelScope.launch {
            try {
                val chat = generativeMode.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )

                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing...", "model"))
                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(), "model"))
            } catch (e : Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error : " + e.message.toString(), "model"))
            }

            // Log.i("Response from Gemini",response.text.toString())
        }
    }
}