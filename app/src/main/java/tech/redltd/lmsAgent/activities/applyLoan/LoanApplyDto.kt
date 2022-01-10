package tech.redltd.lmsAgent.activities.applyLoan

import com.google.gson.annotations.SerializedName


data class ApplyLoanResponse(
    val loanid: String,
    val isSuccess: Boolean,
    val apiVersion: String,
    val isStatus: String
)




data class ApplyForLoanRequest(
    @SerializedName("address")
    val address: String,
    @SerializedName("agentEmail")
    val agentEmail: String,
    @SerializedName("agentId")
    val agentId: Int,
    @SerializedName("agentPassword")
    val agentPassword: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("cusImage")
    val cusImage: String,
    @SerializedName("cusNidImageBack")
    val cusNidImageBack: String,
    @SerializedName("cusNidImageFront")
    val cusNidImageFront: String,
    @SerializedName("cusNidNumber")
    val cusNidNumber: Long,
    @SerializedName("customerMsisdn")
    val customerMsisdn: String,
    @SerializedName("deliveryAddress")
    val deliveryAddress: String,
    @SerializedName("deliveryCity")
    val deliveryCity: String,
    @SerializedName("deliveryDistrict")
    val deliveryDistrict: String,
    @SerializedName("deliveryThana")
    val deliveryThana: String,
    @SerializedName("deliveryZipCode")
    val deliveryZipCode: Int,
    @SerializedName("devicePrice")
    val devicePrice: Double,
    @SerializedName("district")
    val district: String,
    @SerializedName("downPayment")
    val downPayment: Double,
    @SerializedName("emi")
    val emi: Double,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("isAddressIsDeliveryAddress")
    val isAddressIsDeliveryAddress: Boolean,
    @SerializedName("isVerificationRequired")
    val isVerificationRequired: Boolean,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("loanAmount")
    val loanAmount: Double,
    @SerializedName("loanDurationMonth")
    val loanDurationMonth: Int,
    @SerializedName("nomFirstName")
    val nomFirstName: String,
    @SerializedName("nomLastName")
    val nomLastName: String,
    @SerializedName("nomMsisdn")
    val nomMsisdn: String,
    @SerializedName("nomNidImageBack")
    val nomNidImageBack: String,
    @SerializedName("nomNidImageFront")
    val nomNidImageFront: String,
    @SerializedName("nomNidNumber")
    val nomNidNumber: Int,
    @SerializedName("permanentAddress")
    val permanentAddress: String,
    @SerializedName("permanentCity")
    val permanentCity: String,
    @SerializedName("permanentDistrict")
    val permanentDistrict: String,
    @SerializedName("permanentPostcode")
    val permanentPostcode: Int,
    @SerializedName("permanentThana")
    val permanentThana: String,
    @SerializedName("postcode")
    val postcode: Int,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("thana")
    val thana: String,
    @SerializedName("transectionId")
    val transectionId: String,
    @SerializedName("transectionType")
    val transectionType: String
)