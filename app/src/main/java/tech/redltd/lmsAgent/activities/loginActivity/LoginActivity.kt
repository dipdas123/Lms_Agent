package tech.redltd.lmsAgent.activities.loginActivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.createAccount.CreateAccountActivity
import tech.redltd.lmsAgent.activities.otpActivity.OtpActivity
import tech.redltd.lmsAgent.utils.*


class LoginActivity : AppCompatActivity() {
    lateinit var userPhone : EditText
    lateinit var userPassword :EditText
    lateinit var buttonLogin:Button
    lateinit var dialog: AlertDialog
    private val appUtils : AppUtils by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userPhone = findViewById(R.id.userMobileNo)
        userPassword = findViewById(R.id.userPin)
        buttonLogin = findViewById(R.id.buttonLogin)
        dialog = loadingDialog()

        userPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        buttonLogin.setOnClickListener {
            when {
                userPhone.text.toString().isEmpty() -> {
                    userPhone.requestFocus()
                    userPhone.error = "Please enter your phone Number!"
                }
                userPhone.text.toString().length < 11 -> {
                    userPhone.error = "Please enter your phone number correctly!"
                }
                userPassword.text.toString().isEmpty() -> {
                    userPassword.requestFocus()
                    userPassword.error = "Please enter your password!"
                }
                else -> {
                    val phoneNumber = "88" + userPhone.text.toString()
                    val password = userPassword.text.toString()
                    CommonConstant.phoneNumber = phoneNumber

                    if (appUtils.checkInternet()) {
                        fetchLogin(userPhone.text.toString(), password)
                    } else {
                        errorToast("Sorry! Internet is not enable.")
                    }
                }
            }
        }


    }

    fun startCreateAccount(view: View) {
        view.requestFocus()
        startActivity(Intent(this,CreateAccountActivity::class.java))
    }

    private fun fetchLogin(phoneNumber:String,password:String){
        dialog.show()
        appUtils.saveDataIntoPreference(CommonConstant.AGENT_PASS,password)
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("mobile",phoneNumber)
        json.put("usrpassword",password)
        json.put("fcm","a843496254")
        json.put("imei_no","352205106364242")
        val stringRequest = object: JsonObjectRequest(
            Method.POST, LmsUrl.agent_Login,json, Response.Listener { response ->
            dialog.hide()
            try {
                val gson = Gson()
                val agentLoginResponse:AgentLoginResponse = gson.fromJson(response.toString(),AgentLoginResponse::class.java)

                if (agentLoginResponse.isSuccess){
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_ID,agentLoginResponse.agentInfo.agentID.toString())
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_EMAIL,agentLoginResponse.agentInfo.email)
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_NUMBER,phoneNumber)
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_GROUP,agentLoginResponse.agentInfo.agentgroup)
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_FIRSTNAME,agentLoginResponse.agentInfo.firstname)
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_LASTNAME,agentLoginResponse.agentInfo.lastname)
                    appUtils.saveDataIntoPreference(CommonConstant.AGENT_MOBILE_NO,agentLoginResponse.agentInfo.mobile)
                    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdG5hbWUiOiJjdXN0b21lcjEiLCJsYXN0bmFtZSI6Imxhc3QgTmFtZSIsImVtYWlsIjoiY3VzdG9tZXJAZ21haWwuY29tIiwibXNpc2RuIjoiODgwMTg0NDUyNTEyMiIsImFnZW50X2lkIjoyMSwiYWdlbnRfZW1haWwiOiJjdXN0b21lci5sbXMucm9iaXNob3BAcm9iaS5jb20uYmQiLCJhZ2VudF9wYXNzd29yZCI6IjEyMzQ1IiwibG9naW5fZGF0ZXRpbWUiOiIyMDIwLTA2LTI4VDA1OjQ3OjIwLjQ4N1oiLCJpYXQiOjE1OTMzMjMyNDB9.ITxm6TwJmmYxqEo-BghKVtIMmMOxz4rZ65mYzPjZ_Sw"
                    appUtils.saveDataIntoPreference(CommonConstant.TOKEN_BEARER, token)

                    val intent = Intent(this,OtpActivity::class.java)
                    intent.putExtra("mobile",phoneNumber)
                    intent.putExtra("password",password)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                }else{
                    errorToast("Incorrect phone number or password!")
                }

            }catch (ex:Exception){
                ex.printStackTrace()
            }

        },
            Response.ErrorListener { error ->
                run {
                    Log.d("ErrorLOG", "Response is: " + error.message)
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
