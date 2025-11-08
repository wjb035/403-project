package com.example.drawingapp.loginscreen.network;

import com.example.drawingapp.loginscreen.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// 1. app retrieves info
// 2. adds it to the user object (serialized to JSON)
// 3. send user object to the backend java server (via retrofit)

public interface ApiService {
    @POST("/api/users/register")
    Call<User> register(@Body User user);

    @POST("/api/users/login")
    Call<User> login(@Body Map<String, String> credentials);
}
