package tech.redltd.lmsAgent.activities.loanSuccessActivity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_loan_success.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.HomeActivity
import tech.redltd.lmsAgent.activities.applyLoan.ApplyLoanResponse
import tech.redltd.lmsAgent.utils.*

class LoanSuccessActivity : AppCompatActivity() {

    private val appUtils : AppUtils by inject()
    private lateinit var progressDialog:Dialog
    private var transactionId=""
    private var pAID_AMOUNT=""
    private var PaymentID=""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_success)
            progressDialog = loadingDialog()
        try {
            val agentType = appUtils.getDataFromPreference(CommonConstant.AGENT_GROUP)!!

            if (agentType.contains(CommonConstant.RStore_AGENT)){
                transactionId = intent.getStringExtra("TRANSACTION_ID")!!
                pAID_AMOUNT = intent.getStringExtra("PAID_AMOUNT")!!
                PaymentID = intent.getStringExtra("PaymentID")!!
                applyForLoan(transactionId,PaymentID)

                val downPaymentAmount:String = ""+appUtils.getDataFromPreference(CommonConstant.DownPaymentAmount)
                loanDownPayment.text = "Down Payment: Tk $downPaymentAmount"
                transactionIdTv.text = "Transaction ID: $transactionId"
                val total_loan_amt:String = ""+appUtils.getDataFromPreference(CommonConstant.TOTAL_LOAN_AMOUNT)
                loanAmount.text = "Loan Amount: $total_loan_amt"
            }
            if (agentType.contains(CommonConstant.WIC_AGENT)){
                val downPaymentAmount:String = ""+appUtils.getDataFromPreference(CommonConstant.DownPaymentAmount)
                loanDownPayment.text = "Down Payment: Tk $downPaymentAmount"
                val total_loan_amt:String = ""+appUtils.getDataFromPreference(CommonConstant.TOTAL_LOAN_AMOUNT)
                loanAmount.text = "Loan Amount: $total_loan_amt"
                applyForLoan("WIC AGENT", PaymentID)
            }

            val profilePic = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_IMAGE)!!
            val userName = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)!!
            userPhoneNumber.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!
            profileName.text = userName
            Glide.with(this).load(profilePic).into(profileImage)

        }catch (ex:Exception){

        }
    }

    fun okBtnClick(view: View) {
        view.requestFocus()
        val intent = Intent(this,HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun  getApplyForLoanRequest(): JSONObject {
        val jsonObjectString = appUtils.getDataFromPreference("ApplyLoan")
        val parser = JsonParser()
        val gson : JsonObject = parser.parse(jsonObjectString).asJsonObject
        val jsonObject =  JSONObject(gson.toString())
        return jsonObject
    }

    private fun applyForLoan(transectionId: String, paymentID: String){
        progressDialog.show()
        val applyForLoanRequest  = getApplyForLoanRequest()
        applyForLoanRequest.put("transectionId",transectionId)
        applyForLoanRequest.put("paymentID",paymentID)

        val queue = Volley.newRequestQueue(this)
        val stringRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(
            Method.POST, CommonUrl.loanSubmit,applyForLoanRequest, Response.Listener { response ->
                progressDialog.hide()
                try {
                    val gson = Gson()
                    val applyLoanResponse : ApplyLoanResponse = gson.fromJson(response.toString(), ApplyLoanResponse::class.java)
                    successToast(applyLoanResponse.isStatus)
                    loanIdTv.text = "Loan Id: ${applyLoanResponse.loanid}"

                }catch (ex:Exception){
                    ex.printStackTrace()

                }

            },
            Response.ErrorListener { error ->
                progressDialog.hide()
                run {
                    Log.d("Error", "Response is: " + error.message)
                    if (error is TimeoutError){
                        errorToast("Request Time Out")
                    }

                }
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic MTg1NDc4NDUxMjpGYTEyMzQ1Njc4OQ=="
                headers["x-Auth-Token"] = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZ2VudElEIejoyLCJtb2JpbGUiOiIxOTEyNjEwODk5IiwiZmlyc3RuYW1lIjoiU2hhcmlmdWwiLCJvdHAiOjMwMTk1OCwiaWF0IjoxNTcwODU5NTYwfQ.k8ICcAyzAOqlbmgW0N-kr8lUMwcMLDOnTIFEbku0PAs"
                headers["Module"] = "c2hhcmlmdWw="
                return headers
            }
        }

        val defaultRetryPolicy = DefaultRetryPolicy(100000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        stringRequest.retryPolicy = defaultRetryPolicy

        queue.add(stringRequest)
    }


}
