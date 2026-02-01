package com.project.shiensys;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login.php")
    Call<LoginResponse> login(
            @Body LoginRequest body
    );

    @POST("tickets/create.php")
    Call<ApiResponse> createTicket(
            @Body TicketRequest body
    );

    @GET("tickets/get_requests.php")
    Call<RequestsResponse> getRequests(
            @Query("status") String status
    );

    @GET("tickets/get_ticket_detail.php")
    Call<TicketDetailResponse> getTicketDetail(
            @Query("ticket_no") String ticketNo
    );
}
