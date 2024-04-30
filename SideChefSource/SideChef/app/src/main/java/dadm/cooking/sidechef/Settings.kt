package dadm.cooking.sidechef

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

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
    private lateinit var saveChangesButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var frameProgress: FrameLayout
    private var user_id: Int = 0
    private lateinit var username: String
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var token: String
    private lateinit var old_name: String
    private lateinit var old_username: String
    private lateinit var old_email: String
    private lateinit var new_name: String
    private lateinit var new_username: String
    private lateinit var new_email: String

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

        frameProgress = findViewById(R.id.frameProgressSettings)
        progressBar = findViewById(R.id.progressBarSettings)
        saveChangesButton = findViewById(R.id.saveChanges)
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

        old_name = editTextCurrentName.text.toString()
        old_username = editTextCurrentUserName.text.toString()
        old_email = editTextCurrentEmail.text.toString()
        new_name = old_name
        new_username = old_username
        new_email = old_email



        imageViewEditName.setOnClickListener {
            enableEditText(editTextCurrentName)
        }
        backButton.setOnClickListener {
            changeToProfile()
        }

        saveChangesButton.setOnClickListener {
            saveChanges()
        }

        editTextCurrentName.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val isNameValid: Boolean = validateName(editTextCurrentName.text.toString())
                if (isNameValid) {
                    val currentText = editTextCurrentName.text.toString()
                    editTextCurrentName.setText(currentText)
                    new_name = currentText
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
                    new_username = currentText
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
                    new_email = currentText
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

    private fun saveChanges() {
        if (new_name != old_name || new_username != old_username || new_email != old_email && editTextCurrentPassword.text.toString() == "*****") {
            frameProgress.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            saveChangesButton.isEnabled = false
            editTextCurrentName.isEnabled = false;
            imageViewEditName.isEnabled = false;
            editTextCurrentUserName.isEnabled = false;
            imageViewEditUserName.isEnabled = false;
            editTextCurrentPassword.isEnabled = false;
            imageViewEditPassword.isEnabled = false;
            editTextCurrentEmail.isEnabled = false;
            imageViewEditEmail.isEnabled = false;
            changeCredentials(name = new_name, username = new_username, email = new_email)
        } else if (new_name == old_name && new_username == old_username && new_email == old_email && editTextCurrentPassword.text.toString() != "*****") {
            val isPasswordValid: Boolean = validatePassword(editTextCurrentPassword.text.toString())
            if (isPasswordValid) {
                frameProgress.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                saveChangesButton.isEnabled = false
                editTextCurrentName.isEnabled = false;
                imageViewEditName.isEnabled = false;
                editTextCurrentUserName.isEnabled = false;
                imageViewEditUserName.isEnabled = false;
                editTextCurrentPassword.isEnabled = false;
                imageViewEditPassword.isEnabled = false;
                editTextCurrentEmail.isEnabled = false;
                imageViewEditEmail.isEnabled = false;
                var new_password = editTextCurrentPassword.text.toString()
                changePassword(password = new_password)
            }
        } else if (new_name != old_name || new_username != old_username || new_email != old_email && editTextCurrentPassword.text.toString() != "*****") {
            val isPasswordValid: Boolean = validatePassword(editTextCurrentPassword.text.toString())
            if (isPasswordValid) {
                frameProgress.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                saveChangesButton.isEnabled = false
                editTextCurrentName.isEnabled = false;
                imageViewEditName.isEnabled = false;
                editTextCurrentUserName.isEnabled = false;
                imageViewEditUserName.isEnabled = false;
                editTextCurrentPassword.isEnabled = false;
                imageViewEditPassword.isEnabled = false;
                editTextCurrentEmail.isEnabled = false;
                imageViewEditEmail.isEnabled = false;
                var new_password = editTextCurrentPassword.text.toString()
                changePassword(password = new_password)
                changeCredentials(name = new_name, username = new_username, email = new_email)
            }
        }
    }

    private fun changePassword(password: String) {
        val url = getString(R.string.changePasswordURL) + "/$user_id"

        val jsonBody = JSONObject()
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.PUT, url,
            Response.Listener { response ->
                try {
                    Log.d("APP_REST",  handleUpdateUserResponse(response).toString())
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    saveChangesButton.isEnabled = true
                    editTextCurrentName.isEnabled = true;
                    imageViewEditName.isEnabled = true;
                    editTextCurrentUserName.isEnabled = true;
                    imageViewEditUserName.isEnabled = true;
                    editTextCurrentPassword.isEnabled = true;
                    imageViewEditPassword.isEnabled = true;
                    editTextCurrentEmail.isEnabled = true;
                    imageViewEditEmail.isEnabled = true;

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    saveChangesButton.isEnabled = true
                    editTextCurrentName.isEnabled = true;
                    imageViewEditName.isEnabled = true;
                    editTextCurrentUserName.isEnabled = true;
                    imageViewEditUserName.isEnabled = true;
                    editTextCurrentPassword.isEnabled = true;
                    imageViewEditPassword.isEnabled = true;
                    editTextCurrentEmail.isEnabled = true;
                    imageViewEditEmail.isEnabled = true;

                }
            },
            Response.ErrorListener { error ->
                val errorMessage: String = when(error) {
                    is AuthFailureError -> "Authentication Error! Check Credentials"
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
                    else -> "Unknown error"
                }
                showError(errorMessage)
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                frameProgress.visibility = View.GONE
                progressBar.visibility = View.GONE
                saveChangesButton.isEnabled = true
                editTextCurrentName.isEnabled = true;
                imageViewEditName.isEnabled = true;
                editTextCurrentUserName.isEnabled = true;
                imageViewEditUserName.isEnabled = true;
                editTextCurrentPassword.isEnabled = true;
                imageViewEditPassword.isEnabled = true;
                editTextCurrentEmail.isEnabled = true;
                imageViewEditEmail.isEnabled = true;

            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Authorization"] = "Bearer $token"
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun changeCredentials(name: String, username: String, email: String) {
        val url = getString(R.string.changeUserURL) + "/$user_id"

        val jsonBody = JSONObject()
        jsonBody.put("name", name.trim())
        jsonBody.put("username", username.trim())
        jsonBody.put("email", email.trim())
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.PUT, url,
            Response.Listener { response ->
                try {
                    handleUpdateUserResponse(response)
                    Log.d("APP_REST",  handleUpdateUserResponse(response).toString())
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    saveChangesButton.isEnabled = true
                    editTextCurrentName.isEnabled = true;
                    imageViewEditName.isEnabled = true;
                    editTextCurrentUserName.isEnabled = true;
                    imageViewEditUserName.isEnabled = true;
                    editTextCurrentPassword.isEnabled = true;
                    imageViewEditPassword.isEnabled = true;
                    editTextCurrentEmail.isEnabled = true;
                    imageViewEditEmail.isEnabled = true;

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    saveChangesButton.isEnabled = true
                    editTextCurrentName.isEnabled = true;
                    imageViewEditName.isEnabled = true;
                    editTextCurrentUserName.isEnabled = true;
                    imageViewEditUserName.isEnabled = true;
                    editTextCurrentPassword.isEnabled = true;
                    imageViewEditPassword.isEnabled = true;
                    editTextCurrentEmail.isEnabled = true;
                    imageViewEditEmail.isEnabled = true;

                }
            },
            Response.ErrorListener { error ->
                val errorMessage: String = when(error) {
                    is AuthFailureError -> "Authentication Error! Check Credentials"
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
                    else -> "Unknown error"
                }
                showError(errorMessage)
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                frameProgress.visibility = View.GONE
                progressBar.visibility = View.GONE
                saveChangesButton.isEnabled = true
                editTextCurrentName.isEnabled = true;
                imageViewEditName.isEnabled = true;
                editTextCurrentUserName.isEnabled = true;
                imageViewEditUserName.isEnabled = true;
                editTextCurrentPassword.isEnabled = true;
                imageViewEditPassword.isEnabled = true;
                editTextCurrentEmail.isEnabled = true;
                imageViewEditEmail.isEnabled = true;

            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Authorization"] = "Bearer $token"
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun handleUpdateUserResponse(response: String) {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: ChangeUserData = gson.fromJson(response, ChangeUserData::class.java)
            val fragmentManager = supportFragmentManager
            val profileFragment = fragmentManager.findFragmentById(R.id.ContentframeLayout) as? Profile
            profileFragment?.updateLabels(nameUpdated = res.name, usernameUpdated = res.username, emailUpdated = res.email)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST", e.toString())
        }
    }

    private fun showError(message: String) {
        labelValidation.setTextColor(Color.RED)
        labelValidation.text = message
        labelValidation.visibility = View.VISIBLE
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
