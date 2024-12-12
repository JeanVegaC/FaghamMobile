package com.example.faghamsac.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.faghamsac.MainActivity
import com.example.faghamsac.R
import com.example.faghamsac.configuration.ApiClient
import com.example.faghamsac.modules.invoice.services.TokenService
import com.example.faghamsac.modules.login.model.Aplicacion
import com.example.faghamsac.modules.login.model.TokenRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var tokenService: TokenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tokenService = ApiClient.createService(TokenService::class.java)

        val rucEditText = findViewById<EditText>(R.id.rucEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val ruc = rucEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (ruc.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val request = TokenRequest(
                    mail = email,
                    ruc = ruc,
                    clave = password,
                    aplicacion = Aplicacion(codigo = "1")
                )

                loginUser(request)
            } else {
                Toast.makeText(this, "Por favor, ingresa todos los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(request: TokenRequest) {
        lifecycleScope.launch {
            try {
                val response = tokenService.getToken(request)
                Log.d("token", "$response")
                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    val token = tokenResponse?.c2uToken ?: ""
                    if (token != null && response.isSuccessful) {
                        val sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("token", "Bearer " + token)
                        editor.apply()

                        val userMailHeader: TextView = findViewById(R.id.userMail)
                        userMailHeader.text = request.mail

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error de autenticación: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error al conectarse al servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("error", "${e.message}")
            }
        }
    }
}

