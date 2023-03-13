package com.example.whatsappclone.models

data class Message(var uid:String?=null,
                   var message:String?=null,
                   var timeStamp:Long = 0,
                   var messageId: String?=null
                   )