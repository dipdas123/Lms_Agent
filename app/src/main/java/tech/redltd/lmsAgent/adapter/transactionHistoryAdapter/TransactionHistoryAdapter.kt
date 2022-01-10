package tech.redltd.lmsCustomer.adapter.transactionHistoryAdapter;

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.transaction_history.Txn_history


class TransactionHistoryAdapter : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
        lateinit var mContext: Context
        private var transactionList = ArrayList<Txn_history>()


        fun setTransaction(transactions: List<Txn_history>) {
                transactionList = transactions as ArrayList<Txn_history>
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val tnxID: TextView = itemView.findViewById(R.id.tnxID)
                val tnxDate: TextView = itemView.findViewById(R.id.tnxDate)
                val tnxAmount: TextView = itemView.findViewById(R.id.tnxAmount)
                val tnxType: TextView = itemView.findViewById(R.id.tnxType)
                val tnxStatus: TextView = itemView.findViewById(R.id.tnxStatus)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                mContext = parent.context
                val view: View = LayoutInflater.from(mContext).inflate(R.layout.transaction_adapter_item, parent, false)
                return ViewHolder(view)
        }

        override fun getItemCount(): Int {
                return transactionList.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val txnHistory: Txn_history = transactionList[position]

                holder.tnxID.text = txnHistory.transaction_id.toString()
                holder.tnxDate.text = txnHistory.payment_date.toString()
                holder.tnxAmount.text = txnHistory.payment_amount.toString()
                holder.tnxType.text = txnHistory.transection_type.toString()
                holder.tnxStatus.text = txnHistory.transaction_status.toString()
        }


}