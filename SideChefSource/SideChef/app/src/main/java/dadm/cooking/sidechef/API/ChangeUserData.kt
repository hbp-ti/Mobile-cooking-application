package dadm.cooking.sidechef.API

import com.google.gson.annotations.SerializedName

data class ChangeUserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String
)
