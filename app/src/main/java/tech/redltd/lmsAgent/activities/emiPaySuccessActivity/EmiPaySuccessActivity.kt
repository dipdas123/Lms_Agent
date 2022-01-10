package tech.redltd.lmsAgent.activities.emiPaySuccessActivity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_emi_pay_success.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.HomeActivity
import tech.redltd.lmsAgent.utils.*

class EmiPaySuccessActivity : AppCompatActivity() {
    lateinit var dialog : Dialog
    private val appUtils : AppUtils by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emi_pay_success)
        dialog = loadingDialog()
        customerName.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)
        customerMsisdn.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)
        emiSuccessOk.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
        val customerMSISDN = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)
        val agentId = appUtils.getDataFromPreference(CommonConstant.AGENT_ID)
        val TRANSACTION_ID = intent.getStringExtra("TRANSACTION_ID")!!
        val PAID_AMOUNT = intent.getStringExtra("PAID_AMOUNT")!!
        val paymentID = intent.getStringExtra("PaymentID")!!
        val loanId = CommonConstant.LOAN_Id
        fetchApi(customerMSISDN,TRANSACTION_ID,paymentID,PAID_AMOUNT,agentId,loanId)
    }

    private fun fetchApi(customerMSISDN: String?, transactionId: String, paymentID: String, paidAmount: String, agentId: String?, loanId: String) {
        dialog.show()
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("customerMsisdn",customerMSISDN)
        json.put("emiAmount",paidAmount)
        json.put("agentId",agentId)
        json.put("loanId",loanId)
        json.put("tranactionId",transactionId)
        json.put("paymentID",paymentID)
        json.put("tranactionType","BKash")

        val stringRequest = object: JsonObjectRequest(
            Method.POST, CommonUrl.EmiSubmissionSet,json, Response.Listener { response ->
                dialog.hide()
                try {
                    Log.i("EMISUCCESS",response.toString())
                    val gson = Gson()

                }catch (ex:Exception){
                    ex.printStackTrace()
                    errorToast(""+ex.message)
                }

            },
            Response.ErrorListener { error ->
                run {
                    Log.d("Error", "Response is: " + error.message)
                    errorToast(""+error.message)
                    dialog.hide()
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

        queue.add(stringRequest)
    }
}
