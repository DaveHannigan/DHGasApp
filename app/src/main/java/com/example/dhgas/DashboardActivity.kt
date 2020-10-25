package com.example.dhgas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dhgas.databinding.ActivityDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        db.collection("Customer")
                .get()
            .addOnSuccessListener {task->
                        for (doclist in task){}

            }
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful){

                        Toast.makeText(this, "Task successful", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Task failed ${task.exception}", Toast.LENGTH_LONG).show()
                    }
                }
                


    }
}