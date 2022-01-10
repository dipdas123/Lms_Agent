package tech.redltd.lmsAgent.utils

object LmsUrl {
    private const val BASE_URL="https://lms.robi.com.bd/"
    //login Agent
    const val agent_Login:String = BASE_URL+"api/API_AgentMobile/Agentlogin"

    const val aget_otpRequest:String = BASE_URL+"api/API_AgentMobile/Loan_OTP_Generate"

    const val agent_otpCheck:String = BASE_URL+"api/API_AgentMobile/Loan_OTPCheck"

    const val loginOTPCheck:String = BASE_URL+"api/API_AgentMobile/otpCheck"

    const val aget_updatePassword = BASE_URL+"api/API_AgentMobile/passwordUpdate"


}