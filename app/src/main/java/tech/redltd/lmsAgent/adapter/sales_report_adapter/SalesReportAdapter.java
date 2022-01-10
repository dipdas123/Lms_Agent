package tech.redltd.lmsAgent.adapter.sales_report_adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.redltd.lmsAgent.R;
import tech.redltd.lmsAgent.activities.sales_report.SalesReport;
import tech.redltd.lmsAgent.utils.CommonConstant;


public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.DataViewHolder>{
    private Context context;
    private List<SalesReport> salesReports;

    public SalesReportAdapter(Context context, List<SalesReport> salesReports) {
        this.context = context;
        this.salesReports = salesReports;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.sales_report_recycler_items,parent,false);
        final DataViewHolder viewHolder = new DataViewHolder(view);
        return viewHolder ;
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(DataViewHolder holder, int position) {
        SalesReport orderList = salesReports.get(position);

        holder.transactionDate.setText(orderList.getLoandate());
        holder.loanDevice.setText(orderList.getDevice());
        holder.loanID.setText(orderList.getLoanid().toString());
        holder.downPayment.setText(CommonConstant.TAKA_STRING +orderList.getDownpayment().toString());

    }

    @Override
    public int getItemCount() {
        return salesReports.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder{
        private TextView transactionDate;
        private TextView loanDevice;
        private TextView loanID;
        private TextView downPayment;



        public  DataViewHolder(View itemView){
            super(itemView);

            transactionDate = (TextView)itemView.findViewById(R.id.loan_date);
            loanDevice = (TextView)itemView.findViewById(R.id.loanDevice);
            loanID = (TextView)itemView.findViewById(R.id.loanID);
            downPayment = (TextView)itemView.findViewById(R.id.downPayment);

        }
    }


}
