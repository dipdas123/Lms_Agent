package tech.redltd.lmsAgent.activities.sales_report;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoricalDataResponse {

    @SerializedName("wis_sales_report")
    @Expose
    private WisSalesReport wisSalesReport;
    @SerializedName("apiVersion")
    @Expose
    private String apiVersion;

    public WisSalesReport getWisSalesReport() {
        return wisSalesReport;
    }

    public void setWisSalesReport(WisSalesReport wisSalesReport) {
        this.wisSalesReport = wisSalesReport;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

}

class WisSalesReport {

    @SerializedName("sales_details")
    @Expose
    private SalesDetails salesDetails;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMessage")
    @Expose
    private String responseMessage;

    public SalesDetails getSalesDetails() {
        return salesDetails;
    }

    public void setSalesDetails(SalesDetails salesDetails) {
        this.salesDetails = salesDetails;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

}

class SalesDetails {

    @SerializedName("wic")
    @Expose
    private String wic;
    @SerializedName("sales")
    @Expose
    private List<SalesReport> sales = null;
    @SerializedName("total_sales")
    @Expose
    private Integer totalSales;
    @SerializedName("total_sales_amount")
    @Expose
    private Integer totalSalesAmount;

    public String getWic() {
        return wic;
    }

    public void setWic(String wic) {
        this.wic = wic;
    }

    public List<SalesReport> getSales() {
        return sales;
    }

    public void setSales(List<SalesReport> sales) {
        this.sales = sales;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public Integer getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(Integer totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

}



