package tech.redltd.lmsAgent.activities.otpActivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import tech.redltd.lmsAgent.activities.HomeActivity
import tech.redltd.lmsAgent.activities.loginActivity.LoginOTPCheck
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.utils.*

class OtpActivity : AppCompatActivity() {
    private lateinit var otpView: OtpView
    private lateinit var verifyOtpBtn:Button
    private var otpString:String = ""
    private lateinit var dialog: AlertDialog
    private val apiService:ApiService by inject()
    private val appUtils :AppUtils by inject()

    var  mobile:String = ""
    var  password:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn)
        verifyOtpBtn.isClickable = false
        otpView = findViewById(R.id.otp_view)
        dialog = loadingDialog()
        try {
            mobile = intent.getStringExtra("mobile")!!
            password = intent.getStringExtra("password")!!
        }catch (ex:Exception){}


        otpView.setOtpCompletionListener {
            otpString = it
            verifyOtpBtn.isClickable = true
            if (appUtils.checkInternet()){
                postOtp(it,password,mobile)
            }else{
                errorToast("Sorry! Internet is not enable.")
            }


        }
    }

    fun verifyOtp(view: View) {
        view.requestFocus()
        if (appUtils.checkInternet()){
            postOtp(otpString,password,mobile)
        }else{
            errorToast("Sorry! Internet is not enable.")
        }

        //startActivity(Intent(this@OtpActivity,HomeActivity::class.java))
        //finish()
    }
    //startActivity(Intent(this,HomeActivity::class.java))
    private fun postOtp(otp:String,password:String,mobile:String){
        dialog.show()
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("mobile",mobile)
        json.put("password",password)
        json.put("otp",otp)
        val stringRequest = object: JsonObjectRequest(
            Method.POST, LmsUrl.loginOTPCheck, json, Response.Listener { response ->
                dialog.hide()
                try {
                    val gson = Gson()
                    val loginOTPCheck: LoginOTPCheck = gson.fromJson(response.toString(), LoginOTPCheck::class.java)

                    if (loginOTPCheck.ResponseCode == 100) {
                        successToast("OTP Matched")
                        appUtils.saveDataIntoPreference(CommonConstant.IS_OTP_VALID, "True")
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }else{
                        errorToast("OTP not Matched! Please try again.")
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

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }


}
