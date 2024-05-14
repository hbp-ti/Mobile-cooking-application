package dadm.cooking.sidechef

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class click_recipe_details : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var recipeNameLabel: TextView
    private lateinit var recipeSmallNameLabel: TextView
    private lateinit var recipeTypeLabel: TextView
    private lateinit var recipeTimeLabel: TextView
    private lateinit var recipeIngredientsLabel: TextView
    private lateinit var recipeInstructionsLabel: TextView
    private lateinit var recipeImageView: ImageView
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
        recipeNameLabel = findViewById(R.id.RecipeNameDetails)
        recipeSmallNameLabel = findViewById(R.id.RecipeSmallNameDetails)
        recipeTypeLabel = findViewById(R.id.RecipeTypeDetails)
        recipeTimeLabel = findViewById(R.id.RecipeTimeDetails)
        recipeImageView = findViewById(R.id.recipeImageDetailed)
        recipeIngredientsLabel = findViewById(R.id.ingredientListDetails)
        recipeInstructionsLabel = findViewById(R.id.instructionsDetails)
        backButton = findViewById(R.id.goBackArrowRecipeDetails)

        backButton.setOnClickListener {
            changeToMainActivity()
        }

        recipeId = intent.getIntExtra("recipe_id", 0)
        recipeName = intent.getStringExtra("recipe_name") ?: ""
        recipePreparation = intent.getStringExtra("recipe_preparation") ?: ""
        recipePrepTime = intent.getIntExtra("recipe_prepTime", 0).toString()
        recipeType = intent.getStringExtra("recipe_type") ?: ""
        recipeImage = intent.getStringExtra("recipe_picture") ?: ""
        recipeIngredients = intent.getStringExtra("recipe_ingredients") ?: ""


        val ingredientsList: MutableList<String> = recipeIngredients.split(",")
            .map { it.trim() }
            .map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
            .toMutableList()

        val stringBuilder: StringBuilder = StringBuilder()

        for(ingredient in ingredientsList) {
            stringBuilder.append("- ").append(ingredient).append("\n")
        }

        recipeNameLabel.text = recipeName
        recipeSmallNameLabel.text = recipeName
        recipeTypeLabel.text = recipeType
        recipeTimeLabel.text = recipePrepTime+"m"
        //recipeImageView.setImageBitmap(recipeImage)
        recipeIngredientsLabel.text = stringBuilder.toString()
        recipeInstructionsLabel.text = recipePreparation

    }

    private fun changeToMainActivity() {
        finish()
    }
}