package tech.redltd.lmsAgent.activities

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_faq.*
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.utils.setHomeToolbar

class FaqActivity : AppCompatActivity() {
    private lateinit var faqWebView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        faqWebView = findViewById(R.id.faqWebView)
        setHomeToolbar("Faq")

        faqWebView.isClickable = true
        faqWebView.settings.domStorageEnabled = true
        faqWebView.settings.setAppCacheEnabled(false)
        faqWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        faqWebView.clearCache(true)
        faqWebView.settings.allowFileAccessFromFileURLs = true
        faqWebView.settings.allowUniversalAccessFromFileURLs = true
        faqWebView.webViewClient = WebViewClient()
        faqWebView.loadUrl("https://lms.robi.com.bd/lms_faq")

        //faqWebView.webViewClient = BkashPaymentActivity.CheckoutWebViewClient()
        faqWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
