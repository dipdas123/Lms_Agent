package tech.redltd.lmsAgent.activities.productDetailsActivity

import com.google.gson.annotations.SerializedName

data class ProductSpecificationResponse(
    @SerializedName("deviceSpecs")
    val deviceSpecs: List<deviceSpecs>

)

data class deviceSpecs(
    @SerializedName("androidversion")
    val androidversion: String,
    @SerializedName("display")
    val display: String,
    @SerializedName("memory")
    val memory: String,
    @SerializedName("camera")
    val camera: String,
    @SerializedName("battery")
    val battery: String,
    @SerializedName("detailslink")
    val detailslink: String,
    @SerializedName("processor")
    val processor: String
)



