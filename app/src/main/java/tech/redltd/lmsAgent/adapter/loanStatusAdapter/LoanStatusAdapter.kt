package tech.redltd.lmsAgent.adapter.loanStatusAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loanList.Loan

class LoanStatusAdapter : RecyclerView.Adapter<LoanStatusAdapter.ViewHolder>() {
    private var loanList: List<Loan> = listOf()
    private lateinit var mContext: Context

    fun setLoanList(loanLists:List<Loan>){
        loanList = loanLists
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loanDate :TextView = itemView.findViewById(R.id.loan_date)
        val loanDurationMonth :TextView = itemView.findViewById(R.id.loanDurationMonth)
        val loanDueAmount :TextView = itemView.findViewById(R.id.loanDueAmount)
        val loanEmi :TextView = itemView.findViewById(R.id.loanEmi)
        val statusLoanAmount :TextView = itemView.findViewById(R.id.statusLoanAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.loan_status_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return loanList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loan:Loan = loanList[position]
        holder.loanDate.text = loan.loandate
        holder.loanDurationMonth.text = loan.loanduration.toString()
        holder.loanDueAmount.text = loan.remainingbalance.toString()
        holder.loanEmi.text = loan.nextpaymentamount.toString()
        holder.statusLoanAmount.text = loan.loanamount.toString()

        /*holder.itemView.setOnClickListener {
            if (loan.loanstatusId != 0){
                val intent = Intent(mContext, InstallmentCheckActivity::class.java)
                intent.putExtra("loanid",loan.loanid)
                intent.putExtra("nextpaymentamount",loan.nextpaymentamount)
                intent.putExtra("loanduration",loan.loanduration)
                intent.putExtra("loandate",loan.loandate)
                intent.putExtra("customerid",loan.customerid)
                intent.putExtra("loanamount",loan.loanamount)
                mContext.startActivity(intent)
            }else{
                mContext.errorToast("Loan Under Review Please Wait for Approval")
            }

        }*/



    }
}