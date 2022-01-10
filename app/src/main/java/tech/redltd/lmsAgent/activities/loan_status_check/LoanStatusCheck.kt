package tech.redltd.lmsAgent.activities.loan_status_check

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_loan_status.*
import kotlinx.android.synthetic.main.activity_loan_status.loanStatusRecycler
import kotlinx.android.synthetic.main.loan_status_check.*
import kotlinx.android.synthetic.main.loan_status_check.customerName
import kotlinx.android.synthetic.main.loan_status_check.profileImage
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loanList.LoanListResponse
import tech.redltd.lmsAgent.adapter.loanStatusAdapter.LoanStatusAdapter
import tech.redltd.lmsAgent.utils.*

class LoanStatusCheck : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private val appUtils: AppUtils by inject()
    private lateinit var loanStatusAdapter : LoanStatusAdapter
    lateinit var customerMsisdnTV:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loan_status_check)
        customerMsisdnTV = findViewById(R.id.customerMsisdn)
        setHomeToolbar("Loan Status")
        dialog = loadingDialog()


        checkCreadit.setOnClickListener {
            if (userMobileNo.text.toString().isEmpty() || userMobileNo.text.toString().length < 11){
                errorToast("Please input a valid phone number!")
            }else {
                if (appUtils.checkInternet()) {
                    val cus_msisdn : String = "88"+userMobileNo.text.toString()
                    fetchApi(cus_msisdn)
                } else {
                    errorToast("Sorry! Internet is not enable.")
                }
            }
        }

        loanStatusAdapter = LoanStatusAdapter()
        loanStatusRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        loanStatusRecycler.adapter = loanStatusAdapter

        //fetchApi()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchApi(cusMsisdn: String) {
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("customerid",cusMsisdn)

        val stringRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(
            Method.POST, CommonUrl.loanList,json, Response.Listener { response ->
                dialog.hide()
                try {
                    val gson = Gson()
                    val loanlist: LoanListResponse = gson.fromJson(response.toString(), LoanListResponse::class.java)

                    if (loanlist.loanList.isEmpty()) {
                        loanStausLayout.visibility = View.INVISIBLE
                        errorToast("Currently you have no Loan!")
                    } else {
                        val loanID: String = loanlist.loanList[0].loanid.toString()
                        if (loanID != "") {
                            loanStausLayout.visibility = View.VISIBLE
                            customerLoanIdS.text = "Loan ID: $loanID"
                        } else {
                            customerLoanIdS.text = "No Response!"
                        }

                        customerName.text =
                            loanlist.loanList[0].firstName + loanlist.loanList[0].lastName
                        customerMsisdnTV.text = loanlist.loanList[0].customerid
                        val profilePic = loanlist.loanList[0].cusImage
                        Glide.with(this).load(profilePic).into(profileImage)


                        loanStatusAdapter.setLoanList(loanlist.loanList)
                        loanStatusAdapter.notifyDataSetChanged()

                    }
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    //errorToast("" + ex.message)
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
