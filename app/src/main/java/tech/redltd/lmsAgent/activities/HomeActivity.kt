package tech.redltd.lmsAgent.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.changePasswordActivity.PasswordChangeActivity
import tech.redltd.lmsAgent.activities.checkEligibility.CheckEligibilityActivity
import tech.redltd.lmsAgent.activities.loanList.LoanListActivity
import tech.redltd.lmsAgent.activities.loanStatus.LoanStatusActivity
import tech.redltd.lmsAgent.activities.loan_status_check.LoanStatusCheck
import tech.redltd.lmsAgent.activities.loginActivity.LoginActivity
import tech.redltd.lmsAgent.activities.sales_report.AgentSalesReportActivity
import tech.redltd.lmsAgent.activities.transaction_history.TransactionHistory
import tech.redltd.lmsAgent.adapter.productAdapter.ProductOnClick
import tech.redltd.lmsAgent.adapter.productAdapter.ProductRecyclerAdapter
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.network.MyProfileResponse
import tech.redltd.lmsAgent.network.aspDto.LoanQuery
import tech.redltd.lmsAgent.network.aspDto.LoanQueryResponse
import tech.redltd.lmsAgent.network.lmsDto.RobiShopPorductRequest
import tech.redltd.lmsAgent.network.lmsDto.RobiShopProductResponse
import tech.redltd.lmsAgent.utils.*

class HomeActivity : AppCompatActivity(),ProductOnClick {
    lateinit var drawerLayout:DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    private lateinit var mDrawerToggle:ActionBarDrawerToggle
    lateinit var handsetRecycler:RecyclerView
    private val apiService:ApiService by inject()
    lateinit var dialog: AlertDialog
    private lateinit var productRecyclerAdapter:ProductRecyclerAdapter
    private val appUtils : AppUtils by inject()

    lateinit var nav_view : NavigationView

    //6 month Text view
    lateinit var month6installment : TextView
    lateinit var month6Amount : TextView
    lateinit var month6downpayment : TextView

    //12 month text view
    lateinit var month12installment : TextView
    lateinit var month12Amount : TextView
    lateinit var month12downpayment : TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        nav_view = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbarAgentType.text = appUtils.getDataFromPreference(CommonConstant.AGENT_GROUP)//Agent Group


        val token:String = appUtils.getDataFromPreference(CommonConstant.TOKEN)!!
        CommonConstant.bearerToken = token
        dialog = loadingDialog()
        mDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        //userFullName.text = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)

       // getProducts()
        getProfile()

        init()

        rl6month.setOnClickListener {
            rl6month.setBackgroundResource(R.drawable.red_rounded)
            rl12month.setBackgroundResource(R.drawable.grey_rounded)

            val intent = Intent(this, CheckEligibilityActivity::class.java)
            val loanAmount:Int = txtamount6.text.toString().toInt()
            val loanInstallment:Int = txtinstallment6.text.toString().toInt()
            val loanDownPayment:Int = txtdownpayment6.text.toString().toInt()
            intent.putExtra("loanDurationIMonth",6)
            intent.putExtra("loanAmount",loanAmount)
            intent.putExtra("loanInstallment",loanInstallment)
            intent.putExtra("loanDownPayment",loanDownPayment)
            startActivity(intent)
        }

        rl12month.setOnClickListener {
            rl12month.setBackgroundResource(R.drawable.red_rounded)
            rl6month.setBackgroundResource(R.drawable.grey_rounded)

            val intent = Intent(this, CheckEligibilityActivity::class.java)
            val loanAmount:Int = month12Amount.text.toString().toInt()
            val loanInstallment:Int = month12installment.text.toString().toInt()
            val loanDownPayment:Int = month12downpayment.text.toString().toInt()

            intent.putExtra("loanDurationIMonth",12)
            intent.putExtra("loanAmount",loanAmount)
            intent.putExtra("loanInstallment",loanInstallment)
            intent.putExtra("loanDownPayment",loanDownPayment)
            startActivity(intent)
        }

        checkCreadit.setOnClickListener {
            if (userMobileNo.text.toString().isEmpty()){
                errorToast("Please input a phone number!")
            }else {
                if (appUtils.checkInternet()) {
                    EmiDepositLinear.visibility = View.GONE
                    loanInfoLinear.visibility =  View.VISIBLE
                    val cus_msisdn : String = "88"+userMobileNo.text.toString()
                    CommonConstant.CUSTOMER_NUMBER = cus_msisdn
                    appUtils.saveDataIntoPreference(CommonConstant.CUSTOMER_MSISDN,cus_msisdn)
                    vollyRequest(cus_msisdn)
                } else {
                    errorToast("Sorry! Internet is not enabled.")
                }
            }

/*            val cus_msisdn : String = "88"+userMobileNo.text.toString()
            CommonConstant.CUSTOMER_NUMBER = cus_msisdn
            appUtils.saveDataIntoPreference(CommonConstant.CUSTOMER_MSISDN, cus_msisdn)
            val intent = Intent(applicationContext, BkashPaymentActivity::class.java)
            startActivity(intent)*/



/*            //payment successful.  Go to another activity and save order data to database
            val intent = Intent(applicationContext, LoanSuccessActivity::class.java)
            intent.putExtra("TRANSACTION_ID", "transactionID")
            intent.putExtra("PAID_AMOUNT", "amount")
            intent.putExtra("PaymentID", "paymentID")
            intent.putExtra("PAYMENT_SERIALIZE", "data")
            startActivity(intent)*/


        }

        navHeaderText()

        nav_view.setNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_installmentCheck->{
                    startActivity(Intent(this, LoanStatusCheck::class.java))
                    drawerLayout.closeDrawers()
                }

                R.id.menu_change_password->{
                    startActivity(Intent(this,PasswordChangeActivity::class.java))
                    drawerLayout.closeDrawers()

                }

                R.id.menu_faq->{
                    startActivity(Intent(this,FaqActivity::class.java))
                    drawerLayout.closeDrawers()
                }

                R.id.menu_termsAndConditions->{
                    startActivity(Intent(this, TermsAndConditions::class.java))
                }

                R.id.menu_transaction_history->{
                    startActivity(Intent(this, TransactionHistory::class.java))
                }

                R.id.menu_sales_report->{
                    startActivity(Intent(this, AgentSalesReportActivity::class.java))
                }

                R.id.menu_logout->{
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
            }
            false
        }

        toolbar.setNavigationIcon(R.drawable.ic_nav_toggle_icon)
    }

    fun checkEligibility(view: View) {
        view.requestFocus()
        startActivity(Intent(this,CheckEligibilityActivity::class.java))
    }
    fun loadStatus(view: View) {
        view.requestFocus()
        startActivity(Intent(this,LoanStatusActivity::class.java))
    }
    fun payInstallment(view: View) {
        view.requestFocus()
        //startActivity(Intent(this,InstallmentCheckActivity::class.java))
        startActivity(Intent(this,LoanListActivity::class.java))
    }

    override fun onProductClick(sku: String) {
        //Toasty.success(this,"$sku").show()
    }

    private fun getProducts(){
        dialog.show()
        val robiShopProductRequest = RobiShopPorductRequest("50000","10")
        apiService.robiShopProducts(robiShopProductRequest).enqueue(object :Callback<RobiShopProductResponse> {
            override fun onFailure(call: Call<RobiShopProductResponse>, t: Throwable) {
                errorToast("error fetch api")
                dialog.hide()

            }

            override fun onResponse(call: Call<RobiShopProductResponse>,response: Response<RobiShopProductResponse>) {
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

    private fun getProfile(){
        apiService.getMyProfile().enqueue(object : Callback<MyProfileResponse> {
            override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<MyProfileResponse>,
                response: Response<MyProfileResponse>
            ) {
                if (response.isSuccessful){
                    response.body().let {
                        val payload = it?.payload
                        if (payload != null){
                            appUtils.saveDataIntoPreference(CommonConstant.CUSTOMER_IMAGE,payload.cusImage)
                        }
                    }

                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

    @SuppressLint("SetTextI18n")
    private fun navHeaderText(){
        try {
            //val loginUsername = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_NAME)!!
            //val customerMSISDNS = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!
            val agentFirstName = appUtils.getDataFromPreference(CommonConstant.AGENT_FIRSTNAME)!!
            val agentLastName = appUtils.getDataFromPreference(CommonConstant.AGENT_LASTNAME)!!
            val agentMobileNo = appUtils.getDataFromPreference(CommonConstant.AGENT_MOBILE_NO)!!

            val navigationView:NavigationView = findViewById(R.id.nav_view)
            val navHeader : View = navigationView.getHeaderView(0)
            val userName:TextView = navHeader.findViewById(R.id.navHeaderUserName)

            userName.text = ""+agentFirstName+agentLastName
            navHeaderAgentPhoneNo.text = agentMobileNo
        }catch (ex:Exception){

        }

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

    }

    private fun vollyRequest(cus_msisdn:String){
        dialog.show()
        val agentId:String = appUtils.getDataFromPreference(CommonConstant.AGENT_ID)!!
        val agent_password:String = appUtils.getDataFromPreference(CommonConstant.AGENT_PASS)!!
        //val cus_msisdn:String = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!

        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("Agent_id", agentId)
        json.put("ag_password", agent_password)
        json.put("cus_msisdn", cus_msisdn)

        val stringRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(
            Method.POST, CommonUrl.LOAN_QUERY,json,
            com.android.volley.Response.Listener { response ->
            dialog.hide()
            try {
                val gson = Gson()
                val loanQueryResponse: LoanQueryResponse = gson.fromJson(response.toString(), LoanQueryResponse::class.java)
                val loanQuery: LoanQuery = loanQueryResponse.loanQuery
                //errorToast(""+response.toString())
                if(loanQuery != null){
                    // 6 month
                    month6installment.text = loanQuery.month6Installment
                    txtinstallment6Month.text = "Tk "+loanQuery.month6Installment  //DIP
                    month6Amount.text = loanQuery.month6
                    txtamount6Month.text = "Tk "+loanQuery.month6  //DIP
                    month6downpayment.text = loanQuery.month6DownPayment
                    txtdownpayment6Month.text = "Tk "+loanQuery.month6DownPayment  //DIP

                    if (loanQuery.month6 == "0"){
                        rl6month.visibility = View.GONE
                    }else{
                        rl6month.visibility = View.VISIBLE
                    }

                    // 12 month
                    month12installment.text = loanQuery.month12Installment
                    txtinstallment12Month.text = "Tk "+loanQuery.month12Installment
                    month12Amount.text = loanQuery.month12
                    txtamount12Month.text = "Tk "+loanQuery.month12
                    month12downpayment.text = loanQuery.month12DownPayment
                    txtdownpayment12Month.text = "Tk "+loanQuery.month12DownPayment

                    if (loanQuery.month12 == "0"){
                        rl12month.visibility = View.GONE
                    }else{
                        rl12month.visibility = View.VISIBLE
                    }

                }else{
                    AlertDialog.Builder(this).setMessage(loanQueryResponse.status).setPositiveButton(android.R.string.yes) {
                            dialog, which ->

                            dialog.dismiss()
                            // Continue with delete operation
                            //onBackPressed()
                        } // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()

                    loanInfoLinear.visibility = View.GONE
                    //EmiDepositLinear.visibility = View.VISIBLE
                }


               // can_Apply_LoanTv.text = loanQueryResponse.status
            }catch (ex:Exception){
                ex.printStackTrace()

            }

        },
            com.android.volley.Response.ErrorListener { error ->
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
