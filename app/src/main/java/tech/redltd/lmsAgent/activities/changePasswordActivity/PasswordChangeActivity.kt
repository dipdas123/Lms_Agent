package tech.redltd.lmsAgent.activities.changePasswordActivity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_password_change.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loginActivity.LoginActivity
import tech.redltd.lmsAgent.network.AgentPasswordUpdateResponse
import tech.redltd.lmsAgent.utils.*

class PasswordChangeActivity : AppCompatActivity() {

    private val appUtils:AppUtils by inject()

    private lateinit var loading:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)
        setHomeToolbar("Change Password")
        loading = loadingDialog()
        loading.setCancelable(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun changePassword(view: View) {
        view.requestFocus()
        val oldPassword = oldPass.text.toString()
        val newPassword = newPass.text.toString()
        val mobileNumber = phoneNumber.text.toString()
        when {
            mobileNumber.isEmpty() -> {
                newPass.error = "Phone Number is Required"
                newPass.requestFocus()
                return
            }
            oldPassword.isEmpty() -> {
                oldPass.error = "old Password is Required"
                oldPass.requestFocus()
                return
            }
            newPassword.isEmpty() -> {
                newPass.error = "New Password is Required"
                newPass.requestFocus()
                return
            }
            else -> {
                if (appUtils.checkInternet()){
                    fetchApi(oldPassword,newPassword,mobileNumber)
                }else{
                    errorToast("Make Sure Your phone Have internet connection")
                }

            }
        }

    }

    private fun fetchApi(oldpas:String,newPass:String,mobileNumber:String){
       loading.show()
        val jsonObject = JSONObject()
        jsonObject.put("password",oldpas)
        jsonObject.put("newpassword",newPass)
        jsonObject.put("mobile",mobileNumber)

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object: JsonObjectRequest(
            Method.POST, LmsUrl.aget_updatePassword,jsonObject,
            com.android.volley.Response.Listener { response ->
                loading.hide()
                try {
                    val gson = Gson()
                    val agentPasswordUpdateResponse : AgentPasswordUpdateResponse = gson.fromJson(response.toString(),AgentPasswordUpdateResponse::class.java)
                    if (agentPasswordUpdateResponse.isSuccess){
                        AlertDialog.Builder(this)
                            .setMessage(agentPasswordUpdateResponse.isstatus)
                            .setCancelable(false)
                            .setPositiveButton(
                                "Ok"
                            ) { dialogInterface, i ->
                                appUtils.saveDataIntoPreference(CommonConstant.CUSTOMER_MSISDN,"")
                                appUtils.saveDataIntoPreference(CommonConstant.TOKEN,"")
                                appUtils.saveDataIntoPreference(CommonConstant.AGENT_ID,"")
                                appUtils.saveDataIntoPreference(CommonConstant.AGENT_PASS,"")
                                appUtils.saveDataIntoPreference(CommonConstant.AGENT_EMAIL,"")
                                appUtils.saveDataIntoPreference(CommonConstant.CUSTOMER_NAME,"")
                                appUtils.saveDataIntoPreference(CommonConstant.IS_OTP_VALID,"")
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            .create()
                            .show()

                    }

                }catch (ex:Exception){
                    ex.printStackTrace()

                }

            },
            com.android.volley.Response.ErrorListener { error ->
                run {
                    Log.d("Error", "Response is: " + error.message)
                    loading.hide()
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
        loading.dismiss()
    }
}
