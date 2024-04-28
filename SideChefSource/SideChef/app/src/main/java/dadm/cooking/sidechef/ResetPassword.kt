package dadm.cooking.sidechef

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResetPassword : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var labelValidation: TextView
    private lateinit var emailInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
    }

    private fun setupView() {
        labelValidation = findViewById(R.id.labelValidationReset)
        emailInput = findViewById(R.id.emailInput)
        sendButton = findViewById(R.id.buttonSend)
        sendButton.setOnClickListener {
            val isEmailValid: Boolean = validateEmail(emailInput.text.toString())
            if (isEmailValid) {
                sendResetEmail()
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        if (email.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input field"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!email.matches(emailPattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please enter a valid email address in the format name@domain.com"
            labelValidation.visibility = View.VISIBLE
            return false
        } else {
            labelValidation.visibility = View.INVISIBLE
            return true
        }
    }

    private fun sendResetEmail() {
        //TODO("IMPLEMENT EMAIL RESET")
        
        finish()
    }
}