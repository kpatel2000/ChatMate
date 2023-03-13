package com.example.whatsappclone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapter.ChatAdapter
import com.example.whatsappclone.databinding.ActivityGroupChatBinding
import com.example.whatsappclone.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class GroupChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityGroupChatBinding
    lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        database = FirebaseDatabase.getInstance()
        val senderId = FirebaseAuth.getInstance().uid
        binding.userName.text = "Group Chat"
        val messageList = ArrayList<Message>()
        val adapter = ChatAdapter(this, messageList)
        binding.chatRecyclerViewRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerViewRecyclerView.adapter = adapter

        database.reference.child("Group Chat")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val model = dataSnapshot.getValue(Message::class.java)
                        messageList.add(model!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })

        binding.send.setOnClickListener {
            val message = binding.etMessage.text.toString()
            val messageModel = Message(senderId, message, Date().time)
            binding.etMessage.text = null
            database.reference.child("Group Chat")
                .push()
                .setValue(messageModel)
                .addOnSuccessListener {

                }
        }
    }
}