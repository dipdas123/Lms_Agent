package tech.redltd.lmsAgent.network.thana


import com.google.gson.annotations.SerializedName

data class ThanaPayload(
    @SerializedName("districtid")
    val districtid: Int,
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