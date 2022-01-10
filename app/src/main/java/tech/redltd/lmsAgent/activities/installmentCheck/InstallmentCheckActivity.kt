package tech.redltd.lmsAgent.activities.installmentCheck

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_installment_check.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.bkashPaymentActivity.BkashPaymentActivity
import tech.redltd.lmsAgent.adapter.loanItemAdapter.LoanInstallmentAdapter
import tech.redltd.lmsAgent.utils.*

class InstallmentCheckActivity : AppCompatActivity() {
    lateinit var loanInstallmentRecycler: RecyclerView
    lateinit var loanInstallmentAdapter: LoanInstallmentAdapter
    private val TAG = InstallmentCheckActivity::class.java.simpleName
    private lateinit var dialog : Dialog
    var isFound = true
    private val appUtils: AppUtils by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installment_check)
        setHomeToolbar("Installment Check")
        dialog = loadingDialog()

        customerName.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)
        customerMsisdn.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)

        loanInstallmentAdapter = LoanInstallmentAdapter()
        val loanid = intent.getIntExtra("loanid",0)
        val nextpaymentamount = intent.getIntExtra("nextpaymentamount",0)
        val loanduration = intent.getIntExtra("loanduration",0)
        val loandate = intent.getStringExtra("loandate")!!
        val customerid = intent.getStringExtra("customerid")!!
        val loanamount = intent.getIntExtra("loanamount",0)
        totalLoanAmount.text = loanamount.toString()
        customerLoanId.text ="Loan Id: $loanid"
        CommonConstant.LOAN_Id = loanid.toString()

        customerMsisdn.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)


        loanInstallmentRecycler = findViewById(R.id.loanInstallmentRecycler)
        loanInstallmentRecycler.layoutManager = GridLayoutManager(this,3)
        loanInstallmentRecycler.adapter = loanInstallmentAdapter
        btnEmiPayBtn.setOnClickListener {
            CommonConstant.is_EmiPay = true
            val intent =Intent(this,BkashPaymentActivity::class.java)
            val amount:Int = upComingLoanAmount.text.toString().toInt()
            CommonConstant.downPaymentAmount = amount.toString()
            startActivity(intent)
        }
        fetchApi(loanid,nextpaymentamount, loanduration, loandate, customerid)
    }

    private fun fetchApi(loanid:Int,nextpaymentamount:Int,loanduration:Int,loandate:String,customerid:String){
        dialog.show()
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("loanid",loanid)
        json.put("nextpaymentamount",nextpaymentamount)
        json.put("loanduration",loanduration)
        json.put("loandate",loandate)
        json.put("customerid",customerid)
        val stringRequest = object: JsonObjectRequest(
            Method.POST, CommonUrl.loanDetails,json, Response.Listener { response ->
                dialog.hide()
                try {
                    Log.i(TAG,response.toString())
                    val gson = Gson()
                    val loanDetailsResponse : LoandetailsResponse = gson.fromJson(response.toString(),LoandetailsResponse::class.java)
                    val loanDetails: Loandetails = loanDetailsResponse.loandetails
                    val paymentDetails:PaymentDetails = loanDetails.paymentDetails

                    val emidetail:List<Emidetail> = paymentDetails.emidetails

                    emidetail.forEach { eidetic->
                        run {
                            if (!eidetic.isPaid){
                                if (isFound){
                                    upComingLoanAmount.text = eidetic.amount.toString()
                                    installmentAmount.text = eidetic.amount.toString()
                                    upComingEmiDate.text = eidetic.date
                                    isFound = false
                                    return@run
                                }

                            }
                        }
                    }

                    loanInstallmentAdapter.setEmiDetails(emidetail)
                    loanInstallmentAdapter.notifyDataSetChanged()

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }
}
