package tech.redltd.lmsAgent.activities.productDetailsActivity

import com.google.gson.annotations.SerializedName

data class CheckCredentialResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("loanCredential")
    val loanCredential: LoanCredential
)

data class LoanCredential(
    @SerializedName("deviceamount")
    val deviceamount: Int,
    @SerializedName("downpayment")
    val downpayment: Int,
    @SerializedName("emi")
    val emi: Int,
    @SerializedName("loanamount")
    val loanamount: Int,
    @SerializedName("remainingbalance")
    val remainingbalance: Int
)