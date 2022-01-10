package tech.redltd.lmsAgent.activities.loanList

import com.google.gson.annotations.SerializedName

data class LoanListResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("loanList")
    val loanList: List<Loan>
)


data class Loan(
    @SerializedName("cus_image")
    val cusImage: String,
    @SerializedName("customerid")
    val customerid: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("dmrp")
    val dmrp: Int,
    @SerializedName("downpayment")
    val downpayment: Int,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("interestrate")
    val interestrate: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("loanamount")
    val loanamount: Int,
    @SerializedName("loandate")
    val loandate: String,
    @SerializedName("loanduration")
    val loanduration: Int,
    @SerializedName("loanid")
    val loanid: Int,
    @SerializedName("loanstatus")
    val loanstatus: String,
    @SerializedName("loanstatus_id")
    val loanstatusId: Int,
    @SerializedName("nextduedate")
    val nextduedate: String,
    @SerializedName("nextpaymentamount")
    val nextpaymentamount: Int,
    @SerializedName("paymentcount")
    val paymentcount: Int,
    @SerializedName("pendingpaymentcount")
    val pendingpaymentcount: Int,
    @SerializedName("remainingbalance")
    val remainingbalance: Int
)