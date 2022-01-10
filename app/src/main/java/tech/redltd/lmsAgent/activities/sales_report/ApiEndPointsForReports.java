package tech.redltd.lmsAgent.activities.sales_report;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiEndPointsForReports {
    @Headers({
            "Module: c2hhcmlmdWw=",
            "Content-Type: application/json",
            "Authorization: Basic MTg1NDc4NDUxMjpGYTEyMzQ1Njc4OQ==",
            "x-Auth-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZ2VudElEIejoyLCJtb2JpbGUiOiIxOTEyNjEwODk5IiwiZmlyc3RuYW1lIjoiU2hhcmlmdWwiLCJvdHAiOjMwMTk1OCwiaWF0IjoxNTcwODU5NTYwfQ.k8ICcAyzAOqlbmgW0N-kr8lUMwcMLDOnTIFEbku0PAs",
    })
    @POST("api/API_AgentMobile/wicsalesreport")
    Call<HistoricalDataResponse> getHistoricalData(@Body Map<String, Object> json);
}
