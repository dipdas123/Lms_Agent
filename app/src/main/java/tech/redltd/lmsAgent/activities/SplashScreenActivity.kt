package tech.redltd.lmsAgent.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loginActivity.LoginActivity
import tech.redltd.lmsAgent.utils.AppUtils
import tech.redltd.lmsAgent.utils.CommonConstant

class SplashScreenActivity : AppCompatActivity() {
    private val appUtils : AppUtils by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity_splash)


        Handler().postDelayed({
            val isLogin :String? = appUtils.getDataFromPreference(CommonConstant.AGENT_ID)
            val isOtpValud = appUtils.getDataFromPreference(CommonConstant.IS_OTP_VALID)
            if (isLogin!!.isNotEmpty() && isOtpValud.equals("True") ){
                startActivity(Intent(this,HomeActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            //startActivity(Intent(this, ApplyLoanActivity::class.java))


        },2000)


    }
}
