package tech.redltd.lmsAgent.activities.createAccount

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_account.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.otpActivity.OtpActivity
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.utils.CommonConstant
import tech.redltd.lmsAgent.utils.errorToast
import tech.redltd.lmsAgent.utils.loadingDialog

class CreateAccountActivity : AppCompatActivity() {
    lateinit var userFirstName:EditText
    lateinit var userLastName:EditText
    lateinit var userMobileNo:EditText
    lateinit var userPin:EditText
    lateinit var userEmail:EditText
    lateinit var userDob:EditText
    lateinit var userNidNo:EditText
    private var userDobText:String =""
    lateinit var buttonRegister:Button
    lateinit var dialog: AlertDialog
    val apiService:ApiService by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        //find elements
        userFirstName = findViewById(R.id.userFirstName)
        userLastName = findViewById(R.id.userLastName)
        userMobileNo = findViewById(R.id.userMobileNo)
        userPin = findViewById(R.id.userPin)
        userEmail = findViewById(R.id.userEmail)
        userDob = findViewById(R.id.userDob)
        userNidNo = findViewById(R.id.userNidNo)
        dialog = loadingDialog()
        buttonRegister = findViewById(R.id.buttonRegister)


        val datePicker = MaterialDatePicker.Builder.datePicker().build()

        userDob.setOnClickListener {
            datePicker.show(supportFragmentManager,"dob")
        }

        datePicker.addOnPositiveButtonClickListener {
            userDobText = datePicker.headerText
            userDobLayout.hint = userDobText
        }



        buttonRegister.setOnClickListener {
            if (validate()){

             registerUser()
            }
        }
    }

    private fun validate():Boolean{
        if (userFirstName.text.toString().isEmpty()){
            userFirstName.error = "First Name Is Required"
            userFirstName.requestFocus()
            return false
        }
        if (userLastName.text.toString().isEmpty()){
            userLastName.error = "Last Name Is Required"
            userLastName.requestFocus()
            return false
        }
        if (userMobileNo.text.toString().isEmpty()){
            userMobileNo.error = "Mobile Number Is Required"
            userMobileNo.requestFocus()
            return false
        }
        if (userPin.text.toString().isEmpty()){
            userPin.error = "Password Is Required"
            userPin.requestFocus()
            return false
        }
        if (userEmail.text.toString().isEmpty()){
            userEmail.error = "Email Is Required"
            userEmail.requestFocus()
            return false
        }
        if (userDobText.isEmpty()){
            userDob.error = "Please Select your Date of Birth"
            userDob.requestFocus()
            return false
        }
        if (userNidNo.text.toString().isEmpty()){
            userNidNo.error = "Nid Is Required"
            userNidNo.requestFocus()
            return false
        }

        return true
    }

    private fun registerUser(){
        dialog.show()
        val firstname = userFirstName.text.toString()
        val lastName = userLastName.text.toString()
        val phone = "88"+userMobileNo.text.toString()
        val password = userPin.text.toString()
        val email = userEmail.text.toString()
        val dob = userDobText
        val nid = userNidNo.text.toString()
        CommonConstant.phoneNumber = phone
        val createAccountBody = CreateAccountBody(firstname,lastName,phone,password,email,dob,nid,null)
        apiService.customerRegistration(createAccountBody).enqueue(object : Callback<CreateAccountResponse> {
            override fun onFailure(call: Call<CreateAccountResponse>, t: Throwable) {
                dialog.hide()
            }

            override fun onResponse(
                call: Call<CreateAccountResponse>,
                response: Response<CreateAccountResponse>
            ) {
                dialog.hide()
              if (response.isSuccessful){
                  val intent = Intent(this@CreateAccountActivity,OtpActivity::class.java)
                  CommonConstant.fromRegister = true
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                  startActivity(intent)
              }else{
                 try {
                     val gson = Gson()
                     val error : RegisterErrorBody = gson.fromJson(response.errorBody()!!.string(),RegisterErrorBody::class.java)
                     errorToast(error.errors)
                 }catch (ex:Exception){}
              }

            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

}
