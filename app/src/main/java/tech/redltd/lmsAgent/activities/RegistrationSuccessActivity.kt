package tech.redltd.lmsAgent.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loginActivity.LoginActivity
import tech.redltd.lmsAgent.utils.CommonConstant

class RegistrationSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_success)
    }

    fun registerSuccessClick(view: View) {
        val intent = Intent(this,LoginActivity::class.java)
        CommonConstant.fromRegister = false
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
