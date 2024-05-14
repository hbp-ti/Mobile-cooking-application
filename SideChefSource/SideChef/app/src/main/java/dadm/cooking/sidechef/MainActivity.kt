package dadm.cooking.sidechef

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
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


class MainActivity : AppCompatActivity() {

    private lateinit var signUpLink: TextView
    private lateinit var buttonLogIn: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var labelValidation: TextView
    private lateinit var keeploginBox: CheckBox
    private lateinit var progressBar: ProgressBar
    private lateinit var frameProgress: FrameLayout
    private var id: Int = 0
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var token: String
    private val DEFAULT_BACKOFF_MULT: Float = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val (idLoaded, nameLoaded, emailLoaded, usernameLoaded) = loadDataLogIn(this)
        if (idLoaded != null && nameLoaded != null && emailLoaded != null && usernameLoaded != null) {
            val intent = Intent(this, MainPageNav::class.java)
            intent.putExtra("user_id", idLoaded)
            intent.putExtra("username", usernameLoaded)
            intent.putExtra("email", emailLoaded)
            intent.putExtra("name", nameLoaded)
            startActivity(intent)
        } else {
            setContentView(R.layout.activity_main)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            setupView()
        }
    }

    private fun setupView() {
        frameProgress = findViewById(R.id.frameProgressLogIn)
        progressBar = findViewById(R.id.progressBarLogIn)
        signUpLink = findViewById(R.id.signUpLabel)
        signUpLink.setOnClickListener {
            changeToSignUp()
        }

        buttonLogIn = findViewById(R.id.buttonLogIn)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        labelValidation = findViewById(R.id.labelValidationLogin)
        keeploginBox = findViewById(R.id.checkBoxKeepLogIn)
        buttonLogIn.setOnClickListener {
            val isCredentialsVal: Boolean = validateCredentials(usernameInput.text.toString(), passwordInput.text.toString())
            if (isCredentialsVal) {
                buttonLogIn.isEnabled = false
                frameProgress.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                usernameInput.isEnabled = false
                passwordInput.isEnabled = false
                keeploginBox.isEnabled = false
                signUpLink.isEnabled = false
                login(username = usernameInput.text.toString(), pass = passwordInput.text.toString())
            }
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        val usernamePattern = Regex("^[a-zA-Z0-9_!@#\$%^&*().\\-+=~]{5,15}$")
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()-_=+\\\\|<>?{}\\[\\]~])(?!.*\\s).{8,}\$")
        if (username.isBlank() or password.isBlank()) {
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

    private fun login(username: String, pass: String) {
        val url = getString(R.string.loginURL)

        val jsonBody = JSONObject()
        jsonBody.put("username", username.trim())
        jsonBody.put("password", pass.trim())
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    handleLoginResponse(response)
                    Log.d("APP_REST",  handleLoginResponse(response).toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    showError("Invalid username or password")
                    buttonLogIn.isEnabled = true
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    usernameInput.isEnabled = true
                    passwordInput.isEnabled = true
                    keeploginBox.isEnabled = true
                    signUpLink.isEnabled = true
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
                buttonLogIn.isEnabled = true
                frameProgress.visibility = View.GONE
                progressBar.visibility = View.GONE
                usernameInput.isEnabled = true
                passwordInput.isEnabled = true
                keeploginBox.isEnabled = true
                signUpLink.isEnabled = true
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


    private fun handleLoginResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            id = res.id
            token = res.token
            username = res.username
            password = passwordInput.text.toString()
            getUserInfo(res.id)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }


    private fun getUserInfo(id_user: Int) {
        val url = getString(R.string.getuserURL) + "/$id_user"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    handleGetUserResponse(response)
                    Log.d("APP_REST",  handleGetUserResponse(response).toString())
                    progressBar.visibility = View.GONE
                    frameProgress.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace())
                    buttonLogIn.isEnabled = true
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    usernameInput.isEnabled = true
                    passwordInput.isEnabled = true
                    keeploginBox.isEnabled = true
                    signUpLink.isEnabled = true
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
                    else -> "Unknown error"
                }
                showError(errorMessage)
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                buttonLogIn.isEnabled = true
                frameProgress.visibility = View.GONE
                progressBar.visibility = View.GONE
                usernameInput.isEnabled = true
                passwordInput.isEnabled = true
                keeploginBox.isEnabled = true
                signUpLink.isEnabled = true
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Authorization"] = "Bearer $token"
                return headers
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

    private fun handleGetUserResponse(response: String) {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()

        try {
            val res: GetUserProfileData = gson.fromJson(response, GetUserProfileData::class.java)
            email = res.email
            name = res.name
            if (keeploginBox.isChecked) {
                saveDataLogIn(context = this,username = username, password = password, token = token, id = id, email = email, name = name)
            } else {
                savePasswordLogIn(context = this, password = password, token = token)
            }
            changeToMainPage(id = id, username = username, name = name, email = email)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }

    private fun showError(message: String) {
        labelValidation.setTextColor(Color.RED)
        labelValidation.text = message
        labelValidation.visibility = View.VISIBLE
    }

    private fun changeToSignUp() {
        val intent = Intent(this, CreateAccount::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeToMainPage(id: Int, username: String, name: String, email: String) {
        val intent = Intent(this, MainPageNav::class.java)
        intent.putExtra("user_id", id)
        intent.putExtra("username", username)
        intent.putExtra("email", email)
        intent.putExtra("name", name)
        startActivity(intent)
        finish()
    }

    private fun keepLogin(): Boolean {
        val sharedPref = getSharedPreferences(R.string.userLogInFile.toString(), Context.MODE_PRIVATE)
        return sharedPref.getBoolean(R.string.keepLoginKey.toString(), false)
    }

    private fun saveDataLogIn(context: Context, username: String, password: String, token: String, id: Int, email: String, name: String) {

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
            editor.putInt(context.getString(R.string.idKey), id)
            editor.putString(context.getString(R.string.nameKey), name)
            editor.putString(context.getString(R.string.emailKey), email)
            editor.putString(context.getString(R.string.tokenKey), token)
            editor.putString(context.getString(R.string.usernameKey), username)
            editor.putString(context.getString(R.string.passwordKey), password)
            editor.apply()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun savePasswordLogIn(context: Context, password: String, token: String) {
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
            editor.putString(context.getString(R.string.tokenKey), token)
            editor.apply()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadDataLogIn(context: Context): UserData {
        var username: String? = null
        var name: String? = null
        var email: String? = null
        var id: Int? = null
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
            id = sharedPref.getInt(context.getString(R.string.idKey), -1)
            name = sharedPref.getString(context.getString(R.string.nameKey), null)
            email = sharedPref.getString(context.getString(R.string.emailKey), null)
            username = sharedPref.getString(context.getString(R.string.usernameKey), null)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return UserData(id, name, email, username)
    }

}
