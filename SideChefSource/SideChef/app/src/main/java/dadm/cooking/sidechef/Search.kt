package dadm.cooking.sidechef

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.security.GeneralSecurityException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Search.newInstance] factory method to
 * create an instance of this fragment.
 */
class Search : Fragment() {

    private lateinit var searchBar: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var frameProgress: FrameLayout
    private lateinit var recyclerView: RecyclerView
    private val DEFAULT_BACKOFF_MULT: Float = 1f
    private var user_id: Int = 0
    private lateinit var username: String
    private lateinit var token: String
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var name_recipe: String
    private lateinit var preparation_recipe: String
    private var prepTime_recipe: Int = -1
    private lateinit var type_recipe: String
    private lateinit var picture_recipe: String
    private lateinit var ingredients_recipe: String
    private var id_Recipe: Int = -1
    private lateinit var nameRecipeWord: String

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view = view, savedInstanceState = savedInstanceState)
    }

    private fun setupView(view: View, savedInstanceState: Bundle?) {
        searchBar = view.findViewById(R.id.search_bar)
        //Nao sei pq mas fica assim mal
        val editText = searchBar.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(Color.BLACK)

        frameProgress = view.findViewById(R.id.frameProgressSearch)
        progressBar = view.findViewById(R.id.progressBarSearch)
        recyclerView = view.findViewById(R.id.recyclerSearch)
        user_id = requireActivity().intent.getIntExtra("user_id", 0)
        username = requireActivity().intent.getStringExtra("username").toString()
        token = loadToken(context = requireActivity()).toString()
        name = requireActivity().intent.getStringExtra("name").toString()
        email = requireActivity().intent.getStringExtra("email").toString()

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                nameRecipeWord = query.trim()
                progressBar.visibility = View.VISIBLE
                frameProgress.visibility = View.VISIBLE
                searchBar.isEnabled = false
                getRecipes(token = token, name = nameRecipeWord)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }


    private fun getRecipes(token: String, name: String) {
        val url = getString(R.string.getRecipeByNameURL) + "/$name"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    handleGetRecipesResponse(response)
                    progressBar.visibility = View.GONE
                    frameProgress.visibility = View.GONE
                    searchBar.isEnabled = true
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace())
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    searchBar.isEnabled = true
                    Toast.makeText(requireActivity(), "Failed to get recipes", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    loginBeforeGetRecipes(username = username)
                } else {
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    searchBar.isEnabled = true
                    Toast.makeText(requireActivity(), "Failed to get recipes", Toast.LENGTH_SHORT).show()
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
                    10000,
                    0,
                    DEFAULT_BACKOFF_MULT
                )
            }

        }
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(stringRequest)
    }

    private fun handleGetRecipesResponse(response: String) {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()

        try {
            val res: List<GetRecipesResponseData> = gson.fromJson(response, Array<GetRecipesResponseData>::class.java).toList()
            Log.d("APP_REST", response)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            val dividerItemDecoration = DividerItemDecoration(requireActivity() , DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.vertical_spacing_recycler)!!)
            recyclerView.addItemDecoration(dividerItemDecoration)
            val adapter = Search_RecyclerViewAdapter(recipeList = res)
            recyclerView.adapter = adapter

            adapter.onItemClick = { recipe ->
                val intent = Intent(requireActivity(), click_recipe_details::class.java)
                intent.putExtra("recipe_id", recipe.id)
                intent.putExtra("recipe_name", recipe.name)
                intent.putExtra("recipe_preparation", recipe.preparation)
                intent.putExtra("recipe_prepTime", recipe.prepTime)
                intent.putExtra("recipe_type", recipe.type)
                intent.putExtra("recipe_picture", recipe.picture)
                intent.putExtra("recipe_ingredients", recipe.ingredients)
                startActivity(intent)
            }

            adapter.onImageClick = { recipe ->
                name_recipe = recipe.name
                preparation_recipe = recipe.preparation
                prepTime_recipe = recipe.prepTime
                type_recipe = recipe.type
                picture_recipe = recipe.picture
                ingredients_recipe = recipe.ingredients
                id_Recipe = recipe.id
                favoriteRecipe(token = token, name = recipe.name, preparation = recipe.preparation, prepTime = recipe.prepTime, type = recipe.type, picture = recipe.picture, ingredients = recipe.ingredients, idUser = user_id , id_Recipe = recipe.id)
            }
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }

    private fun loginBeforeGetRecipes(username: String) {
        val password = loadPassword(context = requireActivity())

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
                    searchBar.isEnabled = true
                }
            },
            Response.ErrorListener { error ->
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(stringRequest)
    }

    private fun handleLoginResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            token = res.token
            SaveToken(context = requireActivity(), tokenData = res.token)
            getRecipes(token = token, name = nameRecipeWord)
        } catch (e: JsonSyntaxException) {
            Log.d("APP_REST",e.toString())
        }
    }


    private fun favoriteRecipe(token: String, name: String, preparation: String, prepTime: Int, type: String, picture: String, ingredients: String, idUser: Int, id_Recipe: Int) {
        val url = getString(R.string.addRecipeURL)

        val jsonBody = JSONObject()
        jsonBody.put("name", name.trim())
        jsonBody.put("preparation", preparation.trim())
        jsonBody.put("prepTime", prepTime)
        jsonBody.put("type", type.trim())
        jsonBody.put("picture", picture.trim())
        jsonBody.put("ingredients", ingredients.trim())
        jsonBody.put("idUser", idUser)
        jsonBody.put("idRec", id_Recipe)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    progressBar.visibility = View.GONE
                    frameProgress.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace())
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), "Failed to add recipes", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    loginBeforeAddRecipe(username = username)
                } else {
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), "Failed to add recipes", Toast.LENGTH_SHORT).show()
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

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(stringRequest)
    }

    private fun loginBeforeAddRecipe(username: String) {
        val password = loadPassword(context = requireActivity())

        val url = getString(R.string.loginURL)

        val jsonBody = JSONObject()
        jsonBody.put("username", username.trim())
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    handleLoginAddRecipeResponse(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("APP_REST","JSONException" + e.printStackTrace() )
                    frameProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), "Failed to renew token", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.d("APP_REST", error.toString())
                Log.d("APP_REST", error.networkResponse.statusCode.toString())
                Toast.makeText(requireActivity(), "Failed to renew token", Toast.LENGTH_SHORT).show()
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(stringRequest)
    }

    private fun handleLoginAddRecipeResponse(response: String){
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val res: LoginResponseData = gson.fromJson(response, LoginResponseData::class.java)
            token = res.token
            SaveToken(context = requireActivity(), tokenData = res.token)
            favoriteRecipe(token = token, name = name_recipe, preparation = preparation_recipe, prepTime = prepTime_recipe, type = type_recipe, picture = picture_recipe, ingredients = ingredients_recipe, idUser = user_id , id_Recipe = id_Recipe)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Search.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Search().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}