package com.example.reqresapi.model.network;

import com.example.reqresapi.model.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface defining the API endpoints for interacting with the server.
 * This interface is used by Retrofit to generate the necessary code for making HTTP requests.
 */
public interface ApiService {

    /**
     * Fetches a list of users from the server, with pagination support.
     *
     * @param page The page number to fetch.
     * @return A Call object containing the UserResponse, which includes user data and pagination information.
     */
    @GET("api/users")
    Call<UserResponse> getUsers(@Query("page") int page);

}

