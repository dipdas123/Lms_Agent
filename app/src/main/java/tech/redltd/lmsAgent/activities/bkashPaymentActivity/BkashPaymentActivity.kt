package tech.redltd.lmsAgent.activities.bkashPaymentActivity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bkash_payment.*
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.utils.AppUtils
import tech.redltd.lmsAgent.utils.CommonConstant
import tech.redltd.lmsAgent.utils.CommonUrl
import tech.redltd.lmsAgent.utils.setHomeToolbar


class BkashPaymentActivity : AppCompatActivity() , BkashOnlickInterface{
    private lateinit var wvBkashPayment: WebView
    private val appUtils : AppUtils by inject()
    var urls = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bkash_payment)
        setHomeToolbar("bKash")
        //        val amount = CommonConstant.downPaymentAmount
        val downPaymentAmount:String = ""+appUtils.getDataFromPreference(CommonConstant.DownPaymentAmount)
        val a1:String = ""+appUtils.getDataFromPreference(CommonConstant.TOKEN_BEARER)
        val cmsisdn:String = ""+appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)

        val url: String = CommonUrl.bkashUrl+downPaymentAmount+"&a1="+a1+"&cmsisdn="+cmsisdn
        //showAlert("ALERT","$downPaymentAmount\n\n$a1\n\nCUSTOMER PHONE$cmsisdn\n\n$url")

        wvBkashPayment = findViewById(R.id.bkashWebView)

        progress_circular.visibility = View.VISIBLE

        val webSettings: WebSettings = wvBkashPayment.settings
        webSettings.javaScriptEnabled = true

        CommonConstant.downPaymentAmount = ""

        wvBkashPayment.isClickable = true
        wvBkashPayment.settings.domStorageEnabled = true
        wvBkashPayment.settings.setAppCacheEnabled(false)
        wvBkashPayment.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        wvBkashPayment.clearCache(true)
        wvBkashPayment.settings.allowFileAccessFromFileURLs = true
        wvBkashPayment.settings.allowUniversalAccessFromFileURLs = true

        //Check wheather Installment Payment / Loan DownPayment
        if (CommonConstant.is_EmiPay){
            val emiPayJavaScriptInterface  = EmiPayJavaScriptInterface(this)
            emiPayJavaScriptInterface.setBkashOnlickInterface(this)
            wvBkashPayment.addJavascriptInterface(emiPayJavaScriptInterface, "BkshPaymentData")
            CommonConstant.is_EmiPay = false
        }else{
            val bkashOnlickInterface = BkashJavaScriptInterface(this)
            bkashOnlickInterface.setBkashOnlickInterface(this)
            wvBkashPayment.addJavascriptInterface(bkashOnlickInterface, "BkshPaymentData")
        }

        wvBkashPayment.loadUrl(url) // api host link .
        wvBkashPayment.webViewClient = CheckoutWebViewClient()

        swipeToRefresh.setOnRefreshListener {
            progress_circular.visibility = View.VISIBLE
            wvBkashPayment.reload()
            if (swipeToRefresh.isRefreshing){
                swipeToRefresh.isRefreshing = false
            }
        }

        wvBkashPayment.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progress_circular.visibility = View.GONE
                }
            }
        }



/*        retryBtnBkash.setOnClickListener{
            progress_circular.visibility = View.VISIBLE
            wvBkashPayment.reload()
        }

        backBtnBkash.setOnClickListener{
            popupPaymentCancelAlert()
        }*/




    }

    override fun onSupportNavigateUp(): Boolean {
        popupPaymentCancelAlert()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        popupPaymentCancelAlert()
    }

    private fun popupPaymentCancelAlert() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setMessage("Want to cancel payment process?")
        alert.setCancelable(false)
        alert.setTitle("Alert!")
        alert.setPositiveButton("Yes") { _, _ ->
            onBackPressed()
        }
        alert.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.cancel() }
        val alertDialog: AlertDialog = alert.create()
        alertDialog.show()
    }

    inner class CheckoutWebViewClient : WebViewClient() {
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            //handler.proceed()

            val builder = AlertDialog.Builder(this@BkashPaymentActivity)
            builder.setMessage("Please press continue to proceed.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Continue"){dialogInterface, which ->
                handler.proceed()
            }
            builder.setNegativeButton("Cancel"){dialogInterface, which ->
                handler.cancel()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.i("External URL: ", url)
            if (url == "https://www.bkash.com/terms-and-conditions") {
                val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(myIntent)
                return true
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            val paymentRequest = "{paymentRequest:" + "" + "}"
            wvBkashPayment.loadUrl("javascript:callReconfigure($paymentRequest )")
            wvBkashPayment.loadUrl("javascript:clickPayButton()")

        }



    }



    override fun finishActivity() {
        finish()
    }
}


