package tech.redltd.lmsAgent.network.aspDto

import com.google.gson.annotations.SerializedName

data class LoanSubmissionResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("isStatus")
    val isStatus: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("loanid")
    val loanid: Int
)

data class LoanSubmissionBody(
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
    @SerializedName("customerMsisdn_decrypted")
    val customerMsisdnDecrypted: String,
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
    val nomNidNumber: Long,
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