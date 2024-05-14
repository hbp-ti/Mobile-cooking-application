package dadm.cooking.sidechef

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var myrecipesButton: Button
    private lateinit var settingsButton: Button
    private lateinit var logoutButton: Button
    private lateinit var name_label: TextView
    private lateinit var username_label: TextView
    private var user_id: Int = 0
    private lateinit var username: String
    private lateinit var token: String
    private lateinit var email: String
    private lateinit var name: String

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view, savedInstanceState)
    }

    private fun setupView(view: View, savedInstanceState: Bundle?) {
        myrecipesButton = view.findViewById(R.id.buttonMyRecipes)
        settingsButton = view.findViewById(R.id.buttonSettings)
        name_label = view.findViewById(R.id.nameUser)
        username_label = view.findViewById(R.id.username)
        logoutButton = view.findViewById(R.id.buttonLogOut)

        user_id = requireActivity().intent.getIntExtra("user_id", 0)
        username = requireActivity().intent.getStringExtra("username").toString()
        token = loadToken(context = requireActivity()).toString()
        name = requireActivity().intent.getStringExtra("name").toString()
        email = requireActivity().intent.getStringExtra("email").toString()

        name_label.text = name
        username_label.text = username

        myrecipesButton.setOnClickListener {
            changeToMyRecipes()
        }

        settingsButton.setOnClickListener {
            changeToSettings()
        }

        logoutButton.setOnClickListener {
            deleteDataLogIn(context = requireActivity())
            changeToLogIn()
        }
    }

    private fun deleteDataLogIn(context: Context) {
        try {
            val fileName = context.getString(R.string.userLogInFile)

            context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun changeToMyRecipes() {
        val intent = Intent(requireActivity(), MyRecipes::class.java)
        intent.putExtra("user_id", user_id)
        intent.putExtra("username", username)
        intent.putExtra("name", name)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun changeToSettings() {
        val intent = Intent(requireActivity(), Settings::class.java)
        intent.putExtra("user_id", user_id)
        intent.putExtra("username", username)
        intent.putExtra("name", name)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun changeToLogIn() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
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
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}