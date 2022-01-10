package tech.redltd.lmsAgent.network.aspDto

data class LoanOtpBody(val mobile:String,val loanOTP:String)

data class LoanOtpResponse(val isSuccess:Boolean,val isstatus:String,val apiVersion:String)

data class LoanOtpErrorBody(val isSuccess:Boolean,val isstatus:String,val apiVersion:String)