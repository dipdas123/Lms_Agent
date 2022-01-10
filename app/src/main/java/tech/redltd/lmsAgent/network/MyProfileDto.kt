package tech.redltd.lmsAgent.network

import com.google.gson.annotations.SerializedName


data class MyProfileResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("payload")
    val payload: Payload,
    @SerializedName("success")
    val success: Boolean
)

data class Payload(
    @SerializedName("address")
    val address: String,
    @SerializedName("age_calculation_date")
    val ageCalculationDate: String,
    @SerializedName("age_segment")
    val ageSegment: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("cus_image")
    val cusImage: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fcm")
    val fcm: String,
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("imei")
    val imei: String,
    @SerializedName("is_registered")
    val isRegistered: Boolean,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("msisdn")
    val msisdn: String,
    @SerializedName("national_id")
    val nationalId: String,
    @SerializedName("nid_image_back")
    val nidImageBack: String,
    @SerializedName("nid_image_front")
    val nidImageFront: String,
    @SerializedName("nominee_address")
    val nomineeAddress: String,
    @SerializedName("nominee_date_of_birth")
    val nomineeDateOfBirth: String,
    @SerializedName("nominee_district")
    val nomineeDistrict: String,
    @SerializedName("nominee_division")
    val nomineeDivision: String,
    @SerializedName("nominee_email")
    val nomineeEmail: String,
    @SerializedName("nominee_firstname")
    val nomineeFirstname: String,
    @SerializedName("nominee_image")
    val nomineeImage: String,
    @SerializedName("nominee_lastname")
    val nomineeLastname: String,
    @SerializedName("nominee_msisdn")
    val nomineeMsisdn: String,
    @SerializedName("nominee_nid_image_back")
    val nomineeNidImageBack: String,
    @SerializedName("nominee_nid_image_front")
    val nomineeNidImageFront: String,
    @SerializedName("nominee_nid_no")
    val nomineeNidNo: String,
    @SerializedName("nominee_postcode")
    val nomineePostcode: String,
    @SerializedName("nominee_profession")
    val nomineeProfession: String,
    @SerializedName("nominee_thana")
    val nomineeThana: String,
    @SerializedName("otp")
    val otp: Int,
    @SerializedName("otp_generate_time")
    val otpGenerateTime: String,
    @SerializedName("otp_used")
    val otpUsed: Boolean,
    @SerializedName("password")
    val password: String,
    @SerializedName("postcode")
    val postcode: String,
    @SerializedName("present_district")
    val presentDistrict: String,
    @SerializedName("present_division")
    val presentDivision: String,
    @SerializedName("present_thana")
    val presentThana: String,
    @SerializedName("profession")
    val profession: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("submit_date")
    val submitDate: String,
    @SerializedName("temp_password")
    val tempPassword: String,
    @SerializedName("temp_password_generate_time")
    val tempPasswordGenerateTime: String,
    @SerializedName("wrong_pin_attempt")
    val wrongPinAttempt: Int
)