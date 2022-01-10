package tech.redltd.lmsAgent.activities.sales_report;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.redltd.lmsAgent.R;
import tech.redltd.lmsAgent.adapter.sales_report_adapter.SalesReportAdapter;
import tech.redltd.lmsAgent.utils.AppUtils;
import tech.redltd.lmsAgent.utils.CommonConstant;

public class AgentSalesReportActivity extends AppCompatActivity {
    private LineChart lineChart;

    public static final String BASE_URL = "https://lms.robi.com.bd/";
    private ApiEndPointsForReports apiServices;
    ProgressBar progressBar;
    Button toolbarBackButton;
    Spinner reportTypeSpinner;
    String selectedReportTypeSpinner;
    SalesReportAdapter salesReportAdapter;
    RecyclerView recyclerView;
    List<SalesReport> salesReportList;
    ArrayList<Entry> pricesHigh = new ArrayList<>();
    ArrayList<Entry> pricesHighCurrentReport = new ArrayList<>();
    TextView totalSales, totalAmount;
    List<String> saleDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_sales_report);
        lineChart = findViewById(R.id.activity_main_linechart);
        reportTypeSpinner = findViewById(R.id.reportTypeSpinner);
        progressBar = findViewById(R.id.progressBar);
        totalSales = findViewById(R.id.totalSales);
        totalAmount = findViewById(R.id.totalAmount);

        recyclerView = findViewById(R.id.recyclerSalesRecords);
        @SuppressLint("WrongConstant")
        LinearLayoutManager manager = new LinearLayoutManager(AgentSalesReportActivity.this, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(manager);


        toolbarBackButton = findViewById(R.id.toolbarBackButton);
        toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        AppUtils appUtils = new AppUtils(AgentSalesReportActivity.this);
        String  agent_ID = appUtils.getDataFromPreference(CommonConstant.AGENT_ID);


        String[] selectReportTypeListRoot = new String[]{"All Reports", "Today's Report"};
        final List<String> selectReportTypeList = new ArrayList<>(Arrays.asList(selectReportTypeListRoot));
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AgentSalesReportActivity.this,R.layout.support_simple_spinner_dropdown_item, selectReportTypeList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        reportTypeSpinner.setAdapter(spinnerArrayAdapter);

        reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedReportTypeSpinner =   reportTypeSpinner.getItemAtPosition(reportTypeSpinner.getSelectedItemPosition()).toString();

                if (selectedReportTypeSpinner.equals("All Reports")) {
                    getSalesReportApiCallForAllReport(agent_ID);
                }else {
                    getSalesReportApiCallForCurrentReport(agent_ID);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });


        configureLineChart();
        setupApi();
        getSalesReportApiCallForAllReport(agent_ID);

    }


    private void setupApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .build();

        apiServices = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiEndPointsForReports.class);
    }


    private void configureLineChart() {
        Description desc = new Description();
        desc.setText("Sales Report Graph");
        desc.setTextSize(10);
        lineChart.setDescription(desc);
        lineChart.animateXY(3000, 4000, Easing.EaseInOutBounce, Easing.EaseInExpo);
        lineChart.setDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(true);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(getApplicationContext(), ""+e.getX()+" Tk." , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(0);
        xAxis.setGridLineWidth(0);
        xAxis.setGridColor(Color.WHITE);

        YAxis yAxisL = lineChart.getAxisLeft();
        yAxisL.setDrawLabels(false);
        yAxisL.setAxisLineColor(Color.WHITE);
        yAxisL.setAxisLineWidth(0);
        yAxisL.setGridLineWidth(0);
        yAxisL.setGridColor(Color.WHITE);

        YAxis yAxisR = lineChart.getAxisRight();
        yAxisR.setDrawLabels(false);
        yAxisR.setAxisLineColor(Color.WHITE);
        yAxisR.setAxisLineWidth(0);
        yAxisR.setGridLineWidth(0);
        yAxisR.setGridColor(Color.WHITE);


/*            xAxis.setValueFormatter(new ValueFormatter() {

                @Override
                public String getFormattedValue(float value) {
                    return ""+saleDate;
                }
            });*/


    }


    private void getSalesReportApiCallForAllReport(String agent_ID){
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("agentid", agent_ID);
        jsonObject.put("is_getall", true);

        apiServices.getHistoricalData(jsonObject).enqueue(new Callback<HistoricalDataResponse>() {
            @SuppressLint({"SetTextI18n", "CheckResult"})
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<HistoricalDataResponse> call, Response<HistoricalDataResponse> response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    assert response.body() != null;
                    Toasty.success(AgentSalesReportActivity.this,""+response.body().getWisSalesReport().getResponseMessage());

                    if (response.isSuccessful()) {

                        if (response.body() != null) {
                            HistoricalDataResponse historicalDataResponse;
                            historicalDataResponse = response.body();
                            WisSalesReport wisSalesReport = historicalDataResponse.getWisSalesReport();
                            SalesDetails salesDetails = wisSalesReport.getSalesDetails();


                            if (historicalDataResponse.getWisSalesReport().getSalesDetails() != null) {
                                totalSales.setText(historicalDataResponse.getWisSalesReport().getSalesDetails().getTotalSales().toString());
                                totalAmount.setText(CommonConstant.TAKA_STRING_FRONTSPACE+historicalDataResponse.getWisSalesReport().getSalesDetails().getTotalSalesAmount().toString());


                                for (int i = 0; i < salesDetails.getSales().size(); i++) {
                                    float x = salesDetails.getSales().get(i).getDownpayment();
                                    float y = salesDetails.getSales().get(i).getLoanid();
                                    saleDate = Collections.singletonList(salesDetails.getSales().get(i).getLoandate());

                                    if (y != 0f) {
                                        pricesHigh.add(new Entry(x, salesDetails.getSales().get(i).getDownpayment()));
                                    }else {
                                        pricesHigh.clear();
                                    }
                                }
                            }else {
                                recyclerView.setAdapter(null);
                                salesReportList.clear();
                                pricesHigh.clear();
                                pricesHighCurrentReport.clear();
                                setLineChartData(null);
                                setLineChartDataCurrentReport(null);
                                totalSales.setText("");
                                totalAmount.setText("");
                                Toasty.success(AgentSalesReportActivity.this,""+historicalDataResponse.getWisSalesReport().getResponseMessage());
                            }

                            salesReportList = salesDetails.getSales();
                            if (salesReportList.size() > 0) {
                                recyclerView.setAdapter(new SalesReportAdapter(AgentSalesReportActivity.this, salesReportList));
                            }


                            Comparator<Entry> comparator = new Comparator<Entry>() {
                                @Override
                                public int compare(Entry o1, Entry o2) {
                                    return Float.compare(o1.getX(), o2.getX());
                                }
                            };
                            pricesHigh.sort(comparator);
                            setLineChartData(pricesHigh);

                        }else {
                            Toasty.success(AgentSalesReportActivity.this, "Response Body: Response Body Null !!");
                        }
                    } else {
                        Toasty.success(AgentSalesReportActivity.this, "isSuccessful: Response Not Successful !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @SuppressLint("CheckResult")
            @Override
            public void onFailure(Call<HistoricalDataResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toasty.success(AgentSalesReportActivity.this, "onFailure: Server Error !!!");
            }
        });
    }


    private void getSalesReportApiCallForCurrentReport(String agent_ID){
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("agentid", agent_ID);
        jsonObject.put("is_getall", false);

        apiServices.getHistoricalData(jsonObject).enqueue(new Callback<HistoricalDataResponse>() {
            @SuppressLint({"SetTextI18n", "CheckResult"})
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<HistoricalDataResponse> call, Response<HistoricalDataResponse> response) {
                try {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            HistoricalDataResponse historicalDataResponse;
                            historicalDataResponse = response.body();
                            WisSalesReport wisSalesReport = historicalDataResponse.getWisSalesReport();
                            SalesDetails salesDetails = wisSalesReport.getSalesDetails();

                            if (historicalDataResponse.getWisSalesReport().getSalesDetails() != null) {
                                totalSales.setText(historicalDataResponse.getWisSalesReport().getSalesDetails().getTotalSales().toString());
                                totalAmount.setText(CommonConstant.TAKA_STRING_FRONTSPACE+historicalDataResponse.getWisSalesReport().getSalesDetails().getTotalSalesAmount().toString());

                                for (int i = 0; i < salesDetails.getSales().size(); i++) {
                                    float x = salesDetails.getSales().get(i).getDownpayment();
                                    float y = salesDetails.getSales().get(i).getLoanid();

                                    if (y != 0f) {
                                        pricesHighCurrentReport.add(new Entry(x, salesDetails.getSales().get(i).getDownpayment()));
                                    }else {
                                        pricesHighCurrentReport.clear();
                                    }
                                }
                            } else {
                                recyclerView.setAdapter(null);
                                salesReportList.clear();
                                pricesHighCurrentReport.clear();
                                setLineChartDataCurrentReport(null);
                                totalSales.setText("");
                                totalAmount.setText("");
                                Toast.makeText(getApplicationContext(), "No Sales Report Found", Toast.LENGTH_LONG).show();
                            }


                            List<SalesReport> salesReportList = salesDetails.getSales();
                            if (salesReportList.size() > 0) {
                                recyclerView.setAdapter(new SalesReportAdapter(AgentSalesReportActivity.this, salesReportList));
                            }

                            Comparator<Entry> comparator = new Comparator<Entry>() {
                                @Override
                                public int compare(Entry o1, Entry o2) {
                                    return Float.compare(o1.getX(), o2.getX());
                                }
                            };
                            pricesHighCurrentReport.sort(comparator);
                            setLineChartDataCurrentReport(pricesHighCurrentReport);

                        }else {
                            Toasty.success(AgentSalesReportActivity.this, "Response Body: Response Body Null !!");
                        }
                    } else {
                        Toasty.success(AgentSalesReportActivity.this, "isSuccessful: Response Not Successful !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @SuppressLint("CheckResult")
            @Override
            public void onFailure(Call<HistoricalDataResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toasty.success(AgentSalesReportActivity.this, "onFailure: Server Error !!!");
            }
        });
    }


    private void setLineChartData(ArrayList<Entry> pricesHigh) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.linear_gradient_for_grapth);


        //if (highCheckBox.isChecked()) {
            LineDataSet highLineDataSet = new LineDataSet(pricesHigh, "Down Payment");
            highLineDataSet.setDrawCircles(true);
            highLineDataSet.setLineWidth(3);
            highLineDataSet.setCircleRadius(6);
            highLineDataSet.setCircleHoleRadius(4);
            highLineDataSet.setDrawValues(true);
            highLineDataSet.setValueTextColor(Color.RED);
            highLineDataSet.setValueTextSize(10);
            highLineDataSet.setDrawVerticalHighlightIndicator(true);
            highLineDataSet.setDrawFilled(true);
            highLineDataSet.setColors(Color.RED);
            highLineDataSet.setFillDrawable(drawable);
            highLineDataSet.setCircleColor(Color.RED);
            highLineDataSet.setCircleHoleColor(Color.WHITE);
            highLineDataSet.setDrawCircleHole(true);
            highLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSets.add(highLineDataSet);
        //}

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    private void setLineChartDataCurrentReport(ArrayList<Entry> pricesHighCurrentReport) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.linear_gradient_for_grapth);


        //if (highCheckBox.isChecked()) {
            LineDataSet highLineDataSet = new LineDataSet(pricesHighCurrentReport, "Down Payment");
            highLineDataSet.setDrawCircles(true);
            highLineDataSet.setLineWidth(3);
            highLineDataSet.setCircleRadius(6);
            highLineDataSet.setCircleHoleRadius(4);
            highLineDataSet.setDrawValues(true);
            highLineDataSet.setValueTextColor(Color.RED);
            highLineDataSet.setValueTextSize(10);
            highLineDataSet.setDrawVerticalHighlightIndicator(true);
            highLineDataSet.setDrawFilled(true);
            highLineDataSet.setColors(Color.RED);
            highLineDataSet.setFillDrawable(drawable);
            highLineDataSet.setCircleColor(Color.RED);
            highLineDataSet.setCircleHoleColor(Color.WHITE);
            highLineDataSet.setDrawCircleHole(true);
            highLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSets.add(highLineDataSet);
        //}

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


}


