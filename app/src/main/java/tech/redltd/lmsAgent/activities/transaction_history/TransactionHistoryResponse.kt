package tech.redltd.lmsAgent.activities.transaction_history

import com.google.gson.annotations.SerializedName



data class TransactionHistoryResponsePostCall (val cus_msisdn:String,val loanid:String)

data class TransactionHistoryResponse (

    @SerializedName("transaction_history")
        val transaction_history : Transaction_history,
    @SerializedName("apiVersion")
        val apiVersion : Double
    )

data class Transaction_history (

    @SerializedName("loanid")
    val loanid : Int,
    @SerializedName("txn_history")
    val txn_history : List<Txn_history>,
    @SerializedName("responseCode")
    val responseCode : Int,
    @SerializedName("responseMessage")
    val responseMessage : String
)

data class Txn_history (

    @SerializedName("transaction_id")
    val transaction_id : String,
    @SerializedName("payment_date")
    val payment_date : String,
    @SerializedName("payment_amount")
    val payment_amount : Int,
    @SerializedName("transection_type")
    val transection_type : String,
    @SerializedName("transaction_status")
    val transaction_status : String
)