package tech.redltd.lmsAgent.activities.loanStatus

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_loan_status.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loanList.LoanListResponse
import tech.redltd.lmsAgent.adapter.loanStatusAdapter.LoanStatusAdapter
import tech.redltd.lmsAgent.utils.*

class LoanStatusActivity : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private val appUtils: AppUtils by inject()
    private lateinit var loanStatusAdapter : LoanStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_status)
        setHomeToolbar("Loan Status")
        dialog = loadingDialog()

        customerName.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)
        customerMsisdn.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)
        val profilePic = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_IMAGE)!!
        Glide.with(this)
            .load(profilePic)
            .into(profileImage)


        loanStatusAdapter = LoanStatusAdapter()
        loanStatusRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        loanStatusRecycler.adapter = loanStatusAdapter

        fetchApi()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchApi(){
        val customerid =appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("customerid",customerid)
        val stringRequest = object: JsonObjectRequest(
            Method.POST, CommonUrl.loanList,json, Response.Listener { response ->
                dialog.hide()
                try {
                    val gson = Gson()
                    val loanlist: LoanListResponse = gson.fromJson(response.toString(),
                        LoanListResponse::class.java)
                    loanStatusAdapter.setLoanList(loanlist.loanList)
                    loanStatusAdapter.notifyDataSetChanged()

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
