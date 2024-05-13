package dadm.cooking.sidechef

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class click_recipe_details : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private var recipeId: Int = 0
    private lateinit var recipeName: String
    private lateinit var recipePreparation: String
    private lateinit var recipePrepTime: String
    private lateinit var recipeType: String
    private lateinit var recipeImage: String
    private lateinit var recipeIngredients: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_click_recipe_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
    }

    private fun setupView() {
        backButton = findViewById(R.id.goBackArrowRecipeDetails)
        backButton.setOnClickListener {
            changeToMainActivity()
        }

        recipeId = intent.getIntExtra("recipe_id", 0)
        recipeName = intent.getStringExtra("recipe_name") ?: ""
        recipePreparation = intent.getStringExtra("recipe_preparation") ?: ""
        recipePrepTime = intent.getStringExtra("recipe_prepTime") ?: ""
        recipeType = intent.getStringExtra("recipe_type") ?: ""
        recipeImage = intent.getStringExtra("recipe_picture") ?: ""
        recipeIngredients = intent.getStringExtra("recipe_ingredients") ?: ""

    }

    private fun changeToMainActivity() {
        finish()
    }
}