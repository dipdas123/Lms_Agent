package tech.redltd.lmsAgent.activities.createAccount

data class CreateAccountBody(
    val first_name:String,
    val last_name:String,
    val phone:String,
    val password:String,
    val email:String,
    val date_of_birth:String,
    val nid:String,
    val image:String?
)

data class CreateAccountResponse(val success:Boolean,val message:String)

data class RegisterErrorBody(val success:Boolean,val errors:String,val message:String)