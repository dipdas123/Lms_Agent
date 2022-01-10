package tech.redltd.lmsAgent.activities.profileActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.utils.setHomeToolbar

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setHomeToolbar("Profile")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
