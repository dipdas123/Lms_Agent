package tech.redltd.lmsAgent.network.thana


import com.google.gson.annotations.SerializedName

data class ThanaResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("payload")
    val thanaPayload: List<ThanaPayload>,
    @SerializedName("success")
    val success: Boolean
)