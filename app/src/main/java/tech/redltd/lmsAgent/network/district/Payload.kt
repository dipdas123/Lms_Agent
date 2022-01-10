package tech.redltd.lmsAgent.network.district


import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("division_id")
    val divisionId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("localname")
    val localname: String,
    @SerializedName("name")
    val name: String
){
    override fun toString(): String {
        return name
    }
}