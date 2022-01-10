package tech.redltd.lmsAgent.activities.changePasswordActivity


data class ChangePasswordResponse(val success:Boolean,val payload:Any,val message:String)

data class LoanConfirmOTPResponse(val isSuccess:Boolean,val isstatus:String,val apiVersion:String)