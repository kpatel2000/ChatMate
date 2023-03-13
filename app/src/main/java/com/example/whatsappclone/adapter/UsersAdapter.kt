package com.example.whatsappclone.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.ChatDetailActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UsersAdapter(val context:Context?, private val users:ArrayList<User>): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.profile_image)
        val userName = itemView.findViewById<TextView>(R.id.userNameList)
        val lastMessage = itemView.findViewById<TextView>(R.id.lastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.show_user,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val user = users[position]
        Picasso.get().load(user.profilePic).placeholder(R.drawable.avatar).into(holder.imageView)
        holder.userName.text = user.userName

        FirebaseDatabase.getInstance().reference.child("chats")
            .child(FirebaseAuth.getInstance().uid + user.userId)
            .orderByChild("timeStamp")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()){
                        for (dataSnapshot in snapshot.children){
                            holder.lastMessage.text = dataSnapshot.child("message").getValue(String::class.java)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatDetailActivity::class.java)
            intent.putExtra("userId",user.userId)
            intent.putExtra("profilePic",user.profilePic)
            intent.putExtra("userName",user.userName)
            context!!.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }
}