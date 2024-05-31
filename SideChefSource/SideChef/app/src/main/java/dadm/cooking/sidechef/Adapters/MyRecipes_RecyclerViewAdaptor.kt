package dadm.cooking.sidechef.Adapters

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
import android.util.Log
import android.view.LayoutInflater
import dadm.cooking.sidechef.API.GetSavedRecipesResponseData
import dadm.cooking.sidechef.R

class MyRecipes_RecyclerViewAdaptor(private val context: Context, private var recipeList: MutableList<GetSavedRecipesResponseData>): RecyclerView.Adapter<MyRecipes_RecyclerViewAdaptor.ViewHolder>() {
    var onItemClick : ((GetSavedRecipesResponseData) -> Unit)? = null
    var onImageClick : ((GetSavedRecipesResponseData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipeList[position]

        holder.recipeId = recipe.id
        try {
            val decodedString: ByteArray = Base64.decode(recipeList[position].picture, Base64.DEFAULT)
            val decodedByte: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.recipeImage.setImageBitmap(decodedByte)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.recipeTitle.text = recipe.name
        holder.recipeType.text = recipe.type
        holder.recipeTime.text = recipe.prepTime.toString()+"m"
        holder.recipe_Fk = recipe.id_recipe
        holder.favoritedIcon.setColorFilter(ContextCompat.getColor(context, R.color.redLogOut), PorterDuff.Mode.SRC_IN)
        Log.d("APP_ADAPTER_Recipes", recipeList.toString())
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(recipe)
        }

        holder.favoritedIcon.setOnClickListener {
            // Parar a propagaçao para o pai (itemview)
            it?.parent?.requestDisallowInterceptTouchEvent(true)

            onImageClick?.invoke(recipe)
            recipeList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        Log.d("APP_ADAPTER_Recipes", recipeList.size.toString())
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