package tech.redltd.lmsAgent.network


import com.google.gson.annotations.SerializedName

data class AgentPasswordUpdateResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("isstatus")
    val isstatus: String
)