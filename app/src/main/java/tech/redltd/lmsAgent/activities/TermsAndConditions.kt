package tech.redltd.lmsAgent.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.agreement_dialog_layout.pageTitle
import kotlinx.android.synthetic.main.agreement_dialog_layout.readCheckBox
import kotlinx.android.synthetic.main.agreement_dialog_layout_for_nv.*
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.utils.setHomeToolbar

class TermsAndConditions : AppCompatActivity(){

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agreement_dialog_layout_for_nv)

        toolbar.visibility = View.VISIBLE
        setHomeToolbar("Terms & Conditions")
        readCheckBox.visibility = View.GONE
        aggreeBtnLayout.visibility = View.GONE
        pageTitle.text = "Terms & Conditions"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
