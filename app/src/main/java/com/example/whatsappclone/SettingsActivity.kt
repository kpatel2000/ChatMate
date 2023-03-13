package com.example.whatsappclone

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.whatsappclone.databinding.ActivitySettingsBinding
import com.example.whatsappclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : AppCompatActivity() {
    lateinit var binding:ActivitySettingsBinding
    lateinit var storage:FirebaseStorage
    lateinit var auth:FirebaseAuth
    lateinit var database:FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        database.reference.child("Users").child(auth.uid!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.getValue(User::class.java)
                    Picasso.get()
                        .load(users!!.profilePic)
                        .placeholder(R.drawable.avatar)
                        .into(binding.profileImage)

                    binding.etStatus.setText(users.status)
                    binding.etUsername.setText(users.userName)
                    Toast.makeText(this@SettingsActivity,"Profile Updated",Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })
        binding.backArrow.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnSave.setOnClickListener{
            val status = binding.etStatus.text.toString()
            val username = binding.etUsername.text.toString()

            val obj = HashMap<String, Any>()
            obj["userName"] = username
            obj["status"] = status

            database.reference.child("Users").child(auth.uid!!)
                .updateChildren(obj)
        }

        binding.plus.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data?.data != null){
                val sFile = data.data
                binding.profileImage.setImageURI(sFile)

                val reference = storage.reference.child("profile_pictures")
                    .child(auth.uid!!)
                reference.putFile(sFile!!).addOnSuccessListener {
                    reference.downloadUrl.addOnSuccessListener {
                        database.reference.child("Users")
                            .child(auth.uid!!)
                            .child("profilePic")
                            .setValue(it.toString())
                    }
                }
            }
        }
    }
}