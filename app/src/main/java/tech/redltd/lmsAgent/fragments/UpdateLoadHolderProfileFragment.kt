package tech.redltd.lmsAgent.fragments

import android.app.Activity
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
import kotlinx.android.synthetic.main.fragment_update_load_holder_profile.*
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


class UpdateLoadHolderProfileFragment : Fragment() {
    lateinit var holderProfileImage: ImageView
    private val PROFILE_IMAGE = 111
    private val NID_FRONT = 222
    private val NID_BACK = 333
    private var userProfileImageString:String = "data:image/jpeg;base64,"
    private var userNidFrontImageString:String = "data:image/jpeg;base64,"
    private var userNidBackImageString:String = "data:image/jpeg;base64,"

    private val filterThana: ArrayList<Thana> = ArrayList()

    private val appUtils:AppUtils by inject()



    private val apiService:ApiService by inject()

    lateinit var loading:Dialog

    private lateinit var mContext : Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        loading = context.loadingDialog()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_load_holder_profile, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        loading.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateAutoCompleteTextView(view)
        holderProfileImage = view.findViewById(R.id.holderProfileImage)

        holderProfileImage.setOnClickListener {
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

        buttonUpdateHolder.setOnClickListener {
            fetchApi()
        }

        getProfile()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PROFILE_IMAGE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                holderProfileImage.setImageBitmap(selectedBitmap)
                userProfileImageString += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }.toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == NID_FRONT) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                nidFront.setImageBitmap(selectedBitmap)
                userNidFrontImageString += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }.toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == NID_BACK) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val selectedBitmap: Bitmap? = getCapturedImage(resultUri)
                nidBack.setImageBitmap(selectedBitmap)
                userNidBackImageString += selectedBitmap?.let { getEncoded64ImageStringFromBitmap(it, 70) }.toString()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }


   /* private fun updateAutoCompleteTextView(view: View){
        val districts = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")
        val districtAdapter =  ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, districts)
        val districtAutoComplete: AutoCompleteTextView = view.findViewById(R.id.districAutoComplete)
        districtAutoComplete.setAdapter(districtAdapter)
        districtAutoComplete.setText("Magura")


        val thanas = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")
        val thanaAdapter =  ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, thanas)
        val thanaAutoComplete: AutoCompleteTextView = view.findViewById(R.id.thanaAutoComplete)
        thanaAutoComplete.setAdapter(thanaAdapter)
    }*/

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
        val holderFirstName = holderFirstNameTv.text.toString()
        val holderLastName = holderLastNameTv.text.toString()
        val holderProfession = holderProfessionTv.text.toString()
        val holderAddress = holderAddressTv.text.toString()
        val holderDistrict = districAutoComplete.text.toString()
        val holderThana = thanaAutoComplete.text.toString()
        val holderPostCode = holderPostCodeTv.text.toString()
        val holderEmailAddress = holderEmailAddressTv.text.toString()
        val holderNidNo = holderNidNoTv.text.toString()

        val jsonObject = JsonObject()
        jsonObject.addProperty("firstname",holderFirstName)
        jsonObject.addProperty("lastname",holderLastName)
        jsonObject.addProperty("national_id",holderNidNo)
        jsonObject.addProperty("profession",holderProfession)
        jsonObject.addProperty("address",holderAddress)
        jsonObject.addProperty("city",holderDistrict)
        jsonObject.addProperty("cus_image",userProfileImageString)
        jsonObject.addProperty("email",holderEmailAddress)
        jsonObject.addProperty("nid_image_front",userNidFrontImageString)
        jsonObject.addProperty("nid_image_back",userNidBackImageString)
        jsonObject.addProperty("postcode",holderPostCode)
        jsonObject.addProperty("present_thana",holderThana)


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

        val thanas = filterThana
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
        filterThana.clear()
        thanas.forEach {
            if (it.districtid == districtId) {
                filterThana.add(it)
            }
        }
        return filterThana

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
        loading.show()
        apiService.getMyProfile().enqueue(object : Callback<MyProfileResponse> {
            override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
            loading.hide()
            }

            override fun onResponse(
                call: Call<MyProfileResponse>,
                response: Response<MyProfileResponse>
            ) {
                loading.hide()
                if (response.isSuccessful){
                    response.body().let {
                        val payload = it?.payload
                        if (payload != null){
                            try {
                                holderFirstNameLayout.hint = payload.firstname
                                holderLastNameLayout.hint = payload.lastname
                                holderProfessionLayout.hint = payload.profession
                                holderAddressLayout.hint = payload.address
                                holderDistrictLayout.hint = payload.city
                                holderThanaLayout.hint = payload.presentThana
                                holderPostCodeLayout.hint = payload.postcode
                                holderEmailLayout.hint = payload.email
                                holderNidLayout.hint = payload.nationalId

                                Glide.with(mContext).load(payload.cusImage).into(holderProfileImage)
                                Glide.with(mContext).load(payload.nidImageFront).into(nidFront)
                                Glide.with(mContext).load(payload.nidImageBack).into(nidBack)
                            }catch (ex:Exception){

                            }

                        }
                    }

                }
            }
        })
    }



}
