package tech.redltd.lmsAgent.activities.otpActivity

import com.google.gson.annotations.SerializedName

data class OtpBody(
    val phone:String,
    val otp:String,
    val fcm:String?
)

data class OtpResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("payload")
    val payload: Payload,
    @SerializedName("success")
    val success: Boolean
)

data class Payload(
    @SerializedName("token")
    val token: String,
    @SerializedName("user_info")
    val userInfo: UserInfo
)

data class UserInfo(
    @SerializedName("agent_email")
    val agentEmail: String,
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("agent_password")
    val agentPassword:String
)


data class OtpErrorBody(val success:Boolean,val message:String)