package tech.redltd.lmsAgent.activities.loanConfirmOtp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.mukesh.OtpView
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.bkashPaymentActivity.BkashPaymentActivity
import tech.redltd.lmsAgent.activities.changePasswordActivity.LoanConfirmOTPResponse
import tech.redltd.lmsAgent.activities.loanSuccessActivity.LoanSuccessActivity
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.utils.*

class LoanConfirmOtpActivity : AppCompatActivity() {
    private lateinit var otpView: OtpView
    private lateinit var verifyOtpBtn: Button
    private var otpString:String = ""
    private lateinit var dialog: AlertDialog
    private val apiService: ApiService by inject()
    private val appUtils : AppUtils by inject()
    var  agentMobile:String = ""
    var  agentPassword:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_confirm_otp)

        verifyOtpBtn = findViewById(R.id.verifyOtpBtn)
        verifyOtpBtn.isClickable = false
        otpView = findViewById(R.id.otp_view)
        dialog = loadingDialog()

        try {

            agentMobile = appUtils.getDataFromPreference(CommonConstant.AGENT_NUMBER)!!
            agentPassword = appUtils.getDataFromPreference(CommonConstant.AGENT_PASS)!!
            requestOtp(agentMobile,agentPassword)

        }catch (ex:Exception){}


        otpView.setOtpCompletionListener {
            otpString = it
            verifyOtpBtn.isClickable = true
            postOtp(it,CommonConstant.CUSTOMER_NUMBER)

        }
    }

    private fun postOtp(otp:String, mobile:String) {
        dialog.show()
        val customerId = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!
        val sku: String = appUtils.getDataFromPreference(CommonConstant.PHONE_SKU)!!
        val phoneQtyString: String = appUtils.getDataFromPreference(CommonConstant.PHONE_QUANTITY)!!
        val phoneQtyInt: Int = phoneQtyString.toInt()

        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("mobile", customerId)
        json.put("loanOTP", otp)
        json.put("sku", sku)
        json.put("robishopqty", phoneQtyInt)
        val stringRequest = object: JsonObjectRequest(
            Method.POST, LmsUrl.agent_otpCheck,json, Response.Listener { response ->
                dialog.hide()
                try {
                    val gson = Gson()
                    val loanConfirmOTPCheck: LoanConfirmOTPResponse = gson.fromJson(response.toString(), LoanConfirmOTPResponse::class.java)
                    val agentType = appUtils.getDataFromPreference(CommonConstant.AGENT_GROUP)!!

                    if (loanConfirmOTPCheck.isSuccess) {
                        successToast(""+loanConfirmOTPCheck.isstatus)

                        if (agentType.contains(CommonConstant.RStore_AGENT)) {
                            val intent = Intent(this, BkashPaymentActivity::class.java)
                            startActivity(intent)
                        }
                        if (agentType.contains(CommonConstant.WIC_AGENT)) {
                            val intent = Intent(this, LoanSuccessActivity::class.java)
                            startActivity(intent)
                        }

                    }else{
                        successToast(""+loanConfirmOTPCheck.isstatus)
                    }

                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            },
            Response.ErrorListener { error ->
                run {
                    Log.d("Error", "Response is: " + error.message)
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

    private fun requestOtp(agentMobile:String,agentPassword:String){
        dialog.show()
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        val customerId = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!
        json.put("mobile",agentMobile)
        json.put("agentpassword",agentPassword)
        json.put("customerid",customerId)

        val stringRequest = object: JsonObjectRequest(
            Method.POST, LmsUrl.aget_otpRequest,json, Response.Listener { response ->
                dialog.hide()
                try {
                    val gson = Gson()

                   /* val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)*/
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            },
            Response.ErrorListener { error ->
                run {
                    Log.d("Error", "Response is: " + error.message)
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
