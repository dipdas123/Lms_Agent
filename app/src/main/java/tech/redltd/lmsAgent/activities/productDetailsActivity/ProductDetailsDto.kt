package tech.redltd.lmsAgent.activities.productDetailsActivity

import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    @SerializedName("robishopContent")
    val RobishopContent : RobishopContent
)

data class RobishopContent (
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("error")
    val error: Error,
    @SerializedName("response_code")
    val responseCode: Int
)


data class Product(
    @SerializedName("additional_images")
    val additionalImages: List<AdditionalImage>,
    @SerializedName("attributes_info")
    val attributesInfo: List<AttributesInfo>,
    @SerializedName("base_image_url")
    val baseImageUrl: String,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("product_url")
    val productUrl: String,
    @SerializedName("product_warranty")
    val productWarranty: String,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("weight")
    val weight: Double,
    @SerializedName("weight_unit")
    val weightUnit: String
)


data class Error(
    @SerializedName("error")
    val error: Boolean
)


data class AttributesInfo(
    @SerializedName("label")
    val label: String,
    @SerializedName("value")
    val value: String
)


data class AdditionalImage(
    @SerializedName("url")
    val url: String
)