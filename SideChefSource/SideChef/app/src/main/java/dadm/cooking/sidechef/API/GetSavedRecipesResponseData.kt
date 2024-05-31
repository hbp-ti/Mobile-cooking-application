package dadm.cooking.sidechef.API

import com.google.gson.annotations.SerializedName

data class GetSavedRecipesResponseData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name : String,
    @SerializedName("preparation") val preparation : String,
    @SerializedName("prepTime") val prepTime : Int,
    @SerializedName("type") val type : String,
    @SerializedName("picture") val picture : String,
    @SerializedName("ingredients") val ingredients : String,
    @SerializedName("id_recipe") val id_recipe : Int,
)
