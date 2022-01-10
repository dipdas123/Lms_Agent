package tech.redltd.lmsAgent.network.aspDto

import com.google.gson.annotations.SerializedName

data class LoanCredentialCheckRequest(val Loan_duration_month:Int,val dmrp:Int)


data class LoanCredentialResponse(
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