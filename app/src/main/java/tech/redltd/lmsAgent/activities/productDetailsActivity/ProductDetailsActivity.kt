package tech.redltd.lmsAgent.activities.productDetailsActivity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_product_details.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.applyLoan.ApplyLoanActivity
import tech.redltd.lmsAgent.activities.applyLoan.ApplyLoanConstant
import tech.redltd.lmsAgent.utils.*
import kotlin.collections.set

class ProductDetailsActivity : AppCompatActivity() {
    lateinit var dialog: AlertDialog
    private val appUtils : AppUtils by inject()
    var phoneQuantity:Int = 0
    var phoneSKU:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        dialog = loadingDialog()
        setHomeToolbar("Product Details")
        val loanDuration =  intent.getIntExtra("loan_duration",0)
        val productSKU =  intent.getStringExtra("productSKU")!!
        val dmrp = intent.getIntExtra("dmrp",0)
        val productPrice = CommonConstant.PRODUCT_PRICE
        checkCredential(loanDuration,dmrp,productPrice)
        getProductDetails(productSKU)
        ApplyLoanConstant.devicePrice = productPrice.toDouble()
        ApplyLoanConstant.sku = productSKU
        ApplyLoanConstant.loanDurationMonth = loanDuration
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

    private fun getProductDetails(productSKU:String){
        dialog.show()
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("Sku", productSKU)
        val stringRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(
            Method.POST, CommonUrl.SingleProduct,json, Response.Listener { response ->
            dialog.hide()
            try {
                val gson = Gson()
                val singleProduct: ProductDetailsResponse = gson.fromJson(response.toString(),ProductDetailsResponse::class.java)
                val singleProductSpecs: ProductSpecificationResponse = gson.fromJson(response.toString(),ProductSpecificationResponse::class.java)

                Log.i("SingleProduct",response.toString())
                if (singleProduct.RobishopContent.products.isNotEmpty()){
                    val product = singleProduct.RobishopContent.products[0]
                    singleProductTitle.text = product.name
                    singleProductPrice.text = "Price: ${product.price} Tk. "
                    Glide.with(this).load(product.baseImageUrl).into(singleProductImage)
                    singleProductBrand.text = "Brand: ${product.brand}"
                    singleProductColor.text = "Color: ${product.color}"
                    singleProductQty.text = "${product.qty}"
                    phoneQuantity = product.qty
                    phoneSKU = product.name
                    singleProductWarranty.text = "Warranty: ${product.productWarranty}"

                }else{
                    successToast("No Product Details Provided!")
                }


                //Device Specs
                if (singleProductSpecs.deviceSpecs.isNotEmpty()) {
                    val specificationResponse = singleProductSpecs.deviceSpecs[0]
                    val androidversion: String = specificationResponse.androidversion
                    val displayS: String = specificationResponse.display
                    val memoryS: String = specificationResponse.memory
                    val cameraS: String = specificationResponse.camera
                    val batteryS: String = specificationResponse.battery
                    val detailslinkS: String = specificationResponse.detailslink
                    //val detailslinkS: String = specificationResponse.detailslink
                    val processorS: String = specificationResponse.processor


                    if (androidversion == ""){
                        val noData = "No Information Provided!"
                        androidVersion.text = noData
                    }else{
                        androidVersion.text = androidversion
                    }
                    if (displayS == ""){
                        val noData = "No Information Provided!"
                        display.text = noData
                    }else{
                        display.text = displayS
                    }
                    if (memoryS == ""){
                        val noData = "No Information Provided!"
                        memory.text = noData
                    }else{
                        memory.text = memoryS
                    }
                    if (cameraS == ""){
                        val noData = "No Information Provided!"
                        camera.text = noData
                    }else{
                        camera.text = cameraS
                    }
                    if (batteryS == ""){
                        val noData = "No Information Provided!"
                        battery.text = noData
                    }else{
                        battery.text = batteryS
                    }
                    if (processorS == ""){
                        val noData = "No Information Provided!"
                        detailslink.text = noData
                    }else{
                        detailslink.text = processorS
                    }
/*                    if (detailslinkS == ""){
                        val noData = "No Information Provided!"
                        detailslink.text = noData
                    }else{
                        val textSt = "Click for more details.".toSpannable()

                        // Set clickable span
                        textSt[0..23] = object: ClickableSpan(){
                            override fun onClick(view: View) {
                                val uri: Uri = Uri.parse(detailslinkS)
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                startActivity(intent)
                            }
                        }
                        detailslink.movementMethod = LinkMovementMethod()
                        detailslink.text = textSt
                    }*/
                }else{
                    linSpecsMain.visibility = View.GONE
                    successToast("No Product Specifications provided!")
                }

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

    /*    fun applyForLoanOnClick(view: View) {
        view.requestFocus()
        val fragmentManager : FragmentManager = supportFragmentManager
        val downPayment = singleDownpayment.text.toString()
        CommonConstant.downPaymentAmount = downPayment
        ApplyLoanConstant.downPayment = downPayment.toDouble()
        ApplyLoanConstant.emi = singleInstallment.text.toString().toDouble()
        ApplyLoanConstant.loanAmount = singleAmount.text.toString().toDouble()
        val agreementDialog = AgreementDialog()

        agreementDialog.show(fragmentManager,"hello world")
    }*/

    fun applyForLoanOnClick(view: View) {
        val intent = Intent(applicationContext, ApplyLoanActivity::class.java)
        val downPayment = singleDownpayment.text.toString()
        val total_loan_amt = singleAmountMonth.text.toString()
        CommonConstant.downPaymentAmount = downPayment
        ApplyLoanConstant.downPayment = downPayment.toDouble()
        ApplyLoanConstant.emi = singleInstallment.text.toString().toDouble()
        ApplyLoanConstant.loanAmount = singleAmount.text.toString().toDouble()

        //Saving DownPayment Amount Each time
        appUtils.saveDataIntoPreference(CommonConstant.DownPaymentAmount, downPayment)
        appUtils.saveDataIntoPreference(CommonConstant.TOTAL_LOAN_AMOUNT, total_loan_amt)
        appUtils.saveDataIntoPreference(CommonConstant.PHONE_QUANTITY, phoneQuantity.toString())
        appUtils.saveDataIntoPreference(CommonConstant.PHONE_SKU, phoneSKU)

        intent.putExtra("singleDownpayment", downPayment)
        intent.putExtra("downPayment", CommonConstant.downPaymentAmount)
        intent.putExtra("downPaymentToDouble", ApplyLoanConstant.downPayment)
        intent.putExtra("emi", ApplyLoanConstant.emi)
        intent.putExtra("loanAmount", ApplyLoanConstant.loanAmount)
        intent.putExtra("phoneQuantity", phoneQuantity)
        startActivity(intent)
        finish()

    }


    private fun checkCredential(loan_duration: Int, dmrp: Int, productPrice: Int){
        dialog.show()
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        val agentId = appUtils.getDataFromPreference(CommonConstant.AGENT_ID)
        val customerMsisdn = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)
        json.put("agentId", agentId)
        json.put("customer_msisdn", CommonConstant.CUSTOMER_NUMBER)
        json.put("Loan_duration_month", loan_duration)
        json.put("dmrp", productPrice)
        json.put("loanamount", dmrp)

        val stringRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(
            Method.POST, CommonUrl.loanCredentila,json, Response.Listener { response ->
                dialog.hide()
                try {
                    Log.i("checkCredential",response.toString())
                    val gson = Gson()
                    val checkCredentialResponse : CheckCredentialResponse = gson.fromJson(response.toString(),CheckCredentialResponse::class.java)
                    if (checkCredentialResponse.loanCredential != null){
                        val loanCredential = checkCredentialResponse.loanCredential
                        singleInstallment.text = loanCredential.emi.toString()
                        singleInstallmentMonth.text = "Tk "+loanCredential.emi.toString()
                        singleDownpayment.text = loanCredential.downpayment.toString()
                        singleDownpaymentMonth.text = "Tk "+loanCredential.downpayment.toString()
                        singleAmount.text = loanCredential.loanamount.toString()
                        singleAmountMonth.text = "Tk "+loanCredential.loanamount.toString()
                        txtmonth6.text = "$loan_duration Month"
                    }


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
