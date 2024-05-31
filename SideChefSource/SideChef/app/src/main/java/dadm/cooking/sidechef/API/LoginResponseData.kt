package dadm.cooking.sidechef.API

import com.google.gson.annotations.SerializedName

data class LoginResponseData(
    @SerializedName("id") val id: Int,
    @SerializedName("token") val token : String,
    @SerializedName("username") val username : String
)
