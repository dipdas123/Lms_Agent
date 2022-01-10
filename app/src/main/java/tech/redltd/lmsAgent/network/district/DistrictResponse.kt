package tech.redltd.lmsAgent.network.district


import com.google.gson.annotations.SerializedName

data class DistrictResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("payload")
    val payload: List<Payload>,
    @SerializedName("success")
    val success: Boolean
)