package tech.redltd.lmsAgent.activities.loginActivity

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    val success:Boolean,
    val payload: Any,
    val message:String
)

data class LoginBody(
    val phone:String,
    val password: String,
    val imei:String,
    val device_id:String
)

data class AgentLoginResponse(
    @SerializedName("agentInfo")
    val agentInfo: AgentInfo,
    @SerializedName("isStatus")
    val isStatus: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("x-Auth-Token")
    val xAuthToken: String
)

data class AgentInfo(
    @SerializedName("adress")
    val adress: String,
    @SerializedName("agentID")
    val agentID: Int,
    @SerializedName("agentgroup")
    val agentgroup: String,
    @SerializedName("area")
    val area: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fcmKey")
    val fcmKey: String,
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("imei_no")
    val imeiNo: String,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("nid_number")
    val nidNumber: String,
    @SerializedName("otp")
    val otp: Any,
    @SerializedName("password")
    val password: String,
    @SerializedName("pinretry")
    val pinretry: String,
    @SerializedName("status")
    val status: Int
)

data class LoginOTPCheck(
    @SerializedName("ResponseCode")
    val ResponseCode: Int
)