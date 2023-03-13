package com.example.whatsappclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.whatsappclone.models.User
import com.example.whatsappclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    lateinit var binding:ActivitySignUpBinding
    lateinit var auth:FirebaseAuth
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.btnSignUp.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(binding.etEmail.text.toString(),binding.etPassword.text.toString())
                .addOnCompleteListener {
                    binding.progressbar.visibility = View.GONE

                    if (it.isSuccessful){
                        val user = User(binding.etUsername.text.toString(),binding.etEmail.text.toString(),
                            binding.etPassword.text.toString())

                        val id:String = it.result!!.user!!.uid
                        database.reference.child("Users").child(id).setValue(user)
                        Toast.makeText(this,"User Created Successfully",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.tvAlreadyAccount.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }


    }
}