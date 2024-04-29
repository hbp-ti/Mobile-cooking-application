package dadm.cooking.sidechef

import com.google.gson.annotations.SerializedName

data class GetUserProfileData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username : String
)
