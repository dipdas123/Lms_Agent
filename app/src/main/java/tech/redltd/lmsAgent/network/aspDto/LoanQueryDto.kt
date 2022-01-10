package tech.redltd.lmsAgent.network.aspDto

import com.google.gson.annotations.SerializedName

data class LoanQueryRequest(val Agent_id:Int,val cus_msisdn:String,val ag_password:String)

data class LoanQueryResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("loanQuery")
    val loanQuery: LoanQuery,
    @SerializedName("status")
    val status: String
)

data class LoanQuery(
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("month_12")
    val month12: String,
    @SerializedName("month_12_DownPayment")
    val month12DownPayment: String,
    @SerializedName("month_12_Installment")
    val month12Installment: String,
    @SerializedName("month_24")
    val month24: String,
    @SerializedName("month_24_DownPayment")
    val month24DownPayment: String,
    @SerializedName("month_24_Installment")
    val month24Installment: String,
    @SerializedName("month_6")
    val month6: String,
    @SerializedName("month_6_DownPayment")
    val month6DownPayment: String,
    @SerializedName("month_6_Installment")
    val month6Installment: String,
    @SerializedName("msisdn")
    val msisdn: String
)