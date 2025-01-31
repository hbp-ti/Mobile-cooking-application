package dadm.cooking.sidechef.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dadm.cooking.sidechef.R
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class CreateAccount : AppCompatActivity() {

    private lateinit var signInLink: TextView
    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var signUpButton: Button
    private lateinit var labelValidation: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var frameProgress: FrameLayout
    private val DEFAULT_BACKOFF_MULT: Float = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
    }

    private fun setupView() {
        progressBar = findViewById(R.id.progressBarRegister)
        frameProgress = findViewById(R.id.frameProgressRegister)
        inputName = findViewById(R.id.InputName)
        inputEmail = findViewById(R.id.InputEmail)
        inputUsername = findViewById(R.id.InputUsername)
        inputPassword = findViewById(R.id.InputPassword)
        inputConfirmPassword = findViewById(R.id.InputConfirmPassword)
        labelValidation = findViewById(R.id.labelValidationCreateAcc)
        signUpButton = findViewById(R.id.buttonSignUp)
        signUpButton.setOnClickListener {
            val isCredentialsVal = validateCredentials(inputName.text.toString(), inputEmail.text.toString(), inputUsername.text.toString(), inputPassword.text.toString(), inputConfirmPassword.text.toString())
            if (isCredentialsVal) {
                signUpButton.isEnabled = false
                inputName.isEnabled = false
                inputEmail.isEnabled = false
                inputUsername.isEnabled = false
                inputPassword.isEnabled = false
                inputConfirmPassword.isEnabled = false
                frameProgress.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                signInLink.isEnabled = false
                createAccount(name = inputName.text.toString(), email = inputEmail.text.toString(), username = inputUsername.text.toString(), password = inputPassword.text.toString())
            }
        }
        signInLink = findViewById(R.id.loginLabel)
        signInLink.setOnClickListener {
            changeToSignIn()
        }
    }

    private fun validateCredentials(name: String, email: String, username: String, password: String, confirmPassword: String): Boolean {
        val namePattern = Regex("^[a-zA-ZÀ-ÖØ-öø-ÿ ]{2,30}\$")
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        val usernamePattern = Regex("^[a-zA-Z0-9_!@#\$%^&*().\\-+=~]{5,15}$")
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()-_=+\\\\|<>?{}\\[\\]~])(?!.*\\s).{8,}\$")

        if (name.isBlank() or email.isBlank() or username.isBlank() or password.isBlank() or confirmPassword.isBlank()) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please fill the input fields"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!name.matches(namePattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "The name should not include numbers and must be between 2 and 30 letters"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!email.matches(emailPattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Please enter a valid email address in the format name@domain.com"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!username.matches(usernamePattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Username should have between 5 to 15 characters"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if (!password.matches(passwordPattern)) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "Password must have at least 8 characters, one uppercase letter, one digit, and one special character"
            labelValidation.visibility = View.VISIBLE
            return false
        } else if(password != confirmPassword) {
            labelValidation.setTextColor(Color.RED)
            labelValidation.text = "The passwords dont match"
            labelValidation.visibility = View.VISIBLE
            return false
        } else {
            labelValidation.visibility = View.INVISIBLE
            return true
        }
    }

    private fun createAccount(name: String, email: String, username: String, password: String) {
        val url = getString(R.string.registerURL)

        val jsonBody = JSONObject()
        jsonBody.put("name", name.trim())
        jsonBody.put("email", email.trim())
        jsonBody.put("username", username.trim())
        jsonBody.put("password", password.trim())
        val requestBody = jsonBody.toString()
        
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    Log.d("APP_REST",  response)
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    changeToSignIn()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace())
                    signUpButton.isEnabled = true
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    signUpButton.isEnabled = true
                    inputName.isEnabled = true
                    inputEmail.isEnabled = true
                    inputUsername.isEnabled = true
                    inputPassword.isEnabled = true
                    inputConfirmPassword.isEnabled = true
                    signInLink.isEnabled = true
                }
            },
            Response.ErrorListener { error ->
                val errorMessage: String = when(error) {
                    is AuthFailureError -> "Authentication Error!"
                    is NetworkError -> "Network error!"
                    is TimeoutError -> "Time is over!"
                    is ServerError -> {
                        try {
                            val errorResponse = JSONObject(String(error.networkResponse.data))
                            if (errorResponse.has("error")) {
                                errorResponse.getString("error")
                            } else {
                                "Unknown server error"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "Unknown server error"
                        }
                    }
                    else -> "Unknown Error"
                }
                showError(errorMessage)
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                signUpButton.isEnabled = true
                frameProgress.visibility = View.GONE
                progressBar.visibility = View.GONE
                signUpButton.isEnabled = true
                inputName.isEnabled = true
                inputEmail.isEnabled = true
                inputUsername.isEnabled = true
                inputPassword.isEnabled = true
                inputConfirmPassword.isEnabled = true
                signInLink.isEnabled = true
            }) {

            override fun getRetryPolicy(): RetryPolicy {
                return DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DEFAULT_BACKOFF_MULT
                )
            }
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun showError(message: String) {
        labelValidation.setTextColor(Color.RED)
        labelValidation.text = message
        labelValidation.visibility = View.VISIBLE
    }

    private fun changeToSignIn() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}