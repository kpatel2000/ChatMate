package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapter.ChatAdapter
import com.example.whatsappclone.databinding.ActivityChatDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class ChatDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityChatDetailBinding
    lateinit var database:FirebaseDatabase
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val senderId = auth.uid
        val receiverId = intent.getStringExtra("userId")
        val profile = intent.getStringExtra("profileId")
        val userName = intent.getStringExtra("userName")

        binding.userName.text = userName
        Picasso.get().load(profile).placeholder(R.drawable.avatar).into(binding.profileImage)

        binding.backArrow.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val messageModel = ArrayList<com.example.whatsappclone.models.Message>()
        val chatAdapter = ChatAdapter(this,messageModel,receiverId)

        binding.chatRecyclerViewRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerViewRecyclerView.adapter = chatAdapter

        val senderRoom = senderId + receiverId
        val receiverRoom = receiverId + senderId
        binding.send.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isEmpty())
                return@setOnClickListener
            val model = com.example.whatsappclone.models.Message(senderId,message)
            model.timeStamp = Date().time
            binding.etMessage.text = null
            database.reference.child("chats")
                .child(senderRoom)
                .push()
                .setValue(model)
                .addOnSuccessListener {
                    database.reference.child("chats")
                        .child(receiverRoom)
                        .push()
                        .setValue(model)
                        .addOnSuccessListener {

                        }
                }
        }

        database.reference.child("chats")
            .child(senderRoom)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageModel.clear()
                    for (snapshot1 in snapshot.children){
                        val model = snapshot1.getValue(com.example.whatsappclone.models.Message::class.java)
                        model?.messageId = snapshot1.key
                        messageModel.add(model!!)
                    }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })


    }
}