package dadm.cooking.sidechef

import com.google.gson.annotations.SerializedName

data class GetRecipesResponseData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name : String,
    @SerializedName("preparation") val preparation : String,
    @SerializedName("prepTime") val prepTime : Int,
    @SerializedName("type") val type : String,
    @SerializedName("picture") val picture : String,
    @SerializedName("ingredients") val ingredients : String
)
