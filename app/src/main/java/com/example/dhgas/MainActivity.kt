package com.example.dhgas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dhgas.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mAuth = FirebaseAuth.getInstance()

        binding.editEmail.setText("dave@dhgasltd.co.uk")
        binding.editPassword.setText("Jaffacake1")

        binding.buttonLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            if ( email== "" || password == "") {
                Toast.makeText(this, "Please ensure both email and password are not blank", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){ task ->
                        if (task.isSuccessful){
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            Toast.makeText(this, "Login failed ${task.exception}", Toast.LENGTH_LONG).show()
                            return@addOnCompleteListener
                        }

                    }
        }

        binding.buttonRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}