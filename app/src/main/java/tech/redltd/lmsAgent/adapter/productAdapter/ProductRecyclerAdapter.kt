package tech.redltd.lmsAgent.adapter.productAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.network.lmsDto.Product
import tech.redltd.lmsAgent.utils.CommonConstant

class ProductRecyclerAdapter : RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder>() {
    lateinit var mContext:Context
    private lateinit var productOnClick: ProductOnClick
    private var productList = ArrayList<Product>()
    fun setProducts(products:List<Product>){
        productList = products as ArrayList<Product>
    }

    fun setProductOnClick(productOnClick: ProductOnClick){
        this.productOnClick = productOnClick
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage:ImageView = itemView.findViewById(R.id.productImage)
        val productName :TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view:View = LayoutInflater.from(mContext).inflate(R.layout.product_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product:Product = productList[position]

        Glide.with(holder.itemView).load(product.baseImageUrl).into(holder.productImage)
        holder.productName.text = product.name
        holder.productPrice.text = "Tk. ${product.price}"

        holder.itemView.setOnClickListener {
            productOnClick.onProductClick(product.sku)
            CommonConstant.PRODUCT_PRICE = product.price
        }

    }
}