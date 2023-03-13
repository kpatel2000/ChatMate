package com.example.whatsappclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChatAdapter(val context:Context, private val message:ArrayList<Message>, private val receiverId:String?=null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENDER_VIEW_TYPE:Int=1
    private val RECEIVER_VIEW_TYPE:Int=2
    inner class ReceiverVewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiverMessage: TextView = itemView.findViewById<TextView>(R.id.receiverMessage)
        val receiverTime: TextView = itemView.findViewById<TextView>(R.id.receiverTime)
    }
    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val senderMessage: TextView = itemView.findViewById<TextView>(R.id.senderMessage)
        val senderTime: TextView = itemView.findViewById<TextView>(R.id.senderTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDER_VIEW_TYPE){
            val view = LayoutInflater.from(context).inflate(R.layout.sender,parent,false)
            SenderViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.receiver,parent,false)
            ReceiverVewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageModel = message[position]
        if (holder.javaClass == SenderViewHolder::class.java){
            (holder as SenderViewHolder).senderMessage.text = messageModel.message
            holder.senderTime.text = messageModel.timeStamp.toString()

        }else{
            (holder as ReceiverVewHolder).receiverMessage.text = messageModel.message
        }
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context).setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    val database = FirebaseDatabase.getInstance()
                    val senderRoom = FirebaseAuth.getInstance().uid + receiverId
                    database.reference.child("chats").child(senderRoom)
                        .child(messageModel.uid!!)
                        .setValue(null)

                }).setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }).show()
            return@setOnLongClickListener false
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (message[position].uid.equals(FirebaseAuth.getInstance().uid)){
            SENDER_VIEW_TYPE
        }else{
            RECEIVER_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return message.size
    }

}