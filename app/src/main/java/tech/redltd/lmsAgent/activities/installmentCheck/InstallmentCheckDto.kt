package tech.redltd.lmsAgent.activities.installmentCheck

import com.google.gson.annotations.SerializedName

data class LoandetailsResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("loandetails")
    val loandetails: Loandetails,
    @SerializedName("responseCode")
    val responseCode: Int,
    @SerializedName("responseMessage")
    val responseMessage: String
)

data class Loandetails(
    @SerializedName("customerid")
    val customerid: String,
    @SerializedName("loanId")
    val loanId: Int,
    @SerializedName("loanduration")
    val loanduration: Int,
    @SerializedName("paymentDetails")
    val paymentDetails: PaymentDetails
)

data class PaymentDetails(
    @SerializedName("downpaywmnt")
    val downpaywmnt: Int,
    @SerializedName("emidetails")
    val emidetails: List<Emidetail>
)

data class Emidetail(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("isPaid")
    val isPaid: Boolean
)