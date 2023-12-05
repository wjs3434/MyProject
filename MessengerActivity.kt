package com.example.firebasetest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MessengerActivity : AppCompatActivity() {
    private val database = Firebase.database
    private val messagesRef = database.getReference("messages")
    private var adapter: MessengerAdapter? = null
    private val recyclerViewMessages by lazy { findViewById<RecyclerView>(R.id.recyclerViewMessenger) }
    private val editMessageContent by lazy { findViewById<EditText>(R.id.editMessageContent) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        adapter = MessengerAdapter(this, emptyList())

        recyclerViewMessages.adapter = adapter

        findViewById<Button>(R.id.buttonSendMessage)?.setOnClickListener {
            sendMessage()
            hideKeyboard()
        }
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messages = mutableListOf<Messages>()
                for (child in dataSnapshot.children) {
                    messages.add(Messages(child.key ?: "", child.value as Map<*, *>))
                }
                adapter?.updateList(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    private fun sendMessage() {
        val messageContent = editMessageContent.text.toString()
        val sellerEmail = intent?.getStringExtra("sellerEmail")
        val currentUserEmail = intent?.getStringExtra("currentUserEmail")

        if (messageContent.isNotEmpty()) {
            val buyerId = currentUserEmail ?: ""
            val ownerID = sellerEmail ?: ""

            // Create a unique key for each message
            val messageKey = messagesRef.push().key ?: ""

            // Create a map with message details
            val messageMap = hashMapOf(
                "buyerId" to buyerId,
                "ownerID" to ownerID,
                "content" to messageContent
            )

            // Save the message to the database
            messagesRef.child(messageKey).setValue(messageMap)

            // Clear the input field after sending the message
            editMessageContent.text.clear()
        } else {
            Snackbar.make(editMessageContent, "Input message content!", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editMessageContent.windowToken, 0)
    }

}

