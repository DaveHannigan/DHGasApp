package com.example.dhgas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dhgas.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.registerToolbar
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        val mAuth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            val email = binding.editEmail.text.toString()
            if (email == "" || email == null){
                return@setOnClickListener
            }
            val password = binding.editPassword.text.toString()
            if (password =="" || password == null || password.length < 4){
                return@setOnClickListener
            }
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener {task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "User created", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "User creation failed ${task.exception}", Toast.LENGTH_LONG).show()
                    }

                })
        }
        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }
}