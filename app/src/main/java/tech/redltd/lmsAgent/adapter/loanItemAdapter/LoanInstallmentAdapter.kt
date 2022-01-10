package tech.redltd.lmsAgent.adapter.loanItemAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.installmentCheck.Emidetail

class LoanInstallmentAdapter : RecyclerView.Adapter<LoanInstallmentAdapter.ViewHolder>() {
    private var emidetailList : List<Emidetail> = listOf()
    private lateinit var mContext : Context

    fun setEmiDetails(emidetails: List<Emidetail>){
        this.emidetailList = emidetails
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loanStatusText : TextView = itemView.findViewById(R.id.loanStatusText)
        val loanAmount : TextView = itemView.findViewById(R.id.loanAmount)
        val lastDate : TextView = itemView.findViewById(R.id.lastDate)
        val emiItemLinear : LinearLayout = itemView.findViewById(R.id.emiItemLinear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
       val view:View = LayoutInflater.from(parent.context).inflate(R.layout.loan_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return emidetailList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emilDetails:Emidetail = emidetailList[position]
        holder.lastDate.text = emilDetails.date
        holder.loanAmount.text = emilDetails.amount.toString()
        if (emilDetails.isPaid){
            holder.loanStatusText.text = "Paid"
            holder.emiItemLinear.background = mContext.getDrawable(R.drawable.green_rounded)
        }else{
            holder.loanStatusText.text = "Upcoming"
            holder.emiItemLinear.background = mContext.getDrawable(R.drawable.red_rounded)
        }
    }
}