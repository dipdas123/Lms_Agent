package tech.redltd.lmsAgent.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_update_profile.*
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.fragments.UpdateLoadHolderProfileFragment
import tech.redltd.lmsAgent.fragments.UpdateNomineeProfileFragment
import tech.redltd.lmsAgent.utils.setHomeToolbar

class UpdateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        setHomeToolbar("Update Profile")
        replaceFragment(UpdateLoadHolderProfileFragment())
        tabLayout.addTab(tabLayout.newTab().setText(R.string.loan_holder))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.nominee_info))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tabLayout.selectedTabPosition){
                    0->replaceFragment(UpdateLoadHolderProfileFragment())
                    1->replaceFragment(UpdateNomineeProfileFragment())
                }
            }
        })

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
