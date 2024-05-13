package dadm.cooking.sidechef

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.graphics.PorterDuff
import android.view.LayoutInflater

class MyRecipes_RecyclerViewAdaptor(private val context: Context, private var recipeList: MutableList<GetSavedRecipesResponseData>): RecyclerView.Adapter<MyRecipes_RecyclerViewAdaptor.ViewHolder>() {
    var onItemClick : ((GetSavedRecipesResponseData) -> Unit)? = null
    var onImageClick : ((GetSavedRecipesResponseData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRecipes_RecyclerViewAdaptor.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecipes_RecyclerViewAdaptor.ViewHolder, position: Int) {
        val recipe = recipeList[position]

        holder.recipeId = recipe.id
//        val decodedString: ByteArray = Base64.decode(recipeList[position].picture, Base64.DEFAULT)
//        val decodedByte: Bitmap =  BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//        holder.recipeImage.setImageBitmap(decodedByte)
        holder.recipeTitle.text = recipe.name
        holder.recipeType.text = recipe.type
        holder.recipeTime.text = recipe.prepTime.toString()
        holder.recipe_Fk = recipe.idRecipe
        holder.favoritedIcon.setColorFilter(ContextCompat.getColor(context, R.color.redLogOut), PorterDuff.Mode.SRC_IN)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(recipe)
        }

        holder.recipeImage.setOnClickListener {
            onImageClick?.invoke(recipe)

            recipeList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var recipeId: Int = 0
        var recipe_Fk: Int = 0
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeType: TextView = itemView.findViewById(R.id.recipeType)
        val recipeTime: TextView = itemView.findViewById(R.id.recipeTime)
        val favoritedIcon: ImageView = itemView.findViewById(R.id.recipeFavorited)
    }
}