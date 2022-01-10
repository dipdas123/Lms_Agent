package tech.redltd.lmsAgent.activities.loanList

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
import kotlinx.android.synthetic.main.activity_loan_list.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.adapter.loanListItem.LoanListItemAdapter
import tech.redltd.lmsAgent.utils.*

class LoanListActivity : AppCompatActivity() {
    private lateinit var dialog:Dialog
    private val appUtils:AppUtils by inject()
    private lateinit var loanListItemAdapter: LoanListItemAdapter
    private val TAG = LoanListActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_list)
        setHomeToolbar("Loan List")
        dialog = loadingDialog()

        customerName.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)
        customerMsisdn.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)
        val profilePic = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_IMAGE)!!
        Glide.with(this)
            .load(profilePic)
            .into(profileImage)


        loanListItemAdapter = LoanListItemAdapter()
        loanListRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        loanListRecycler.adapter = loanListItemAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

    override fun onResume() {
        super.onResume()
        fetchApi()
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
                    Log.i(TAG,response.toString())
                    val gson = Gson()
                    val loanlist: LoanListResponse = gson.fromJson(response.toString(),LoanListResponse::class.java)
                    loanListItemAdapter.setLoanList(loanlist.loanList)
                    loanListItemAdapter.notifyDataSetChanged()

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
