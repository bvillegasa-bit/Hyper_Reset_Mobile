package com.hyperreset.app.data.api;

import com.hyperreset.app.data.model.LoginRequest;
import com.hyperreset.app.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<LoginResponse> register(@Body LoginRequest request);
}
