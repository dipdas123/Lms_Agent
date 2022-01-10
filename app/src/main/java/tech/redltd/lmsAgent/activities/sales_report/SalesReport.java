package tech.redltd.lmsAgent.activities.sales_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalesReport {

    @SerializedName("loanid")
    @Expose
    private Integer loanid;
    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("downpayment")
    @Expose
    private Integer downpayment;
    @SerializedName("loandate")
    @Expose
    private String loandate;

    public Integer getLoanid() {
        return loanid;
    }

    public void setLoanid(Integer loanid) {
        this.loanid = loanid;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Integer getDownpayment() {
        return downpayment;
    }

    public void setDownpayment(Integer downpayment) {
        this.downpayment = downpayment;
    }

    public String getLoandate() {
        return loandate;
    }

    public void setLoandate(String loandate) {
        this.loandate = loandate;
    }

}
