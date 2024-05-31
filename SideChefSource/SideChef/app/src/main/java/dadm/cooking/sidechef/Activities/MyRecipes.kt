package dadm.cooking.sidechef.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKey
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import dadm.cooking.sidechef.API.GetSavedRecipesResponseData
import dadm.cooking.sidechef.API.LoginResponseData
import dadm.cooking.sidechef.Adapters.MyRecipes_RecyclerViewAdaptor
import dadm.cooking.sidechef.R
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.security.GeneralSecurityException

class MyRecipes : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var frameProgress: FrameLayout
    private lateinit var recyclerView: RecyclerView
    private val DEFAULT_BACKOFF_MULT: Float = 1f
    private var user_id: Int = 0
    private lateinit var username: String
    private lateinit var token: String
    private lateinit var email: String
    private lateinit var name: String
    private var id_recipe_remove: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_recipes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
    }



    private fun setupView() {
        frameProgress = findViewById(R.id.frameProgressMyRecipes)
        progressBar = findViewById(R.id.progressBarMyRecipes)
        backButton = findViewById(R.id.goBackArrowRecipes)
        recyclerView = findViewById(R.id.recyclerMyRecipes)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        user_id = intent.getIntExtra("user_id", 0)
        username = intent.getStringExtra("username").toString()
        token = loadToken(context = this).toString()
        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()
        progressBar.visibility = View.VISIBLE
        frameProgress.visibility = View.VISIBLE
        backButton.isEnabled = false
        getSavedRecipes(token = token)

        backButton.setOnClickListener {
            changeToProfile()
        }
    }

    private fun getSavedRecipes(token: String) {
        val url = getString(R.string.getSavedRecipesURL)
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    handleGetSavedRecipesResponse(response)
                    progressBar.visibility = View.GONE
                    frameProgress.visibility = View.GONE
                    backButton.isEnabled = true
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace())
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    backButton.isEnabled = true
                    Toast.makeText(this, "Failed to get recipes", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    loginBeforeGetRecipes(username = username)
                } else {
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    backButton.isEnabled = true
                    Toast.makeText(this, "Failed to get recipes", Toast.LENGTH_SHORT).show()
                    Log.d("APP_REST", error.toString())
                    Log.d("APP_REST", error.networkResponse.statusCode.toString())
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

    private fun handleGetSavedRecipesResponse(response: String) {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()

        try {
            val res: MutableList<GetSavedRecipesResponseData> = gson.fromJson(response, Array<GetSavedRecipesResponseData>::class.java).toMutableList()
            val adapter = MyRecipes_RecyclerViewAdaptor(context = this, recipeList = res)
            val dividerItemDecoration = DividerItemDecoration(this , DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this ,
                R.drawable.vertical_spacing_recycler
            )!!)
            recyclerView.addItemDecoration(dividerItemDecoration)
            recyclerView.adapter = adapter

            adapter.onItemClick = { recipe ->
                val intent = Intent(this, click_recipe_details::class.java)
                intent.putExtra("recipe_id", recipe.id)
                intent.putExtra("recipe_name", recipe.name)
                intent.putExtra("recipe_preparation", recipe.preparation)
                intent.putExtra("recipe_prepTime", recipe.prepTime)
                intent.putExtra("recipe_type", recipe.type)
                intent.putExtra("recipe_picture", recipe.picture)
                intent.putExtra("recipe_ingredients", recipe.ingredients)
                intent.putExtra("fk_recipe_id", recipe.id_recipe)
                startActivity(intent)
            }

            adapter.onImageClick = { recipe ->
                id_recipe_remove = recipe.id
                removeFavoriteRecipe(recipe_id = recipe.id, token = token)
            }
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }

    private fun loginBeforeGetRecipes(username: String) {
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
                    handleLoginResponse(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    backButton.isEnabled = true
                    Toast.makeText(this, "Failed to renew token", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                backButton.isEnabled = true
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

    private fun handleLoginResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            token = res.token
            SaveToken(context = this, tokenData = res.token)
            getSavedRecipes(token = token)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }


    private fun removeFavoriteRecipe(recipe_id: Int, token: String) {
        val url = getString(R.string.removeRecipeURL) + "/$recipe_id"
        val stringRequest: StringRequest = object : StringRequest(
            Method.DELETE, url,
            Response.Listener { response ->
                try {
                    progressBar.visibility = View.GONE
                    frameProgress.visibility = View.GONE
                    backButton.isEnabled = true
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace())
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    backButton.isEnabled = true
                    Toast.makeText(this, "Failed to remove recipes", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    loginBeforeRemoveRecipe(username = username)
                } else {
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    backButton.isEnabled = true
                    Toast.makeText(this, "Failed to remove recipes", Toast.LENGTH_SHORT).show()
                    Log.d("APP_REST", error.toString())
                    Log.d("APP_REST", error.networkResponse.statusCode.toString())
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

    private fun loginBeforeRemoveRecipe(username: String) {
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
                    handleLoginRemoveRecipeResponse(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    backButton.isEnabled = true
                    Toast.makeText(this, "Failed to renew token", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                backButton.isEnabled = true
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

    private fun handleLoginRemoveRecipeResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            token = res.token
            SaveToken(context = this, tokenData = res.token)
            removeFavoriteRecipe(recipe_id = id_recipe_remove, token = token)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
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

    private fun changeToProfile() {
        finish()
    }
}
