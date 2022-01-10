package tech.redltd.lmsAgent.network

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tech.redltd.lmsAgent.activities.changePasswordActivity.ChangePasswordResponse
import tech.redltd.lmsAgent.activities.createAccount.CreateAccountBody
import tech.redltd.lmsAgent.activities.createAccount.CreateAccountResponse
import tech.redltd.lmsAgent.activities.loginActivity.LoginBody
import tech.redltd.lmsAgent.activities.loginActivity.LoginResponse
import tech.redltd.lmsAgent.activities.otpActivity.OtpBody
import tech.redltd.lmsAgent.activities.otpActivity.OtpResponse
import tech.redltd.lmsAgent.fragments.UpdateProfileResponse
import tech.redltd.lmsAgent.network.district.DistrictResponse
import tech.redltd.lmsAgent.network.lmsDto.RobiShopPorductRequest
import tech.redltd.lmsAgent.network.lmsDto.RobiShopProductResponse
import tech.redltd.lmsAgent.network.thana.ThanaResponse
import tech.redltd.lmsAgent.utils.CommonUrl
import java.util.concurrent.TimeUnit

interface ApiService {

    @POST("api/auth/customerRegistration")
     fun customerRegistration(@Body createAccountBody: CreateAccountBody):Call<CreateAccountResponse>

    @POST("api/auth/customerLogin")
     fun customerLogin(@Body loginBody: LoginBody):Call<LoginResponse>

    @POST("api/auth/customerAuthOtpChecked")
    fun customerOtp(@Body otpBody: OtpBody):Call<OtpResponse>

    @POST("api/getrobishopproducts")
    fun robiShopProducts(@Body rabiShopProductRequest: RobiShopPorductRequest):Call<RobiShopProductResponse>

    @GET("api/getMyprofileInfo")
    fun getMyProfile():Call<MyProfileResponse>

    @POST("api/updateProfile")
    fun updateUserProfile(@Body updateProfileBody: JsonObject):Call<UpdateProfileResponse>

    @POST("api/ChangePassword")
    fun changePassword(@Body jsonObject: JsonObject):Call<ChangePasswordResponse>

    @GET("api/getDistricts")
    fun getDistrictList():Call<DistrictResponse>

    @GET("api/getThana")
    fun getThanaList(@Query("district_id")district_id:Int ):Call<ThanaResponse>







    companion object{
        operator fun invoke():ApiService{
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = Level.BODY
            val okHttpClient:OkHttpClient = OkHttpClient()
                .newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl(CommonUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ApiService::class.java)
        }
    }
}