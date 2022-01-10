package tech.redltd.lmsAgent.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_update_nominee_profile.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.network.MyProfileResponse
import tech.redltd.lmsAgent.utils.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class UpdateNomineeProfileFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var nomineeProfileImage: ImageView

    private val PROFILE_IMAGE = 111
    private val NID_FRONT = 222
    private val NID_BACK = 333
    private var nomineeProfileImageString:String="data:image/jpeg;base64,"
    private var nomineeNidFrontString:String="data:image/jpeg;base64,"
    private var nomineeNidBackString:String="data:image/jpeg;base64,"

    private val appUtils:AppUtils by inject()

    private val filterNomineeThana: ArrayList<Thana> = ArrayList()

    private val apiService:ApiService by inject()
    lateinit var loading : Dialog

    override fun onDestroy() {
        super.onDestroy()
        loading.dismiss()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_nominee_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nomineeProfileImage = view.findViewById(R.id.nomineeProfileImage)
        buttonNomineeProfile.setOnClickListener {
            fetchApi()
        }
       updateAutoCompleteTextView(view)

        nomineeProfileImage.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(mContext)
            startActivityForResult(intent, PROFILE_IMAGE)
            //CropImage.activity().start(mContext, this)
        }

        nidFront.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(mContext)
            startActivityForResult(intent, NID_FRONT)
        }

        nidBack.setOnClickListener {
            val intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(mContext)
            startActivityForResult(intent, NID_BACK)
        }

        getProfile()

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        loading = context.loadingDialog()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PROFILE_IMAGE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap:Bitmap? = getCapturedImage(resultUri)
                nomineeProfileImage.setImageBitmap(selectedBitmap)
                nomineeProfileImageString += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }.toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == NID_FRONT) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap:Bitmap? = getCapturedImage(resultUri)
                nidFront.setImageBitmap(selectedBitmap)
                nomineeNidFrontString += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }.toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == NID_BACK) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap:Bitmap? = getCapturedImage(resultUri)
                nidBack.setImageBitmap(selectedBitmap)
                nomineeNidBackString += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }.toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT >= 29) {
            val source: ImageDecoder.Source =  ImageDecoder.createSource( mContext.contentResolver, selectedPhotoUri)
            try {
                bitmap = ImageDecoder.decodeBitmap(source)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, selectedPhotoUri
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return bitmap
    }

    private fun fetchApi(){
        loading.show()
        val nomineeFirstName = nomineeFirstNameTv.text.toString()
        val nomineeLastName =nomineeLastNameTv.text.toString()
        val nomineeProfession = nomineeProfessionTv.text.toString()
        val nomineeAddress = nomineeAddressTv.text.toString()
        val nomineeDistrict = districAutoComplete.text.toString()
        val nomineeThana = thanaAutoComplete.text.toString()
        val nomineePostCode = nomineePostCodeTv.text.toString()
        val nomineeEmailAddress = nomineeEmailAddressTv.text.toString()
        val nomineeNidNo = nomineeNidNoTv.text.toString()

        val jsonObject = JsonObject()
        jsonObject.addProperty("nominee_firstname",nomineeFirstName)
        jsonObject.addProperty("nominee_lastname",nomineeLastName)
        jsonObject.addProperty("nominee_nid_no",nomineeNidNo)
        jsonObject.addProperty("nominee_profession",nomineeProfession)
        jsonObject.addProperty("nominee_address",nomineeAddress)
        jsonObject.addProperty("nominee_district",nomineeDistrict)
        jsonObject.addProperty("nominee_image",nomineeProfileImageString)
        jsonObject.addProperty("nominee_email",nomineeEmailAddress)
        jsonObject.addProperty("nominee_nid_image_front",nomineeNidFrontString)
        jsonObject.addProperty("nominee_nid_image_back",nomineeNidBackString)
        jsonObject.addProperty("nominee_postcode",nomineePostCode)
        jsonObject.addProperty("nominee_thana",nomineeThana)

        apiService.updateUserProfile(jsonObject).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                loading.hide()
            }

            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                loading.hide()
                if (response.isSuccessful){
                    mContext.successToast("Profile Update Successfully")
                }

            }
        })

    }


    private  fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap,quality:Int): String {
        if (bitmap == null){
            mContext.errorToast("Bitmap error")
        }else{
            val resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
            val byteArrayOutputStream =  ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream .toByteArray()
            val imageString = Base64.encodeToString(byteArray, Base64.DEFAULT)
            return imageString
        }
        return bitmap.toString()
    }


    private fun updateAutoCompleteTextView(view: View){
        val districts = appUtils.getAllDistricts()

        val districtAdapter =  ArrayAdapter(mContext, R.layout.dropdown_menu_popup_item, districts)
        val districtAutoComplete: AutoCompleteTextView = view.findViewById(R.id.districAutoComplete)

        val thanaAutoComplete: AutoCompleteTextView = view.findViewById(R.id.thanaAutoComplete)

        districtAutoComplete.setAdapter(districtAdapter)

        val thanas = filterNomineeThana
        val thanaAdapter =  ArrayAdapter(mContext, R.layout.dropdown_menu_popup_item, thanas)

        thanaAutoComplete.setAdapter(thanaAdapter)
        districtAutoComplete.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            run {
                updateThana(getDistrictId(districtAutoComplete.text.toString()))
                thanaAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateThana(districtId: Int): List<Thana> {
        val thanas = appUtils.getAllThana()
        filterNomineeThana.clear()
        thanas.forEach {
            if (it.districtid == districtId) {
                filterNomineeThana.add(it)
            }
        }
        return filterNomineeThana

    }


    private fun getDistrictId(district: String):Int{
        val districts = appUtils.getAllDistricts()
        districts.forEach {
            if (it.name.contains(district))
                return it.id
        }
        return 0
    }

    private fun getProfile(){
        apiService.getMyProfile().enqueue(object : Callback<MyProfileResponse> {
            override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<MyProfileResponse>,
                response: Response<MyProfileResponse>
            ) {
                if (response.isSuccessful){
                    response.body().let {
                        val payload = it?.payload
                        if (payload != null){
                            try {
                                nomineeFirstNameLayout.hint = payload.nomineeFirstname
                                nomineeLastNameLayout.hint = payload.nomineeLastname
                                nomineeProfessionLayout.hint = payload.nomineeProfession
                                nomineeAddressLayout.hint = payload.nomineeAddress
                                nomineeDistrictLayout.hint = payload.nomineeDistrict
                                nomineeThanaLayout.hint = payload.nomineeThana
                                nomineePostCodeLayout.hint = payload.nomineePostcode
                                nomineeEmailLayout.hint = payload.nomineeEmail
                                nomineeNidLayout.hint = payload.nomineeNidNo
                                Glide.with(mContext).load(payload.nomineeImage).into(nomineeProfileImage)
                                Glide.with(mContext).load(payload.nomineeNidImageFront).into(nidFront)
                                Glide.with(mContext).load(payload.nomineeNidImageBack).into(nidBack)

                            }catch (ex:Exception){

                            }
                        }
                    }

                }
            }
        })
    }


}
