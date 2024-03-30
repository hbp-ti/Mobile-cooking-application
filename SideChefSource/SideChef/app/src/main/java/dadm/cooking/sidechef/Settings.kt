package dadm.cooking.sidechef

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Settings : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var editTextCurrentName: EditText
    private lateinit var imageViewEditName: ImageView

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
        backButton.setOnClickListener {
            changeToProfile()
        }

        editTextCurrentName = findViewById(R.id.textViewCurrentName)
        imageViewEditName = findViewById(R.id.imageViewEditName)

        imageViewEditName.setOnClickListener {
            editTextCurrentName.isEnabled = true
            editTextCurrentName.requestFocus()
            editTextCurrentName.setSelection(editTextCurrentName.text.length)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editTextCurrentName, InputMethodManager.SHOW_IMPLICIT)
        }

    }

    private fun changeToProfile() {
        finish()
    }
}

