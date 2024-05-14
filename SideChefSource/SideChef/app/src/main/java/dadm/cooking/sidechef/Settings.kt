package dadm.cooking.sidechef

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

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
    private val DEFAULT_BACKOFF_MULT: Float = 1f
    private var fullUserChange: Boolean = false

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
        token = loadToken(context = this).toString()

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


        imageViewEditName.setOnClickListener {
            enableEditText(editTextCurrentName)
        }
        backButton.setOnClickListener {
            changeToProfile()
        }

        saveChangesButton.setOnClickListener {
            saveChanges()
        }

        imageViewEditUserName.setOnClickListener {
            enableEditText(editTextCurrentUserName)
        }

        imageViewEditEmail.setOnClickListener {
            enableEditText(editTextCurrentEmail)
        }

        imageViewEditPassword.setOnClickListener {
            enableEditText(editTextCurrentPassword)
        }
    }

    private fun saveChanges() {
        val newName = editTextCurrentName.text.toString()
        val newUsername = editTextCurrentUserName.text.toString()
        val newEmail = editTextCurrentEmail.text.toString()
        val newPassword = editTextCurrentPassword.text.toString()

        val isNameChanged = newName != old_name
        val isUsernameChanged = newUsername != old_username
        val isEmailChanged = newEmail != old_email
        val isPasswordChanged = newPassword != "*****"

        if ((isNameChanged or isUsernameChanged or isEmailChanged) and !isPasswordChanged) {
            if (validateName(newName) and validateUsername(newUsername) and validateEmail(newEmail)) {
                changeCredentials(name = newName, usernameParam = newUsername, email = newEmail)
            }
        } else if (!isNameChanged and !isUsernameChanged and !isEmailChanged and isPasswordChanged) {
            if (validatePassword(newPassword)) {
                changePassword(password = newPassword)
            }
        } else if ((isNameChanged or isUsernameChanged or isEmailChanged) and isPasswordChanged) {
            if (validateName(newName) and validateUsername(newUsername) and validateEmail(newEmail) and validatePassword(newPassword)) {
                fullUserChange = true
                changePassword(password = newPassword)
                changeCredentials(name = newName, usernameParam = newUsername, email = newEmail)
            }
        }
    }


    private fun changePassword(password: String) {
        val url = getString(R.string.changePasswordURL) + "/$user_id"
        disableInputs()
        val jsonBody = JSONObject()
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.PUT, url,
            Response.Listener { response ->
                updatePasswordDataLogIn(context = this, password = password)
                if (!fullUserChange) {
                    finish()
                }
                try {
                    Log.d("APP_REST",  "changed password"+response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    enableInputs()
                }
            },
            Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    loginBeforeChangePassword(username = username)
                } else {
                    val errorMessage: String = when(error) {
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
                    enableInputs()
                }
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

    private fun loginBeforeChangePassword(username: String) {
        val password = loadPassword(context = this)

        val url = getString(R.string.loginURL)

        val jsonBody = JSONObject()
        jsonBody.put("username", username.trim())
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    handleLoginChangePasswordResponse(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    enableInputs()
                    Toast.makeText(this, "Failed to renew token", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                enableInputs()
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                Toast.makeText(this, "Failed to renew token", Toast.LENGTH_SHORT).show()
            }) {

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

    private fun handleLoginChangePasswordResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            token = res.token
            SaveToken(context = this, tokenData = res.token)
            changePassword(password = editTextCurrentPassword.text.toString())
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }


    private fun changeCredentials(name: String, usernameParam: String, email: String) {
        val url = getString(R.string.changeUserURL) + "/$user_id"
        disableInputs()
        val jsonBody = JSONObject()
        jsonBody.put("name", name.trim())
        jsonBody.put("username", usernameParam.trim())
        jsonBody.put("email", email.trim())
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.PUT, url,
            Response.Listener { response ->
                try {
                    updateDataLogIn(context = this, name = name, username = username, email = email)
                    handleUpdateUserResponse(response)
                    Log.d("APP_REST",  handleUpdateUserResponse(response).toString())
                    enableInputs()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    enableInputs()
                }
            },
            Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    loginBeforeChangeCredentials(username = username)
                } else {
                    val errorMessage: String = when(error) {
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
                    enableInputs()
                }
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

            override fun getRetryPolicy(): RetryPolicy {
                return DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DEFAULT_BACKOFF_MULT
                )
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

            val mainPageNavIntent = Intent(this, MainPageNav::class.java)
            mainPageNavIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            mainPageNavIntent.putExtra("user_id", res.id)
            mainPageNavIntent.putExtra("username", res.username)
            mainPageNavIntent.putExtra("token", token)
            mainPageNavIntent.putExtra("name", res.name)
            mainPageNavIntent.putExtra("email", res.email)
            startActivity(mainPageNavIntent)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST", e.toString())
        }
    }

    private fun loginBeforeChangeCredentials(username: String) {
        val password = loadPassword(context = this)

        val url = getString(R.string.loginURL)

        val jsonBody = JSONObject()
        jsonBody.put("username", username.trim())
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    handleLoginChangeCredentialsResponse(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    enableInputs()
                    Toast.makeText(this, "Failed to renew token", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                enableInputs()
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                Toast.makeText(this, "Failed to renew token", Toast.LENGTH_SHORT).show()
            }) {

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

    private fun handleLoginChangeCredentialsResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            token = res.token
            SaveToken(context = this, tokenData = res.token)
            changeCredentials(name = editTextCurrentName.text.toString(), usernameParam = editTextCurrentUserName.text.toString(), email = editTextCurrentEmail.text.toString())
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }


    private fun showError(message: String) {
        labelValidation.setTextColor(Color.RED)
        labelValidation.text = message
        labelValidation.visibility = View.VISIBLE
    }

    private fun validateName(name: String): Boolean {
        val namePattern = Regex("^[a-zA-ZÀ-ÖØ-öø-ÿ ]{2,30}\$")
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
        val usernamePattern = Regex("^[a-zA-Z0-9_!@#\$%^&*().\\-+=~]{5,15}$")
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

    private fun updateDataLogIn(context: Context, name: String, email: String, username: String) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPref = EncryptedSharedPreferences.create(
                context,
                context.getString(R.string.userLogInFile),
                masterKey,
                PrefKeyEncryptionScheme.AES256_SIV,
                PrefValueEncryptionScheme.AES256_GCM
            )

            val editor = sharedPref.edit()

            editor.putString(context.getString(R.string.nameKey), name)
            editor.putString(context.getString(R.string.emailKey), email)
            editor.putString(context.getString(R.string.usernameKey), username)

            editor.apply()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updatePasswordDataLogIn(context: Context, password: String) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPref = EncryptedSharedPreferences.create(
                context,
                context.getString(R.string.userLogInFile),
                masterKey,
                PrefKeyEncryptionScheme.AES256_SIV,
                PrefValueEncryptionScheme.AES256_GCM
            )

            val editor = sharedPref.edit()

            editor.putString(context.getString(R.string.passwordKey), password)

            editor.apply()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadPassword(context: Context): String? {
        var password: String? = null
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPref = EncryptedSharedPreferences.create(
                context,
                context.getString(R.string.userLogInFile),
                masterKey,
                PrefKeyEncryptionScheme.AES256_SIV,
                PrefValueEncryptionScheme.AES256_GCM
            )
            password = sharedPref.getString(context.getString(R.string.passwordKey), null)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return password
    }


    private fun changeToProfile() {
        finish()
    }

    private fun disableInputs() {
        frameProgress.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        saveChangesButton.isEnabled = false
        editTextCurrentName.isEnabled = false
        imageViewEditName.isEnabled = false
        editTextCurrentUserName.isEnabled = false
        imageViewEditUserName.isEnabled = false
        editTextCurrentPassword.isEnabled = false
        imageViewEditPassword.isEnabled = false
        editTextCurrentEmail.isEnabled = false
        imageViewEditEmail.isEnabled = false
    }

    private fun enableInputs() {
        frameProgress.visibility = View.GONE
        progressBar.visibility = View.GONE
        saveChangesButton.isEnabled = true
        editTextCurrentName.isEnabled = true
        imageViewEditName.isEnabled = true
        editTextCurrentUserName.isEnabled = true
        imageViewEditUserName.isEnabled = true
        editTextCurrentPassword.isEnabled = true
        imageViewEditPassword.isEnabled = true
        editTextCurrentEmail.isEnabled = true
        imageViewEditEmail.isEnabled = true
    }

    private fun loadToken(context: Context): String? {
        var token: String? = null
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPref = EncryptedSharedPreferences.create(
                context,
                context.getString(R.string.userLogInFile),
                masterKey,
                PrefKeyEncryptionScheme.AES256_SIV,
                PrefValueEncryptionScheme.AES256_GCM
            )
            token = sharedPref.getString(context.getString(R.string.tokenKey), null)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return token
    }

    private fun SaveToken(context: Context, tokenData: String) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPref = EncryptedSharedPreferences.create(
                context,
                context.getString(R.string.userLogInFile),
                masterKey,
                PrefKeyEncryptionScheme.AES256_SIV,
                PrefValueEncryptionScheme.AES256_GCM
            )
            val editor = sharedPref.edit()
            editor.putString(context.getString(R.string.tokenKey), tokenData)
            editor.apply()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun enableEditText(editText: EditText) {
        editText.isEnabled = true
        editText.requestFocus()
        editText.setSelection(editText.text.length)
        val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}
