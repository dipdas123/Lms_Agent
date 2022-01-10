package tech.redltd.lmsAgent.activities.checkEligibility

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_check_eligibility.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.productDetailsActivity.ProductDetailsActivity
import tech.redltd.lmsAgent.adapter.productAdapter.ProductOnClick
import tech.redltd.lmsAgent.adapter.productAdapter.ProductRecyclerAdapter
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.network.aspDto.LoanQuery
import tech.redltd.lmsAgent.network.aspDto.LoanQueryResponse
import tech.redltd.lmsAgent.network.lmsDto.RobiShopPorductRequest
import tech.redltd.lmsAgent.network.lmsDto.RobiShopProductResponse
import tech.redltd.lmsAgent.utils.*


class CheckEligibilityActivity : AppCompatActivity(),ProductOnClick {

    private val apiService : ApiService by inject()
    private val appUtils : AppUtils by inject()

    lateinit var rl6month : RelativeLayout
    lateinit var rl12month : RelativeLayout

    //6 month Text view
    lateinit var month6installment : TextView
    lateinit var month6Amount : TextView
    lateinit var month6downpayment : TextView

    //12 month text view
    lateinit var month12installment : TextView
    lateinit var month12Amount : TextView
    lateinit var month12downpayment : TextView

    lateinit var dialog: AlertDialog

    lateinit var productRecyclerAdapter: ProductRecyclerAdapter

    private var loan_duration_month:Int=0
    private var dmrp = 0

    private var loanDurationIMonth = 0
    private var loanAmount = 0
    private var loanInstallment = 0
    private var loanDownPayment = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_eligibility)
        setHomeToolbar("Check Eligibility")
        rl6month = findViewById(R.id.rl6month)
        rl12month = findViewById(R.id.rl12month)
        dialog = loadingDialog()
        month6installment = findViewById(R.id.txtinstallment6)
        month6Amount = findViewById(R.id.txtamount6)
        month6downpayment = findViewById(R.id.txtdownpayment6)

        try {
            loanDurationIMonth = intent.getIntExtra("loanDurationIMonth",0)
            loanAmount = intent.getIntExtra("loanAmount",0)
            loanInstallment = intent.getIntExtra("loanInstallment",0)
            loanDownPayment = intent.getIntExtra("loanDownPayment",0)

            txtmonth6.text = " $loanDurationIMonth Month "
            txtinstallment6MonthEx.text = " $loanDurationIMonth"//DIP
            month6installment.text = loanInstallment.toString()
            txtinstallment6Month.text = "Tk $loanInstallment"  //DIP
            month6Amount.text = loanAmount.toString()
            txtamount6Month.text = "Tk $loanAmount"  //DIP
            month6downpayment.text = loanDownPayment.toString()
            txtdownpayment6month.text = "Tk $loanDownPayment"  //DIP
            fetchProductByPriceRange(loanAmount.toString(),"0")
        }catch (ex:Exception){

        }



        //init()
        //val text ="<font color=#27C0A2>Congratulation!</font> <font color=#bbbcbcbc>You can apply for Loan</font>"
        //can_Apply_LoanTv.text = HtmlCompat.fromHtml(text,HtmlCompat.FROM_HTML_MODE_LEGACY)
            //product recycler




        productRecyclerAdapter = ProductRecyclerAdapter()
        productRecyclerAdapter.setProductOnClick(this)
        handsetRecycler.layoutManager =LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        handsetRecycler.adapter = productRecyclerAdapter

        rl6month.setOnClickListener {
             val maxPrice = month6Amount.text.toString()

            loan_duration_month = loanDurationIMonth
            dmrp =  month6Amount.text.toString().toInt()

        }

        rl12month.setOnClickListener {
            val maxPrice = month12Amount.text.toString()
            fetchProductByPriceRange(maxPrice,"0")
            loan_duration_month = 12
            dmrp = month12Amount.text.toString().toInt()
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onProductClick(sku: String) {
        //Toasty.success(this, sku).show()
        val intent = Intent(this,ProductDetailsActivity::class.java)
        val amount : Int = month6Amount.text.toString().toInt()
        intent.putExtra("productSKU",sku)
        intent.putExtra("loan_duration",loanDurationIMonth)
        intent.putExtra("dmrp",amount)
        startActivity(intent)

    }

    private fun init(){
        //6 month
        month6installment = findViewById(R.id.txtinstallment6)
        month6Amount = findViewById(R.id.txtamount6)
        month6downpayment = findViewById(R.id.txtdownpayment6)

        //12 month

        month12installment = findViewById(R.id.txtinstallment12)
        month12Amount = findViewById(R.id.txtamount12)
        month12downpayment = findViewById(R.id.txtdownpayment12)

        //vollyRequest()
    }


    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }




    private fun vollyRequest(){
        dialog.show()
        val agentId:String = appUtils.getDataFromPreference(CommonConstant.AGENT_ID)!!
        val agent_password:String = appUtils.getDataFromPreference(CommonConstant.AGENT_PASS)!!
        val cus_msisdn:String = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("Agent_id", agentId)
        json.put("ag_password", agent_password)
        json.put("cus_msisdn", cus_msisdn)
        val stringRequest = object: JsonObjectRequest(Request.Method.POST, CommonUrl.LOAN_QUERY,json,Response.Listener { response ->
            dialog.hide()
            try {
                val gson = Gson()
                val loanQueryResponse:LoanQueryResponse = gson.fromJson(response.toString(),LoanQueryResponse::class.java)
                val loanQuery: LoanQuery = loanQueryResponse.loanQuery
                if(loanQuery != null){
                    // 6 month
                    month6installment.text = loanQuery.month6Installment
                    month6Amount.text = loanQuery.month6
                    month6downpayment.text = loanQuery.month6DownPayment

                    // 12 month
                    month12installment.text = loanQuery.month12Installment
                    month12Amount.text = loanQuery.month12
                    month12downpayment.text = loanQuery.month12DownPayment
                }else{
                    AlertDialog.Builder(this)
                        .setMessage(loanQueryResponse.status)
                        .setPositiveButton(
                            android.R.string.yes
                        ) { dialog, which ->
                            // Continue with delete operation
                            onBackPressed()
                        } // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()

                    loanInfoLinear.visibility = View.GONE
                }


                can_Apply_LoanTv.text = loanQueryResponse.status
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

    private fun fetchProductByPriceRange(maxPrice:String,minPrice:String){
        dialog.show()
        val robiShopProductRequest = RobiShopPorductRequest(maxPrice,minPrice)
        apiService.robiShopProducts(robiShopProductRequest).enqueue(object :
            Callback<RobiShopProductResponse> {
            override fun onFailure(call: Call<RobiShopProductResponse>, t: Throwable) {
                errorToast("error fetch api")
                dialog.hide()

            }

            override fun onResponse(call: Call<RobiShopProductResponse>, response: retrofit2.Response<RobiShopProductResponse>) {
                dialog.hide()
                if (response.isSuccessful){
                    val robiShopProductResponse: RobiShopProductResponse = response.body()!!
                    val products = robiShopProductResponse.payload.products
                    productRecyclerAdapter.setProducts(products)
                    productRecyclerAdapter.notifyDataSetChanged()
                }else{
                    networkError(response.code())
                }
            }
        })
    }
}
