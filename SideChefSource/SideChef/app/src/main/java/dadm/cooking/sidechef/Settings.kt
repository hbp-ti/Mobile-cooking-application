package dadm.cooking.sidechef

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class Settings : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var editTextCurrentName: EditText
    private lateinit var imageViewEditName: ImageView
    private lateinit var editTextCurrentUserName: EditText
    private lateinit var imageViewEditUserName: ImageView
    private lateinit var editTextCurrentPassword: EditText
    private lateinit var imageViewEditPassword: ImageView
    private lateinit var editTextCurrentEmail: EditText
    private lateinit var imageViewEditEmail: ImageView
    private lateinit var labelValidation: TextView
    private var user_id: Int = 0
    private lateinit var username: String
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
    }

    private fun setupView() {
        user_id = intent.getIntExtra("user_id", 0)
        username = intent.getStringExtra("username").toString()
        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()
        token = intent.getStringExtra("token").toString()

        backButton = findViewById(R.id.goBackArrowSettings)
        editTextCurrentName = findViewById(R.id.textViewCurrentName)
        imageViewEditName = findViewById(R.id.imageViewEditName)
        editTextCurrentUserName = findViewById(R.id.textViewCurrentUserName)
        imageViewEditUserName = findViewById(R.id.imageViewEditUserName)
        editTextCurrentPassword = findViewById(R.id.textViewCurrentPassword)
        imageViewEditPassword = findViewById(R.id.imageViewEditPassword)
        editTextCurrentEmail = findViewById(R.id.textViewCurrentEmail)
        imageViewEditEmail = findViewById(R.id.imageViewEditEmail)
        labelValidation = findViewById(R.id.labelValidationSettings)

        editTextCurrentName.setText(name)
        editTextCurrentUserName.setText(username)
        editTextCurrentEmail.setText(email)
        editTextCurrentPassword.setText("*****")

        imageViewEditName.setOnClickListener {
            enableEditText(editTextCurrentName)
        }
        backButton.setOnClickListener {
            changeToProfile()
        }

        editTextCurrentName.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val isNameValid: Boolean = validateName(editTextCurrentName.text.toString())
                if (isNameValid) {
                    val currentText = editTextCurrentName.text.toString()
                    editTextCurrentName.setText(currentText)
                    editTextCurrentName.isEnabled = false
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editTextCurrentName.windowToken, 0)
                }
                true
            } else {
                false
            }
        }

        imageViewEditUserName.setOnClickListener {
            enableEditText(editTextCurrentUserName)
        }
        editTextCurrentUserName.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val isUsernameValid: Boolean = validateUsername(editTextCurrentUserName.text.toString())
                if (isUsernameValid) {
                    val currentText = editTextCurrentUserName.text.toString()
                    editTextCurrentUserName.setText(currentText)
                    editTextCurrentUserName.isEnabled = false
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editTextCurrentUserName.windowToken, 0)
                }
                true
            } else {
                false
            }
        }

        imageViewEditEmail.setOnClickListener {
            enableEditText(editTextCurrentEmail)
        }
        editTextCurrentEmail.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val isEmailValid: Boolean = validateEmail(editTextCurrentEmail.text.toString())
                if (isEmailValid) {
                    val currentText = editTextCurrentEmail.text.toString()
                    editTextCurrentEmail.setText(currentText)
                    editTextCurrentEmail.isEnabled = false
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editTextCurrentEmail.windowToken, 0)
                }
                true
            } else {
                false
            }
        }


        imageViewEditPassword.setOnClickListener {
            enableEditText(editTextCurrentPassword)
        }
        editTextCurrentPassword.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val isPasswordValid: Boolean = validatePassword(editTextCurrentPassword.text.toString())
                if (isPasswordValid) {
                    val currentText = editTextCurrentPassword.text.toString()
                    editTextCurrentPassword.setText(currentText)
                    editTextCurrentPassword.isEnabled = false
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editTextCurrentPassword.windowToken, 0)
                }
                true
            } else {
                false
            }
        }
    }


    private fun validateName(name: String): Boolean {
        val namePattern = Regex("^[a-zA-Z ]{2,30}\$")
        if (name.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input fields"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!name.matches(namePattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "The name should not include numbers and must be between 2 and 30 letters"
            labelValidation.visibility = View.VISIBLE
            return false
        } else {
            labelValidation.visibility = View.INVISIBLE
            return true
        }
    }

    private fun validateEmail(email: String): Boolean {
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        if (email.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input fields"
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

    private fun validateUsername(username: String): Boolean {
        val usernamePattern = Regex("^[a-zA-Z0-9_!@#\$%^&*()-+=~]{5,15}$")
        if (username.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input fields"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!username.matches(usernamePattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Username should have between 5 to 15 characters"
            labelValidation.visibility = View.VISIBLE
            return false
        } else {
            labelValidation.visibility = View.INVISIBLE
            return true
        }
    }

    private fun validatePassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()-_=+\\\\|<>?{}\\[\\]~])(?!.*\\s).{8,}\$")
        if (password.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input fields"
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

    private fun changeToProfile() {
        finish()
    }

    private fun enableEditText(editText: EditText) {
        editText.isEnabled = true
        editText.requestFocus()
        editText.setSelection(editText.text.length)
        val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}
