package dadm.cooking.sidechef

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.inputmethod.EditorInfo

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backButton = findViewById(R.id.goBackArrowSettings)
        editTextCurrentName = findViewById(R.id.textViewCurrentName)
        imageViewEditName = findViewById(R.id.imageViewEditName)
        editTextCurrentUserName = findViewById(R.id.textViewCurrentUserName)
        imageViewEditUserName = findViewById(R.id.imageViewEditUserName)
        editTextCurrentPassword = findViewById(R.id.textViewCurrentPassword)
        imageViewEditPassword = findViewById(R.id.imageViewEditPassword)
        editTextCurrentEmail = findViewById(R.id.textViewCurrentEmail)
        imageViewEditEmail = findViewById(R.id.imageViewEditEmail)

        imageViewEditName.setOnClickListener {
            enableEditText(editTextCurrentName)
        }
        backButton.setOnClickListener {
            changeToProfile()
        }

        editTextCurrentName.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val currentText = editTextCurrentName.text.toString()
                editTextCurrentName.setText(currentText)
                editTextCurrentName.isEnabled = false
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextCurrentName.windowToken, 0)
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
                val currentText = editTextCurrentUserName.text.toString()
                editTextCurrentUserName.setText(currentText)
                editTextCurrentUserName.isEnabled = false
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextCurrentUserName.windowToken, 0)
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
                val currentText = editTextCurrentEmail.text.toString()
                editTextCurrentEmail.setText(currentText)
                editTextCurrentEmail.isEnabled = false
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextCurrentEmail.windowToken, 0)
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
                val currentText = editTextCurrentPassword.text.toString()
                editTextCurrentPassword.setText(currentText)
                editTextCurrentPassword.isEnabled = false
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextCurrentPassword.windowToken, 0)
                true
            } else {
                false
            }
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
