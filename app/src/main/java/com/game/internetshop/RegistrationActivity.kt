package com.game.internetshop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val buttonSigningIn = findViewById<Button>(R.id.buttonSignIn)
        buttonSigningIn.setOnClickListener {
            val intent = Intent(this, SigningInActivity::class.java)
            startActivity(intent)
        }
    }
}