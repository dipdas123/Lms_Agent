package tech.redltd.lmsAgent.utils

object CommonUrl {
   const val BASE_URL:String="https://lms.robi.com.bd/api/customer/"

   const val ASP_BASE_URL:String="https://lms.robi.com.bd/"

   //Loan Query
   const val LOAN_QUERY:String = ASP_BASE_URL+"api/API_AgentMobile/LoanQuery"

   //Single Product
   const val SingleProduct:String = ASP_BASE_URL+"api/API_AgentMobile/robishopSingleProductFetch"

   //loan Credential Check
   const val loanCredentila:String = ASP_BASE_URL+"api/API_AgentMobile/loanCredentila_check"

   const val bkashUrl:String = "http://lms.robi.com.bd/getway/bkash_payment_getway?amount="

   const val loanSubmit:String = ASP_BASE_URL+"api/API_AgentMobile/LoanSubmissionSet"

    const val loanList:String = ASP_BASE_URL+"api/API_AgentMobile/LoanlistList"

    const val loanDetails:String = ASP_BASE_URL+"api/API_AgentMobile/LoanDetails"

    const val EmiSubmissionSet:String = ASP_BASE_URL+"api/API_AgentMobile/EmiSubmissionSet"

    const val generateOTPAndSend:String = ASP_BASE_URL+"api/API_AgentMobile/Loan_OTP_Generate"

    const val loanOtpCheck:String = ASP_BASE_URL+"api/API_AgentMobile/Loan_OTPCheck"


}