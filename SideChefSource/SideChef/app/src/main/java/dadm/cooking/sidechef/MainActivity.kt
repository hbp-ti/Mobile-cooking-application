package dadm.cooking.sidechef

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var signUpLink: TextView
    private lateinit var forgotPass: TextView
    private lateinit var buttonLogIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        signUpLink = findViewById(R.id.signUpLabel)
        signUpLink.setOnClickListener {
            changeToSignUp()
        }

        forgotPass = findViewById(R.id.forgotPassLabel)
        forgotPass.setOnClickListener {
            changeToForgotPassword()
        }

        buttonLogIn = findViewById(R.id.buttonLogIn)
        buttonLogIn.setOnClickListener {
            changeToMainPage()
        }
    }

    private fun changeToSignUp() {
        val intent = Intent(this, CreateAccount::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeToForgotPassword() {
        val intent = Intent(this, ResetPassword::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeToMainPage() {
        val intent = Intent(this, MainPageNav::class.java)
        startActivity(intent)
        finish()
    }

}