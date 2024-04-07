package dadm.cooking.sidechef

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var signUpLink: TextView
    private lateinit var forgotPass: TextView
    private lateinit var buttonLogIn: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var labelValidation: TextView
    private lateinit var keeploginBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val loginkept: Boolean = keepLogin()

        if (loginkept) {
            val intent = Intent(this, MainPageNav::class.java)
            startActivity(intent)
        } else {
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

            usernameInput = findViewById(R.id.usernameInput)
            passwordInput = findViewById(R.id.passwordInput)
            labelValidation = findViewById(R.id.labelValidationLogin)
            keeploginBox = findViewById(R.id.checkBoxKeepLogIn)
            buttonLogIn.setOnClickListener {
                val isCredentialsVal: Boolean = validateCredentials(usernameInput.text.toString(), passwordInput.text.toString())
                if (isCredentialsVal) {
                    if (keeploginBox.isChecked) {
                        saveLogin(true)
                        changeToMainPage()
                    } else {
                        changeToMainPage()
                    }
                }
            }
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        val usernamePattern = Regex("^[a-zA-Z0-9]{5,15}$")
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()-_=+\\\\|<>?{}\\[\\]~])(?!.*\\s).{8,}\$")
        if (username.isBlank() || password.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input fields"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!username.matches(usernamePattern)){
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Username should have between 5 to 15 characters"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!password.matches(passwordPattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Password must have at least 8 characters, one uppercase letter, one digit, and one special character"
            labelValidation.visibility = View.VISIBLE
            return false
        } else {
            labelValidation.visibility = View.INVISIBLE
            return true
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
    }

    private fun changeToMainPage() {
        val intent = Intent(this, MainPageNav::class.java)
        startActivity(intent)
        finish()
    }

    private fun keepLogin(): Boolean {
        val sharedPref = getSharedPreferences(R.string.userLogInFile.toString(), Context.MODE_PRIVATE)
        return sharedPref.getBoolean(R.string.keepLoginKey.toString(), false)
    }

    private fun saveLogin(saveLogIn: Boolean) {
        val sharedPref = getSharedPreferences(R.string.userLogInFile.toString(), Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(R.string.keepLoginKey.toString(), saveLogIn).apply()
    }

}