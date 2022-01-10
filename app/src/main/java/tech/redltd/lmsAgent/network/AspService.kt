package tech.redltd.lmsAgent.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import tech.redltd.lmsAgent.activities.transaction_history.TransactionHistoryResponse
import tech.redltd.lmsAgent.activities.transaction_history.TransactionHistoryResponsePostCall
import tech.redltd.lmsAgent.network.aspDto.*
import tech.redltd.lmsAgent.utils.CommonUrl


interface AspService {
    @POST("api/API_AgentMobile/LoanQuery")
    fun loanQuery(@Body loanQueryRequest: LoanQueryRequest):Call<ResponseBody>

    @POST("api/API_AgentMobile/loanCredentila_check")
    fun loanCredentialCheck(@Body loanCredentialCheckRequest: LoanCredentialCheckRequest):Call<LoanQueryResponse>

    @POST("api/API_AgentMobile/Loan_OTPCheck")
    fun loanOtpCheck(@Body loanOtpBody: LoanOtpBody):Call<LoanOtpResponse>

    @POST("api/API_AgentMobile/txnhistory")
    fun getTransactionHistory(@Body transactionHistoryResponsePostCall : TransactionHistoryResponsePostCall):Call<TransactionHistoryResponse>




    companion object{
        operator fun invoke():AspService{
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(AspInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(CommonUrl.ASP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(AspService::class.java)
        }
    }
}