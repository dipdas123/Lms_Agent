package tech.redltd.lmsAgent.activities.transaction_history

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_transaction_history.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.network.AspService
import tech.redltd.lmsAgent.utils.*
import tech.redltd.lmsCustomer.adapter.transactionHistoryAdapter.TransactionHistoryAdapter

class TransactionHistory : AppCompatActivity() {
    private val apiServiceForNew: AspService by inject()
    lateinit var dialog: AlertDialog
    lateinit var transactionHistoryRecycler: RecyclerView
    lateinit var transactionHistoryRecyclerAdapter: TransactionHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)
        dialog = loadingDialog()
        setHomeToolbar("Transaction History")

        transactionHistoryRecycler = findViewById(R.id.transactionHistoryRecycler)
        transactionHistoryRecyclerAdapter = TransactionHistoryAdapter()
        transactionHistoryRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        transactionHistoryRecycler.adapter = transactionHistoryRecyclerAdapter


        getTransactionReportOnClick.setOnClickListener {
            when {
                loanIDET.text.toString().isEmpty() -> {
                    errorToast("Please input Loan ID")
                }
                userMobileNoET.text.toString().isEmpty() -> {
                    errorToast("Please input customer MSISDN")
                }userMobileNoET.text.toString().length < 11 -> {
                    errorToast("Please input correct customer MSISDN")
                }
                else -> {
                    val loanIDNumber : String = loanIDET.text.toString()
                    val customerMSISDN : String = "88"+userMobileNoET.text.toString()

                    requestForTransactionHistory(loanIDNumber, customerMSISDN)
                }
            }
            }

    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun requestForTransactionHistory(loanIDNumber:String, customerMSISDN:String){
        dialog.show()
        val transactionHistoryResponseS = TransactionHistoryResponsePostCall(customerMSISDN, loanIDNumber)

        apiServiceForNew.getTransactionHistory(transactionHistoryResponseS).enqueue(object : Callback<TransactionHistoryResponse> {
            override fun onFailure(call: Call<TransactionHistoryResponse>, t: Throwable) {
                errorToast("error fetch api")
                dialog.hide()

            }

            override fun onResponse(call: Call<TransactionHistoryResponse>, response: retrofit2.Response<TransactionHistoryResponse>) {
                dialog.hide()
                if (response.isSuccessful){
                    val transactionHistoryResponse: TransactionHistoryResponse = response.body()!!
                    val transactionHistoryList:List<Txn_history> = transactionHistoryResponse.transaction_history.txn_history

                    if (transactionHistoryList.isNotEmpty()) {
                        transactionHistoryRecyclerAdapter.setTransaction(transactionHistoryList)
                        transactionHistoryRecycler.adapter = transactionHistoryRecyclerAdapter
                        transactionHistoryRecyclerAdapter.notifyDataSetChanged()
                    }else {
                        transactionHistoryRecycler.adapter = null
                        transactionHistoryRecyclerAdapter.notifyDataSetChanged()
                        successToast("No Transaction History Found")
                    }

                }else{
                    networkError(response.code())
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }
}