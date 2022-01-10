package tech.redltd.lmsAgent.activities.applyLoan

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_apply_loan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.dialog.AgreementDialog
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.network.district.DistrictResponse
import tech.redltd.lmsAgent.network.district.Payload
import tech.redltd.lmsAgent.network.thana.ThanaPayload
import tech.redltd.lmsAgent.network.thana.ThanaResponse
import tech.redltd.lmsAgent.utils.*
import java.io.ByteArrayOutputStream
import kotlin.coroutines.CoroutineContext

class ApplyLoanActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext get() = mJob + Dispatchers.Main
    private lateinit var mContext : Context
    private val appUtils : AppUtils by inject()
    private val apiService: ApiService by inject()

    private  val HOLDER_FRONT = 400
    private  val HOLDER_BACK = 401

    private  val NOMINEE_FRONT = 402
    private  val NOMINEE_BACK = 403

    private val HOLDER_PROFILE = 404

    private val filterThana: ArrayList<Thana> = ArrayList()
    private val filterDeliveryThana: ArrayList<Thana> = ArrayList()
    private val filterNomineeThana: ArrayList<Thana> = ArrayList()

    private var holderNidFront = "data:image/jpeg;base64,"
    private var holderNidBack = "data:image/jpeg;base64,"

    private var nomineeNidFront = "data:image/jpeg;base64,"
    private var nomineeNidBack = "data:image/jpeg;base64,"
    private var holderProfile = "data:image/jpeg;base64,"

    private var transactionId=""
    private lateinit var progressDialog: AlertDialog
    private var holderdateOfBirth = ""

    private lateinit var holderDistrictSpinner: Spinner
    private lateinit var holderThanaSpinner: Spinner

    private lateinit var deliveryDistrictSpinner: Spinner
    private lateinit var deliveryThanaSpinner: Spinner

    private var deliveryEqualPermanent:String = ""

    private lateinit var permanentDistrictSpinner: Spinner
    private lateinit var permanentThanaSpinner: Spinner
    private var isAddressIsDeliveryAddressCheck : Boolean = false
    private var isPermanentDistrictIsDeliveryDistrict : String = ""
    private var isPermanentThanaIsDeliveryThana : String = ""
    private var isPermanentAddressIsDeliveryAddress : String = ""
    private var isPermanentPostCodeIsDeliveryPostCode : String = ""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_loan)
        setHomeToolbar("Apply For Loan")

        try {
            transactionId = intent.getStringExtra("TRANSACTION_ID")!!
            //successToast(transactionId)
        }catch (ex:Exception){
            // errorToast("no transaction Id")
        }


        val phoneQtyString: String = appUtils.getDataFromPreference(CommonConstant.PHONE_QUANTITY)!!
        //successToast(""+phoneQtyString)


        holderDistrictSpinner = findViewById(R.id.holderDistrictSpinner)
        holderThanaSpinner = findViewById(R.id.holderThanaSpinner)


        deliveryDistrictSpinner = findViewById(R.id.deliveryDistrictSpinner)
        deliveryThanaSpinner = findViewById(R.id.deliveryThanaSpinner)

        permanentDistrictSpinner = findViewById(R.id.permanentDistrictSpinner)
        permanentThanaSpinner = findViewById(R.id.permanentThanaSpinner)

        progressDialog = loadingDialog()
        getDistrictList()

        //Present Address = Delivery Address
        checkBox1SameAsPermanentAdd.setOnClickListener {
            when {
                holderAddress.text.toString().isEmpty() -> {
                    checkBox1SameAsPermanentAdd.isChecked = false
                    errorToast("Please enter your present address first!")
                }
                holderPostCode.text.toString().isEmpty() -> {
                    checkBox1SameAsPermanentAdd.isChecked = false
                    errorToast("Please enter your present postcode first!")
                }
                else -> {
                    if (checkBox1SameAsPermanentAdd.isChecked) {
                        deliveryEqualPermanent = "true"
                        deliveryAddressLinear.layoutParams = LinearLayout.LayoutParams(0, 0)

                        isPermanentAddressIsDeliveryAddress = holderAddress.text.toString()
                        deliveryAddress.setText("Enter Address")
                        isPermanentDistrictIsDeliveryDistrict = holderDistrictSpinner.selectedItem.toString()
                        isPermanentThanaIsDeliveryThana = holderThanaSpinner.selectedItem.toString()
                        isPermanentPostCodeIsDeliveryPostCode = holderPostCode.text.toString()
                        deliveryPostCode.setText("Enter Postcode")

                        //successToast("You have chosen permanent address as your delivery address" + "\n\n$isPermanentAddressIsDeliveryAddress\n$isPermanentDistrictIsDeliveryDistrict" + "\n$isPermanentThanaIsDeliveryThana\n${isPermanentPostCodeIsDeliveryPostCode}")

                    } else {
                        deliveryEqualPermanent = ""
                        isAddressIsDeliveryAddressCheck = false
                        deliveryAddressLinear.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                        deliveryAddress.setText("")
                        deliveryPostCode.setText("")

                        //successToast("Please add delivery address information")
                    }
                }
            }
        }


        //Date Picker
        val builder = MaterialDatePicker.Builder.datePicker()
        val datePicker = builder.build()
        holderDobLayout.isHintAnimationEnabled = false
        holderDobLayout.setOnClickListener {
            datePicker.show(supportFragmentManager,"Holder-datePicker")
        }
        datePicker.addOnPositiveButtonClickListener {
            holderdateOfBirth = datePicker.headerText
            holderDobLayout.hint = holderdateOfBirth
        }
        val nomineeBuilder = MaterialDatePicker.Builder.datePicker()
        val nomineeDatePicker = nomineeBuilder.build()
        nomineeDobLayout.setOnClickListener {
            nomineeDatePicker.show(supportFragmentManager,"Secondary Contact Person's-DOB")
        }
        nomineeDobLayout.isHintAnimationEnabled = false
        nomineeDatePicker.addOnPositiveButtonClickListener {
            nomineeDobLayout.hint = nomineeDatePicker.headerText.toString()
        }


        val singleDownpayment: String =  intent.getStringExtra("singleDownpayment")!!
        val downPayment: String =  intent.getStringExtra("downPayment")!!
        val downPaymentToDouble: Double = intent.getDoubleExtra("downPaymentToDouble", 0.0)
        val emi: Double = intent.getDoubleExtra("emi",0.0)
        val loanAmount: Double = intent.getDoubleExtra("loanAmount",0.0)
        val phoneQuantity: Int = intent.getIntExtra("phoneQuantity", 0)
        //successToast(singleDownpayment+"\n"+downPayment+"\n"+downPaymentToDouble+"\n"+emi+"\n"+loanAmount)


        mJob = Job()
        btnContinue.setOnClickListener {
            if (validateLoanApplyRequest()) {
                //applyForLoan()
                getApplyForLoanRequest(phoneQuantity)
                applyforLoanAndAggrement(singleDownpayment, downPayment, downPaymentToDouble, emi, loanAmount)
            }
        }


        holdernidFront.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this)
            startActivityForResult(intent, HOLDER_FRONT)
        }

        holderProfileImage.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this)
            startActivityForResult(intent, HOLDER_PROFILE)
        }

        holdernidBack.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this)
            startActivityForResult(intent, HOLDER_BACK)
        }

        nidFront.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this)
            startActivityForResult(intent, NOMINEE_FRONT)
        }

        nidBack.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this)
            startActivityForResult(intent, NOMINEE_BACK)
        }

/*        updateHolderAutoCompleteTextView()
        updateDeliveryAutoCompleteTextView()
        updateNomineeAutoCompleteTextView()*/

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    fun applyforLoanAndAggrement(singleDownpayment: String, downPayment: String, downPaymentToDouble: Double, emi: Double, loanAmount: Double) {
        //requestFocus()
        val fragmentManager : FragmentManager = supportFragmentManager
        val downPaymentS: String = singleDownpayment
        CommonConstant.downPaymentAmount = downPayment
        ApplyLoanConstant.downPayment = downPaymentToDouble
        ApplyLoanConstant.emi = emi
        ApplyLoanConstant.loanAmount = loanAmount
        val agreementDialog = AgreementDialog()

        agreementDialog.show(fragmentManager,"hello world")
    }


    private fun getApplyForLoanRequest(phoneQuantity: Int):JSONObject{
        val address: String = holderAddress.text.toString()
        val agentEmail: String = appUtils.getDataFromPreference(CommonConstant.AGENT_EMAIL)!!
        val agentId: Int = appUtils.getDataFromPreference(CommonConstant.AGENT_ID)!!.toInt()
        val agentPassword: String = appUtils.getDataFromPreference(CommonConstant.AGENT_PASS)!!
        val city: String = holderDistrictSpinner.selectedItem.toString()
        val companyId: Int = 1
        val cusImage: String = holderProfile
        val cusNidImageBack: String = holderNidBack
        val cusNidImageFront: String = holderNidFront
        val cusNidNumber: String = holderNidNo.text.toString()
        val customerMsisdn: String = appUtils.getDataFromPreference(CommonConstant.CUSTOMER_MSISDN)!!

        var deliveryAddressString: String = ""
        var deliveryCity: String = ""
        var deliveryDistrict: String = ""
        var deliveryThana: String = ""
        var deliveryZipCode: Int = 0

        if (deliveryEqualPermanent == ""){
            deliveryAddressString = deliveryAddress.text.toString()
            deliveryCity = deliveryDistrictSpinner.selectedItem.toString()
            deliveryDistrict = deliveryDistrictSpinner.selectedItem.toString()
            deliveryThana =  deliveryThanaSpinner.selectedItem.toString()
            deliveryZipCode = deliveryPostCode.text.toString().toInt()
        }else{
            deliveryAddressString = isPermanentAddressIsDeliveryAddress
            deliveryCity = isPermanentDistrictIsDeliveryDistrict
            deliveryDistrict = isPermanentDistrictIsDeliveryDistrict
            deliveryThana = isPermanentThanaIsDeliveryThana
            deliveryZipCode = isPermanentPostCodeIsDeliveryPostCode.toInt()
        }


        val devicePrice: Double = ApplyLoanConstant.devicePrice
        val district: String = holderDistrictSpinner.selectedItem.toString()
        val downPayment: Double = ApplyLoanConstant.downPayment
        val emi: Double = ApplyLoanConstant.emi
        val firstName: String = holderFirstName.text.toString()
        val isAddressIsDeliveryAddress: Boolean = false
        val isVerificationRequired: Boolean = false
        val lastName: String = holderLastName.text.toString()
        val loanAmount: Double = ApplyLoanConstant.loanAmount
        val loanDurationMonth: Int = ApplyLoanConstant.loanDurationMonth

        val nomFirstName: String = nomineeFirstName.text.toString()
        val nomLastName: String = nomineeLastName.text.toString()
        val nomMsisdn: String = nomineePhoneNumber.text.toString()
        val nomNidImageBack: String = nomineeNidBack
        val nomNidImageFront: String = nomineeNidFront
        val nomNidNumber: String = nomineeNidNo.text.toString()

        val permanentAddress: String = holderPermanentAddress.text.toString()
        val permanentCity: String =  permanentDistrictSpinner.selectedItem.toString()
        val permanentDistrict: String = permanentDistrictSpinner.selectedItem.toString()
        val permanentPostcode: Int = holderPermanentPostCode.text.toString().toInt()
        val permanentThana: String = permanentThanaSpinner.selectedItem.toString()

        val postcode: Int = holderPostCode.text.toString().toInt()
        val sku: String = ApplyLoanConstant.sku
        val thana: String= holderThanaSpinner.selectedItem.toString()
        val transectionId: String= transactionId
        val transectionType: String="DownPayment"


        val jsonObject = JSONObject()
        jsonObject.put("address",address)
        jsonObject.put("agentEmail",agentEmail)
        jsonObject.put("agentId",agentId)
        jsonObject.put("agentPassword",agentPassword)
        jsonObject.put("city",city)
        jsonObject.put("companyId",companyId)
        jsonObject.put("cusImage",cusImage)
        jsonObject.put("cusNidImageBack",cusNidImageBack)
        jsonObject.put("cusNidImageFront",cusNidImageFront)
        jsonObject.put("cusNidNumber",cusNidNumber)
        jsonObject.put("customerMsisdn",customerMsisdn)
        jsonObject.put("deliveryAddress",deliveryAddressString)
        jsonObject.put("deliveryCity",deliveryCity)
        jsonObject.put("deliveryDistrict",deliveryDistrict)
        jsonObject.put("deliveryThana",deliveryThana)
        jsonObject.put("deliveryZipCode",deliveryZipCode)
        jsonObject.put("devicePrice",devicePrice)
        jsonObject.put("district",district)
        jsonObject.put("downPayment",downPayment)
        jsonObject.put("emi",emi)
        jsonObject.put("firstName",firstName)
        jsonObject.put("isAddressIsDeliveryAddress",isAddressIsDeliveryAddress)
        jsonObject.put("isVerificationRequired",isVerificationRequired)
        jsonObject.put("lastName",lastName)
        jsonObject.put("loanAmount",loanAmount)
        jsonObject.put("loanDurationMonth",loanDurationMonth)
        jsonObject.put("nomFirstName",nomFirstName)
        jsonObject.put("nomLastName",nomLastName)
        jsonObject.put("nomMsisdn",nomMsisdn)
        jsonObject.put("nomNidImageBack",nomNidImageBack)
        jsonObject.put("nomNidImageFront",nomNidImageFront)
        jsonObject.put("nomNidNumber",nomNidNumber)
        jsonObject.put("permanentAddress",permanentAddress)
        jsonObject.put("permanentCity",permanentCity)
        jsonObject.put("permanentDistrict",permanentDistrict)
        jsonObject.put("permanentPostcode",permanentPostcode)
        jsonObject.put("permanentThana",permanentThana)
        jsonObject.put("postcode",postcode)
        jsonObject.put("sku",sku)
        jsonObject.put("thana",thana)
        jsonObject.put("transectionId",transectionId)
        jsonObject.put("transectionType",transectionType)

        val jsonString:String = jsonObject.toString()

        appUtils.saveDataIntoPreference("ApplyLoan",jsonString)

        return jsonObject

    }


    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HOLDER_PROFILE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                holderProfileImage.setImageBitmap(selectedBitmap)
                holderProfile += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }
                    .toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }


        if (requestCode == HOLDER_FRONT) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                holdernidFront.setImageBitmap(selectedBitmap)
                holderNidFront += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }
                    .toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == HOLDER_BACK) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                holdernidBack.setImageBitmap(selectedBitmap)
                holderNidBack += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }
                    .toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == NOMINEE_FRONT) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                nidFront.setImageBitmap(selectedBitmap)

                nomineeNidFront += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }
                    .toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == NOMINEE_BACK) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                nidBack.setImageBitmap(selectedBitmap)

                nomineeNidBack += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }
                    .toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }


    private  fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap,quality:Int): String {
       if (bitmap == null){
           errorToast("Bitmap error")
       }else{
           val resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
           val byteArrayOutputStream =  ByteArrayOutputStream()
           resized.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
           val byteArray = byteArrayOutputStream .toByteArray()
           val imageString =Base64.encodeToString(byteArray,Base64.DEFAULT)
           return imageString
       }
     return bitmap.toString()
    }


    private fun convertImage(bitmap:Bitmap):String{
        val byteArrayOutputStream =  ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream .toByteArray()
        return Base64.encodeToString(byteArray,Base64.DEFAULT)
    }


    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
        progressDialog.dismiss()
    }


    private fun getDistrictList(){
        progressDialog.show()
        apiService.getDistrictList().enqueue(object : Callback<DistrictResponse> {
            override fun onFailure(call: Call<DistrictResponse>, t: Throwable) {
                progressDialog.hide()
            }

            override fun onResponse(call: Call<DistrictResponse>, response: retrofit2.Response<DistrictResponse>) {
                progressDialog.hide()
                if (response.isSuccessful){
                    try {
                        val districtResponse:DistrictResponse = response.body()!!
                        val districts = districtResponse.payload
                        val holderDistrictAdapter: ArrayAdapter<Payload> =  ArrayAdapter<Payload>(this@ApplyLoanActivity, R.layout.dropdown_menu_popup_item,districts)
                        val deliveryDistrictAdapter: ArrayAdapter<Payload> =  ArrayAdapter<Payload>(this@ApplyLoanActivity, R.layout.dropdown_menu_popup_item,districts)
                        val nomineeDistrictAdapter: ArrayAdapter<Payload> =  ArrayAdapter<Payload>(this@ApplyLoanActivity, R.layout.dropdown_menu_popup_item,districts)

                        holderDistrictSpinner.adapter = holderDistrictAdapter
                        deliveryDistrictSpinner.adapter = deliveryDistrictAdapter
                        permanentDistrictSpinner.adapter = nomineeDistrictAdapter

                        holderDistrictSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                updateHolderThana(districts[position].id)
                            }

                        }

                        deliveryDistrictSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                updateDeliveryThana(districts[position].id)
                            }

                        }
                        permanentDistrictSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                updatePermanentAddressThana(districts[position].id)
                            }

                        }


                    }catch (ex:Exception){

                    }

                }
            }
        })
    }


/*    private fun updateHolderAutoCompleteTextView(){
        val districts = appUtils.getAllDistricts()

        val districtAdapter =  ArrayAdapter(this, R.layout.dropdown_menu_popup_item, districts)
        val districtAutoComplete: AutoCompleteTextView = findViewById(R.id.holderdistricAutoComplete)
        val thanaAutoComplete: AutoCompleteTextView = findViewById(R.id.holderThanaAutoComplete)

        districtAutoComplete.setAdapter(districtAdapter)

        val thanas = filterThana
        val thanaAdapter =  ArrayAdapter(this, R.layout.dropdown_menu_popup_item, thanas)

        thanaAutoComplete.setAdapter(thanaAdapter)
        districtAutoComplete.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            run {
                //successToast( districts[position].id.toString())
                updateThana(getDistrictId(districtAutoComplete.text.toString()))
                thanaAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateDeliveryAutoCompleteTextView(){
        val districts = appUtils.getAllDistricts()

        val districtAdapter =  ArrayAdapter(this, R.layout.dropdown_menu_popup_item, districts)
        val districtAutoComplete: AutoCompleteTextView = findViewById(R.id.deliveryDistricAutoComplete)
        val thanaAutoComplete: AutoCompleteTextView = findViewById(R.id.deliveryThanaAutoComplete)

        districtAutoComplete.setAdapter(districtAdapter)

        val thanas = filterDeliveryThana
        val thanaAdapter =  ArrayAdapter(this, R.layout.dropdown_menu_popup_item, thanas)

        thanaAutoComplete.setAdapter(thanaAdapter)
        districtAutoComplete.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            run {
                updateDeliveryThana(getDistrictId(districtAutoComplete.text.toString()))
                thanaAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateNomineeAutoCompleteTextView(){
        val districts = appUtils.getAllDistricts()

        val districtAdapter =  ArrayAdapter(this, R.layout.dropdown_menu_popup_item, districts)
        val districtAutoComplete: AutoCompleteTextView = findViewById(R.id.permanentDistrictAutoComplete)
        val thanaAutoComplete: AutoCompleteTextView = findViewById(R.id.permanentThanaAutoComplete)

        districtAutoComplete.setAdapter(districtAdapter)

        val thanas = filterNomineeThana
        val thanaAdapter =  ArrayAdapter(this, R.layout.dropdown_menu_popup_item, thanas)

        thanaAutoComplete.setAdapter(thanaAdapter)
        districtAutoComplete.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            run {
                updateNomineeThana(getDistrictId(districtAutoComplete.text.toString()))
                thanaAdapter.notifyDataSetChanged()
            }
        }
    }*/


    private fun updateHolderThana(districtId: Int) {
        apiService.getThanaList(districtId).enqueue(object : Callback<ThanaResponse> {
            override fun onFailure(call: Call<ThanaResponse>, t: Throwable) {
            }
            override fun onResponse(call: Call<ThanaResponse>, response: retrofit2.Response<ThanaResponse>) {
                if (response.isSuccessful){
                    val thanaResponse : ThanaResponse = response.body()!!
                    val thanaPayload = thanaResponse.thanaPayload
                    val holderThanaAdapter: ArrayAdapter<ThanaPayload> =  ArrayAdapter<ThanaPayload>(this@ApplyLoanActivity, R.layout.dropdown_menu_popup_item,thanaPayload)
                    holderThanaSpinner.adapter = holderThanaAdapter
                }
            }
        })
    }


    private fun updateDeliveryThana(districtId: Int) {
        apiService.getThanaList(districtId).enqueue(object : Callback<ThanaResponse> {
            override fun onFailure(call: Call<ThanaResponse>, t: Throwable) {
            }
            override fun onResponse(call: Call<ThanaResponse>, response: retrofit2.Response<ThanaResponse>) {
                if (response.isSuccessful){
                    val thanaResponse : ThanaResponse = response.body()!!
                    val thanaPayload = thanaResponse.thanaPayload
                    val nomineeThanaAdapter: ArrayAdapter<ThanaPayload> =  ArrayAdapter<ThanaPayload>(this@ApplyLoanActivity, R.layout.dropdown_menu_popup_item,thanaPayload)
                    deliveryThanaSpinner.adapter = nomineeThanaAdapter
                }
            }
        })
    }


    private fun updatePermanentAddressThana(districtId: Int) {
        apiService.getThanaList(districtId).enqueue(object : Callback<ThanaResponse> {
            override fun onFailure(call: Call<ThanaResponse>, t: Throwable) {
            }
            override fun onResponse(call: Call<ThanaResponse>, response: retrofit2.Response<ThanaResponse>) {
                if (response.isSuccessful){
                    val thanaResponse : ThanaResponse = response.body()!!
                    val thanaPayload = thanaResponse.thanaPayload
                    val nomineeThanaAdapter: ArrayAdapter<ThanaPayload> =  ArrayAdapter<ThanaPayload>(this@ApplyLoanActivity, R.layout.dropdown_menu_popup_item,thanaPayload)
                    permanentThanaSpinner.adapter = nomineeThanaAdapter
                }
            }
        })
    }


    private fun getDistrictId(district: String):Int{
        val districts = appUtils.getAllDistricts()
        districts.forEach {
            if (it.name.contains(district))
                return it.id
        }
        return 0
    }


    private fun validateLoanApplyRequest(): Boolean {
        if (holderProfile == "data:image/jpeg;base64,") {
            errorToast("Please Upload an Profile Image")
            return false
        }
        if (holderFirstName.text.toString().isEmpty()) {
            holderFirstName.error = "Please Enter Your First Name"
            holderFirstName.requestFocus()
            return false
        }
        if (holderLastName.text.toString().isEmpty()) {
            holderLastName.error = "Please Enter Your Last Name"
            holderLastName.requestFocus()
            return false
        }
        if (holderAddress.text.toString().isEmpty()) {
            holderAddress.error = "Please Enter Your Address"
            holderAddress.requestFocus()
            return false
        }
        if (holderPostCode.text.toString().isEmpty()) {
            holderPostCode.error = "Please Enter Your Post Code"
            holderPostCode.requestFocus()
            return false
        }else if(holderPostCode.text.toString().length < 4){
            holderPostCode.error = "Please Enter Your Post Code Correctly"
            holderPostCode.requestFocus()
            return false
        }
        if (holderNidNo.text.toString().isEmpty()) {
            holderNidNo.error = "Please Enter Your National ID Number"
            holderNidNo.requestFocus()
            return false
        }else if(holderNidNo.text.toString().length < 10){
            holderNidNo.error = "Please Enter Your National ID Number Correctly!"
            holderNidNo.requestFocus()
            return false
        }
        if (holderPermanentAddress.text.toString().isEmpty()) {
            holderPermanentAddress.error = "Please enter your permanent Address"
            holderPermanentAddress.requestFocus()
            return false
        }
        if (holderPermanentPostCode.text.toString().isEmpty()) {
            holderPermanentPostCode.error = "Please enter your permanent Post Code"
            holderPermanentPostCode.requestFocus()
            return false
        }else if (holderPermanentPostCode.text.toString().length < 4) {
            holderPermanentPostCode.error = "Please Enter Your permanent Post Code Correctly!"
            holderPermanentPostCode.requestFocus()
            return false
        }
        if (deliveryAddress.text.toString().isEmpty()) {
            deliveryAddress.error = "Please Enter your Delivery Address"
            deliveryAddress.requestFocus()
            return false
        }
        if (deliveryPostCode.text.toString().isEmpty()) {
            deliveryPostCode.error = "Please Enter Your Delivery Post Code"
            deliveryPostCode.requestFocus()
            return false
        } else if (deliveryPostCode.text.toString().length < 4) {
            deliveryPostCode.error = "Please Enter Your Delivery Post Code Correctly!"
            deliveryPostCode.requestFocus()
            return false
        }
        if (holderNidFront == "data:image/jpeg;base64,") {
            errorToast("Please Upload Loan Holder National ID Card Front Image")
            return false
        }
        if (holderNidBack == "data:image/jpeg;base64,") {
            errorToast("Please Upload Loan Holder National ID Card Back Image")
            return false
        }
        if (nomineeFirstName.text.toString().isEmpty()) {
            nomineeFirstName.error = "Please Enter Secondary Contact Person's First Name"
            nomineeFirstName.requestFocus()
            return false
        }
        if (nomineeLastName.text.toString().isEmpty()) {
            nomineeLastName.error = "Please Enter Secondary Contact Person's Last Name"
            nomineeLastName.requestFocus()
            return false
        }
        if (nomineePhoneNumber.text.toString().isEmpty()) {
            nomineePhoneNumber.error = "Please Enter Secondary Contact Person's Phone Number"
            nomineePhoneNumber.requestFocus()
            return false
        }
        if (nomineeNidNo.text.toString().isEmpty()) {
            nomineeNidNo.error = "Please Enter Secondary Contact Person's National ID Number"
            nomineeNidNo.requestFocus()
            return false
        }else if(nomineeNidNo.text.toString().length < 10){
            nomineeNidNo.error = "Please Enter Secondary Contact Person's National ID Number Correctly!"
            nomineeNidNo.requestFocus()
            return false
        }
        if (nomineeNidFront == "data:image/jpeg;base64,") {
            errorToast("Please Upload Secondary Contact Person's National ID Card Front Image")
            return false
        }
        if (nomineeNidBack == "data:image/jpeg;base64,") {
            errorToast("Please Upload Secondary Contact Person's National ID Card Back Image")
            return false
        }


        return true

    }

}


