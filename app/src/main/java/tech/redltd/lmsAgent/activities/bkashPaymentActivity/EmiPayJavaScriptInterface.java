package tech.redltd.lmsAgent.activities.bkashPaymentActivity;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import tech.redltd.lmsAgent.activities.emiPaySuccessActivity.EmiPaySuccessActivity;

public class EmiPayJavaScriptInterface {
    Context mContext;
    private BkashOnlickInterface bkashOnlickInterface;


    public void setBkashOnlickInterface(BkashOnlickInterface bkashOnlickInterface){
        this.bkashOnlickInterface = bkashOnlickInterface;
    }

    public EmiPayJavaScriptInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void OnPaymentSuccess(String data) {

        //filtering received data coming from bkash end point

        String[] paymentData = data.split("&");
        String paymentID = paymentData[0].trim().replace("PaymentID= ", "").trim();
        String transactionID = paymentData[1].trim().replace("TransactionID= ", "").trim();
        String amount = paymentData[2].trim().replace("Amount= ", "").trim();

        //payment successful.  Go to another activity and save order data to database
        Intent intent = new Intent(mContext, EmiPaySuccessActivity.class);
        intent.putExtra("TRANSACTION_ID", transactionID);
        intent.putExtra("PAID_AMOUNT", amount);
        intent.putExtra("PaymentID",paymentID);
        intent.putExtra("PAYMENT_SERIALIZE", data);
        mContext.startActivity(intent);
        bkashOnlickInterface.finishActivity();
    }

    @JavascriptInterface
    public void OnPaymentError(Object data) {

        //filtering received data coming from bkash end point


        //payment successful.  Go to another activity and save order data to database
        Intent intent = new Intent(mContext, BkashPaymentActivity.class);
        mContext.startActivity(intent);
        bkashOnlickInterface.finishActivity();

    }


}
